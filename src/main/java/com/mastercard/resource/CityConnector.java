package com.mastercard.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * @author Prapulla Nisankara
 * 
 * Component that load's origin and destination city pairs from properties file city.txt
 * 
 * provides one public method findCityPair to check whether two cities are connected or not
 *
 */
@Component
@Scope("singleton")
public class CityConnector{
	
	
	/**
	 * Map<OriginCity, ArrayList<CitiesConnectedByRoad>>
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
	 * Trenton=[Albany], Fort Worth=[Dallas], 
	 * Austin=[Dallas, San Antonio], 
	 * Dallas=[Fort Worth, Austin], 
	 * Philadelphia=[Newark], 
	 * Boston=[New York, Newark], 
	 * Albany=[Trenton], 
	 * Houston=[San Antonio]
	 * }
	 * 
	 * @param resourceLoader
	 */
	@Autowired
	public CityConnector(ResourceLoader resourceLoader) {
		System.out.println(resourceLoader);
		Resource resource = resourceLoader.getResource("classpath:city.txt");	
		if (resource.exists() ) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
				String line;
			    while((line = reader.readLine()) != null)
			    {
			    	//Expecting the file is properly formated and contains two city pair separated by comma per line. 
			    	System.out.println(line);
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
		    	//TODO need proper logging and exit process
		    	System.out.println(e);
		    }
		}
		else {
			//TODO better handling of not finding resources
			System.out.println("No Resource");
		}
		System.out.println(myMap);		
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
	
	
	/**
	 * Make sure origin and destination city exists. And then traverse to find the connected city
	 * @param originCity
	 * @param destinationCity
	 * @return <tt>true</tt> if Origin and Designation cities connected by Road
	 */
	public boolean findCityPair(String originCity, String destinationCity) {		
		Set<String> inspectedKeys = new HashSet<>(); 
		//Make sure origin and destination city exists
		if(myMap.containsKey(originCity) && myMap.containsKey(destinationCity)) 
		{
			return findCityPair(originCity, destinationCity, inspectedKeys);
		}
		return false;	
	}
	
	/**
	 * Start your road trip from the Origin city and don't stop until you reach all the cities or no more cities left.
	 * 
	 * Attempt for a direct match. Not found. Never mind. Find the route via other connected cities.
	 * 
	 * @param originCity
	 * @param destinationCity
	 * @param inspectedCityKeys The cities that were already inspected. This will help break the recursive loop 
	 * @return <tt>true</tt> if Origin and destination cities connected by Road
	 */
	private boolean findCityPair(String originCity, String destinationCity, Set<String> inspectedCityKeys)
	{	
		//Retrieve cities that were connected by Road
		HashSet<String> aList1 = myMap.get(originCity);
		System.out.println("originCity:"+originCity+" -> "+aList1 + " destinationCity -> "+destinationCity);			
		
		//A direct Match. Hurry. return true
		if(aList1.contains(destinationCity)) {
			System.out.println("Found");
			return true;
		}
		//Not found. Now retrieved cities will become origin cities. Our destination remains same.
		inspectedCityKeys.add(originCity);			
		for (String nextConnectedCity : aList1) {
			//Don't inspect the city that was already inspected. 
			//Let's begin the journey. fasten the seat belts and don't stop until all cities were visited. 
			if (!inspectedCityKeys.contains(nextConnectedCity)
					&& (findCityPair(nextConnectedCity, destinationCity, inspectedCityKeys))) {
				return true;
			}
		}
		//Oops Not found. Never mind. Demand better roads.
		return false;
	}
	
}
