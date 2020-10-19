/**
 * 
 */
package com.mastercard.search;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Prapulla Nisakara
 * Traverse from root node to child nodes all the way down until connected city was found
 *
 */
@Component("simpleSearch")
@Scope("singleton")
public class SimpleSearch implements FindConnctedCities {
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleSearch.class);

	
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
			return findCityPair(originCity, destinationCity, inspectedKeys, cityRepo);
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
	private boolean findCityPair(String originCity, String destinationCity, Set<String> inspectedCityKeys, final Map<String, HashSet<String>> cityRepo)
	{	
		//Retrieve cities that were connected by Road
		HashSet<String> connectedCities = cityRepo.get(originCity);
		logger.debug("originCity:"+originCity+" -> "+connectedCities + " destinationCity -> "+destinationCity);			
		
		//A direct Match. Hurry. return true
		if(connectedCities.contains(destinationCity)) {
			logger.debug("Both the cities were connected");
			return true;
		}
		//Not found. Now retrieved cities will become origin cities. Our destination remains same.
		inspectedCityKeys.add(originCity);	
		return connectedCities.stream().anyMatch(c -> !inspectedCityKeys.contains(c) && findCityPair(c, destinationCity, inspectedCityKeys, cityRepo)  );
	}

}
