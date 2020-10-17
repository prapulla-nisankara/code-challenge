package com.mastercard.search.multithread;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Prapulla Nisankara
 * 
 * Create a recursive task to search connected cities.
 * It works like a Breadth-First search in a Multi Node tree.
 *
 */
public class SearchCityTask extends RecursiveTask<Boolean>{
	private static final long serialVersionUID = -5039326670520555492L;
	private static final Logger logger = LoggerFactory.getLogger(SearchCityTask.class);
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	
	private String originCity;
	private String destinationCity;
	private Set<String> inspectedCityKeys;
	private final Map<String, HashSet<String>> cityRepo;
	
	public SearchCityTask(String originCity, String destinationCity, Set<String> inspectedCityKeys, Map<String, HashSet<String>> cityRepo) {
		super();
		this.originCity = originCity;
		this.destinationCity = destinationCity;
		this.inspectedCityKeys = inspectedCityKeys;
		this.cityRepo = cityRepo;
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
	@Override
	protected Boolean compute() {
		//Retrieve cities that were connected by Road
		HashSet<String> connectedCities = cityRepo.get(originCity);
		logger.debug("originCity:"+originCity+" -> "+connectedCities + " destinationCity -> "+destinationCity);			
		
		//found.get() ture means some other thread foudd it. just return true. otherwise check for direct Match. Hurry. return true
		if(connectedCities.contains(destinationCity)) {
			logger.debug("Both the cities were connected");
			return true;
		}
		//Not found. Now retrieved cities will become origin cities. Our destination remains same.			
		List<ForkJoinTask<Boolean>>  tasks = new ArrayList<>();
		connectedCities.stream().forEach((newOrgCity) -> {
			lock.writeLock().lock();
			if(!inspectedCityKeys.contains(newOrgCity))
			{	
				inspectedCityKeys.add(newOrgCity);
				lock.writeLock().unlock();
				tasks.add(new SearchCityTask(newOrgCity, destinationCity, inspectedCityKeys, cityRepo).fork());
			}
			else
			{
				lock.writeLock().unlock();
			}			
		});
		return tasks.stream().parallel().anyMatch( task-> task.join());
	}
}
