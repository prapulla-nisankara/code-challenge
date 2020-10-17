/**
 * 
 */
package com.mastercard.search.multithread;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mastercard.search.FindConnctedCities;

/**
 * Multithreading approach to find connected cities
 * @author Prapulla Nisakara
 *
 */
@Component("searchWithCallable")
@Scope("singleton")
public class SearchWithCallable implements FindConnctedCities {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchWithCallable.class);
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

	
	/**
	 * Make sure origin and destination city exists. And then traverse to find the connected city
	 * @param originCity
	 * @param destinationCity
	 * @param Map<String, HashSet<String>> cityRepo
	 * @return <tt>true</tt> if Origin and Designation cities connected by Road
	 */
	@Override
	public boolean isCitiesConnected(String originCity, String destinationCity, final Map<String, HashSet<String>> cityRepo) {
		Set<String> inspectedKeys = new HashSet<>(); 
		//Make sure origin and destination city exists
		if(cityRepo.containsKey(originCity) && cityRepo.containsKey(destinationCity)) 
		{
			AtomicBoolean found = new AtomicBoolean();
			return findCityPair(originCity, destinationCity, inspectedKeys, found, cityRepo);
		}
		return false;
	}
	
	/**
	 * Start your road trip from the Origin city.
	 * 
	 * Attempt for a direct match. Not found. Never mind. Find the route via other connected cities.
	 * 
	 * Check if the destination is directly connected.
	 * If not directly connected find the path via route. 
	 * don't stop until you reach destination covering all the cities.
	 * Avoid the cities that were visited
	 * 
	 * @param originCity
	 * @param destinationCity
	 * @param inspectedCityKeys The cities that were already inspected. This will help break the recursive loop 
	 * @return <tt>true</tt> if Origin and destination cities connected by Road
	 */
	private boolean findCityPair(String originCity, String destinationCity, Set<String> inspectedCityKeys, AtomicBoolean found, final Map<String, HashSet<String>> cityRepo)
	{	
		//Retrieve cities that were connected by Road
		HashSet<String> connectedCities = cityRepo.get(originCity);
		logger.debug("originCity:"+originCity+" -> "+connectedCities + " destinationCity -> "+destinationCity);			
		
		//found.get() ture means some other thread foudd it. just return true. otherwise check for direct Match. Hurry. return true
		if(found.get() || connectedCities.contains(destinationCity)) {
			logger.debug("Both the cities were connected");
			found.compareAndSet(false, true);
			return found.get();
		}
		//Not found. Now retrieved cities will become origin cities. Our destination remains same.			
		if(connectedCities.size()==1) {
			//Use the same thread
			lock.writeLock().lock();
			inspectedCityKeys.add(originCity);
			lock.writeLock().unlock();
			return connectedCities.stream().anyMatch(c -> !inspectedCityKeys.contains(c) && findCityPair(c, destinationCity, inspectedCityKeys, found, cityRepo)  );
		}
		
		ExecutorService ex = Executors.newFixedThreadPool(connectedCities.size());
		ExecutorCompletionService<Boolean> ecs = new ExecutorCompletionService<>(ex);
		
		connectedCities.stream().forEach(orgCity -> {
			lock.writeLock().lock();
			inspectedCityKeys.add(originCity);
			lock.writeLock().unlock();
			ecs.submit(() -> {
				lock.readLock().lock();
				boolean cityInspected = inspectedCityKeys.contains(orgCity);
				lock.readLock().unlock();
				if (!found.get() && !cityInspected) {
					return findCityPair(orgCity, destinationCity, inspectedCityKeys, found, cityRepo);
				}
				return false;
			});
		});

		try {			
			int workerCount = connectedCities.size();
			while (workerCount > 0) {
				boolean isCitiesConnected = ecs.take().get();
				if(isCitiesConnected) {					
					return true;
				}
				workerCount--;	
			}			
		} catch (InterruptedException | ExecutionException e) {				
			e.printStackTrace();
			return false;
		}finally {
			ex.shutdown();
		}
		return false;
	}

}
