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
import org.mockito.junit.jupiter.MockitoExtension;

import com.mastercard.search.CityConnectionSearch;
import com.mastercard.search.FindConnctedCities;

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
	FindConnctedCities findConnctedCities;
	
	@InjectMocks
	CityConnectionSearch cityConnectionSearch;	
	
	/**
	 * Test city names exists in CityConntector
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@Test
	@Order(1)
	void testCityConnectorResource() throws NoSuchFieldException, SecurityException {			
		Assertions.assertFalse(cityConnectionSearch.findCityPair("", ""));		
		Assertions.assertFalse(cityConnectionSearch.findCityPair("Boston", "Newark"));
				
		Mockito.when(cityConnector.isCitiesConnected("Boston", "Newark", findConnctedCities)).thenReturn(true);
		Assertions.assertTrue(cityConnectionSearch.findCityPair("Boston", "Newark"));		
	}	
	
}
