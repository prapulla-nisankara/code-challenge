package com.mastercard.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.mastercard.exception.CityLoadException;
import com.mastercard.search.CityConnectionSearch;
import com.mastercard.search.SimpleSearch;

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
	
	@InjectMocks
	CityConnectionSearch CityConnectionSearch;	
	
	/**
	 * Test city names exists in CityConntector
	 */
	@Test
	@Order(1)
	void testCityConnectorResource() {			
		Assertions.assertFalse(CityConnectionSearch.findCityPair("", ""));		
		Assertions.assertFalse(CityConnectionSearch.findCityPair("Boston", "Newark"));
		
		Mockito.when(cityConnector.isCitiesConnected("Boston", "Newark", simpleSearch)).thenReturn(true);
		Assertions.assertTrue(CityConnectionSearch.findCityPair("Boston", "Newark"));
	}
	
	
}
