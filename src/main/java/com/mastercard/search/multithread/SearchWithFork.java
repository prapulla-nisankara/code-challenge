/**
 * 
 */
package com.mastercard.search.multithread;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

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
@Component("searchWithFork")
@Scope("singleton")
public class SearchWithFork implements FindConnctedCities {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchWithFork.class);
	
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
			logger.debug("Initiate SearchCity RecursiveTask");
			inspectedKeys.add(originCity);
			SearchCityTask searchCityTask = new SearchCityTask(originCity, destinationCity, inspectedKeys, cityRepo);
			return ForkJoinPool.commonPool().invoke(searchCityTask);
		}
		return false;
	}
}
