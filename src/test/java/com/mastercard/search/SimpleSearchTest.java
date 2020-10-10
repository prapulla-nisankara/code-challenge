package com.mastercard.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mastercard.exception.CityLoadException;

/**
 * 
 * This is to Test Search algorithm
 * @author Prapulla Nisankara
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class SimpleSearchTest {
	
	Map<String, HashSet<String>> cityMap = new HashMap<>();
	
	SimpleSearch simpleSearch = new SimpleSearch();
	
	/**
	 * Creates city connector resource before executing the tests. 
	 * @throws IOException
	 * @throws CityLoadException	
	 * 			101, "Resource city.txt not found"
	 * 			102, "Reading City resource file failed"
	 * 			103, "Load resource went smooth. But No cities found"
	 */
	@BeforeAll
	void createCityConnector() throws IOException, CityLoadException {
		cityMap.put("New York", (HashSet<String>) Stream.of("Boston").collect(Collectors.toSet())); 
		cityMap.put("San Antonio", (HashSet<String>) Stream.of("Austin", "Houston").collect(Collectors.toSet())); 
		cityMap.put("Newark", (HashSet<String>) Stream.of("Philadelphia", "Boston").collect(Collectors.toSet()));
		cityMap.put("Trenton", (HashSet<String>) Stream.of("Albany").collect(Collectors.toSet()));
		cityMap.put("Fort Worth", (HashSet<String>) Stream.of("Dallas").collect(Collectors.toSet())); 
		cityMap.put("Austin", (HashSet<String>) Stream.of("Dallas", "San Antonio").collect(Collectors.toSet())); 
		cityMap.put("Dallas", (HashSet<String>) Stream.of("Fort Worth", "Austin").collect(Collectors.toSet())); 
		cityMap.put("Philadelphia", (HashSet<String>) Stream.of("Newark").collect(Collectors.toSet()));
		cityMap.put("Boston", (HashSet<String>) Stream.of("New York", "Newark").collect(Collectors.toSet())); 
		cityMap.put("Albany", (HashSet<String>) Stream.of("Trenton").collect(Collectors.toSet()));
		cityMap.put("Houston", (HashSet<String>) Stream.of("San Antonio").collect(Collectors.toSet()));
		System.out.print(cityMap);
	}

	
	/**
	 * Test cities are connected
	 */
	@Test
	@Order(2)
	void testCityConnector() {		     
        Assertions.assertTrue(simpleSearch.isCitiesConnected("Boston", "Newark", cityMap));		
        Assertions.assertTrue(simpleSearch.isCitiesConnected("Boston", "Philadelphia", cityMap));		
        Assertions.assertFalse(simpleSearch.isCitiesConnected("Philadelphia", "Albany", cityMap));		
        Assertions.assertTrue(simpleSearch.isCitiesConnected("Fort Worth", "Dallas", cityMap));		
        Assertions.assertTrue(simpleSearch.isCitiesConnected("Fort Worth", "Austin", cityMap));		
        Assertions.assertTrue(simpleSearch.isCitiesConnected("Fort Worth", "San Antonio", cityMap));		
        Assertions.assertTrue(simpleSearch.isCitiesConnected("Fort Worth", "Houston", cityMap));		
        Assertions.assertFalse(simpleSearch.isCitiesConnected("Fort Worth", "Boston", cityMap));		
        
        Assertions.assertFalse(simpleSearch.isCitiesConnected("", "", cityMap));
        Assertions.assertFalse(simpleSearch.isCitiesConnected("", "Boston", cityMap));
        Assertions.assertFalse(simpleSearch.isCitiesConnected("Fort Worth", "", cityMap));
	}
}
