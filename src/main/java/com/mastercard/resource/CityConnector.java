package com.mastercard.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.mastercard.exception.CityLoadException;
import com.mastercard.search.FindConnctedCities;

/**
 * @author Prapulla Nisankara
 * 
 * Component that load's origin and destination city pairs from properties file city.txt
 * 
 * provides one public method findCityPair to check whether two cities are connected or not
 * 
 * search visitor to find the connected cities
 * 
 * City Names are case sensitive
 * TODO city name's should have been case insensitive 
 *
 */
@Component
@Scope("singleton")
public class CityConnector{
	
	private static final Logger logger = LoggerFactory.getLogger(CityConnector.class);

	
	/**
	 * Map<OriginCity, HashSet<CitiesConnectedByRoad>>
	 */
	Map<String, HashSet<String>> myMap = new HashMap<>();
	
	/**
	 * Construct a map with origin city and it's directly connected destination cities.
	 * 
	 * If city A is connected to B then created bidirectional mapping.
	 * This approach has duplication of data. But better retrieval rate
	 * 
	 * city.txt
	 * 
	 * Boston, New York
	 * Philadelphia, Newark
	 * Newark, Boston
	 * Trenton, Albany
	 * Fort Worth, Dallas
	 * Dallas, Austin
	 * Austin, San Antonio
	 * San Antonio, Houston
	 * 
	 * Boston, New York. This is turned into two entries Boston=[New York, Newark] and New York=[Boston]
	 * Note:- Boston also connected to Newark hence Boston=[New York, Newark]
	 * 
	 * {
	 * New York=[Boston], 
	 * San Antonio=[Austin, Houston], 
	 * Newark=[Philadelphia, Boston], 
	 * Trenton=[Albany], 
	 * Fort Worth=[Dallas], 
	 * Austin=[Dallas, San Antonio], 
	 * Dallas=[Fort Worth, Austin], 
	 * Philadelphia=[Newark], 
	 * Boston=[New York, Newark], 
	 * Albany=[Trenton], 
	 * Houston=[San Antonio]
	 * }
	 * 
	 * @param resourceLoader
	 * @throws CityLoadException 
	 */
	@Autowired
	public CityConnector(ResourceLoader resourceLoader) throws CityLoadException {
		logger.debug("Start loading city resource");
		Resource resource = resourceLoader.getResource("classpath:city.txt");	
		if (resource.exists() ) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
				String line;
			    while((line = reader.readLine()) != null)
			    {
			    	//Expecting the file is properly formated and contains two city pair separated by comma per line. 
			    	logger.debug(line);
			    	String[] values = line.split(",");
			    	if(values.length>1)
			    	{
				    	String city1 = values[0].trim();
				    	String city2 = values[1].trim();
				    	//City A is connected to B, implies B is connected to A. create bidirectional pair
				    	//trade off between size of the map and retrieval iterations
				    	if(!city1.isEmpty() && !city2.isEmpty())
				    	{
				    		addCityPair(city1, city2);
				    		addCityPair(city2, city1);
				    	}
			    	}
			    }
		    } 
		    catch (IOException e) 
		    {
		    	logger.error("City resource loaded failed");
		    	logger.error(e.getStackTrace().toString());
		    	throw new CityLoadException(102, "Reading City resource file failed");
		    }
		}
		else {
			logger.error("Resource city.txt not found");
			throw new CityLoadException(101, "Resource city.txt not found");
		}		
		logger.debug(myMap.toString());		
		if(myMap.size()>0)
		{
			logger.info("Loading city resource success");
		}
		else
		{
			logger.info("Load resouce went smooth. But cities found");
			throw new CityLoadException(103, "Load resource went smooth. But No cities found");
		}
	}		
	
	private void addCityPair(String city1, String city2) {
		if(!myMap.containsKey(city1)) {
			HashSet<String> aHashSet = new HashSet<>();
			aHashSet.add(city2);
			myMap.put(city1, aHashSet);
		}else {
			HashSet<String> aHashSet = myMap.get(city1);
			if(!aHashSet.contains(city2)){
				aHashSet.add(city2);
			}			
		}
	}
	
	public boolean isCitiesConnected(String originCity, String destinationCity, FindConnctedCities cityFinder) {
		return cityFinder.isCitiesConnected(originCity, destinationCity, myMap);
	}
	
	
	/**
	 * Make sure origin and destination city exists. And then traverse to find the connected city
	 * @param originCity
	 * @param destinationCity
	 * @return <tt>true</tt> if Origin and Designation cities connected by Road
	 */
	/*public boolean findCityPair(String originCity, String destinationCity) {		
		Set<String> inspectedKeys = new HashSet<>(); 
		//Make sure origin and destination city exists
		if(myMap.containsKey(originCity) && myMap.containsKey(destinationCity)) 
		{
			return findCityPair(originCity, destinationCity, inspectedKeys);
		}
		return false;	
	}*/
	
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
	/*private boolean findCityPair(String originCity, String destinationCity, Set<String> inspectedCityKeys)
	{	
		//Retrieve cities that were connected by Road
		HashSet<String> connectedCities = myMap.get(originCity);
		logger.debug("originCity:"+originCity+" -> "+connectedCities + " destinationCity -> "+destinationCity);			
		
		//A direct Match. Hurry. return true
		if(connectedCities.contains(destinationCity)) {
			logger.debug("Both the cities were connected");
			return true;
		}
		//Not found. Now retrieved cities will become origin cities. Our destination remains same.
		inspectedCityKeys.add(originCity);	
		return connectedCities.stream().anyMatch(c -> !inspectedCityKeys.contains(c) && findCityPair(c, destinationCity, inspectedCityKeys)  );
	}*/
	
}
