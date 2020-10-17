package com.mastercard.resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mastercard.search.CityConnectionSearch;
import com.mastercard.search.SimpleSearch;
import com.mastercard.search.multithread.SearchWithCallable;
import com.mastercard.search.multithread.SearchWithFork;

/**
 * @author Prapulla Nisankara
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class CodeChallengeApplicationTests {
	
	@Mock
	CityConnector cityConnector;
	
	@Mock
	SimpleSearch simpleSearch;
	
	@Mock
	SearchWithCallable searchWithCallable;
	
	@Mock
	SearchWithFork searchWithFork;
	
	@InjectMocks
	CityConnectionSearch CityConnectionSearch;	
	
	/**
	 * Test city names exists in CityConntector
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@Test
	@Order(1)
	void testCityConnectorResource() throws NoSuchFieldException, SecurityException {			
		Assertions.assertFalse(CityConnectionSearch.findCityPair("", ""));		
		Assertions.assertFalse(CityConnectionSearch.findCityPair("Boston", "Newark"));
		
		FieldSetter.setField(CityConnectionSearch, CityConnectionSearch.getClass().getDeclaredField("seatchStratagy"), "searchWithCallable");
		
		Mockito.when(cityConnector.isCitiesConnected("Boston", "Newark", searchWithCallable)).thenReturn(true);
		Assertions.assertTrue(CityConnectionSearch.findCityPair("Boston", "Newark"));
		
		FieldSetter.setField(CityConnectionSearch, CityConnectionSearch.getClass().getDeclaredField("seatchStratagy"), "searchWithFork");
		Mockito.when(cityConnector.isCitiesConnected("Boston", "Newark", searchWithFork)).thenReturn(true);
		Assertions.assertTrue(CityConnectionSearch.findCityPair("Boston", "Newark"));
		
		FieldSetter.setField(CityConnectionSearch, CityConnectionSearch.getClass().getDeclaredField("seatchStratagy"), "");
		Mockito.when(cityConnector.isCitiesConnected("Boston", "Newark", simpleSearch)).thenReturn(true);
		Assertions.assertTrue(CityConnectionSearch.findCityPair("Boston", "Newark"));
		
	}
	
	
}
