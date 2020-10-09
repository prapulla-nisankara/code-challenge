package com.mastercard.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @author Prapulla Nisankara
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class CodeChallengeApplicationTests {
	
	CityConnector cityConnector;
	
	@BeforeAll
	void createCityConnector() throws IOException {
		String mockFile = "Boston, New York\n" + 
				"Philadelphia, Newark\n" + 
				"Newark, Boston\n" + 
				"Trenton, Albany\n" + 
				"Fort Worth, Dallas\n" + 
				"Dallas, Austin\n" + 
				"Austin, San Antonio\n" + 
				"San Antonio, Houston";
		InputStream is = new ByteArrayInputStream(mockFile.getBytes());          
		
		ResourceLoader resourceLoader =  Mockito.mock(ResourceLoader.class);		
		Resource mockResource = Mockito.mock(Resource.class);  
		
		Mockito.when(resourceLoader.getResource(Mockito.anyString())).thenReturn(mockResource);
		Mockito.when(mockResource.exists()).thenReturn(true);       
		Mockito.when(mockResource.getInputStream()).thenReturn(is);
		   
		cityConnector = new CityConnector(resourceLoader);
		
		Mockito.verify(resourceLoader).getResource(Mockito.anyString());
		Mockito.verify(mockResource).exists();
		Mockito.verify(mockResource).getInputStream();
	}
	
	@Test
	@Order(1)
	void testCityConnectorResource() {		
		Assertions.assertFalse(cityConnector.myMap.isEmpty());		
		Assertions.assertEquals(11, cityConnector.myMap.size());
		Assertions.assertTrue(cityConnector.myMap.containsKey("New York"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("San Antonio"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("Newark"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("Trenton"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("Fort Worth"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("Austin"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("Dallas"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("Philadelphia"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("Boston"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("Albany"));
		Assertions.assertTrue(cityConnector.myMap.containsKey("Houston"));
		
		Assertions.assertFalse(cityConnector.myMap.containsKey("Orlando"));
		
		Assertions.assertEquals(1, cityConnector.myMap.get("New York").size());
		Assertions.assertEquals(2, cityConnector.myMap.get("San Antonio").size());
		Assertions.assertEquals(2, cityConnector.myMap.get("Newark").size());
		Assertions.assertEquals(1, cityConnector.myMap.get("Trenton").size());
		Assertions.assertEquals(1, cityConnector.myMap.get("Fort Worth").size());
		Assertions.assertEquals(2, cityConnector.myMap.get("Austin").size());
		Assertions.assertEquals(2, cityConnector.myMap.get("Dallas").size());
		Assertions.assertEquals(1, cityConnector.myMap.get("Philadelphia").size());
		Assertions.assertEquals(2, cityConnector.myMap.get("Boston").size());
		Assertions.assertEquals(1, cityConnector.myMap.get("Albany").size());
		Assertions.assertEquals(1, cityConnector.myMap.get("Houston").size());
	}
	
	@Test
	@Order(2)
	void testCityConnector() {		     
        Assertions.assertTrue(cityConnector.findCityPair("Boston", "Newark"));		
        Assertions.assertTrue(cityConnector.findCityPair("Boston", "Philadelphia"));		
        Assertions.assertFalse(cityConnector.findCityPair("Philadelphia", "Albany"));		
        Assertions.assertTrue(cityConnector.findCityPair("Fort Worth", "Dallas"));		
        Assertions.assertTrue(cityConnector.findCityPair("Fort Worth", "Austin"));		
        Assertions.assertTrue(cityConnector.findCityPair("Fort Worth", "San Antonio"));		
        Assertions.assertTrue(cityConnector.findCityPair("Fort Worth", "Houston"));		
        Assertions.assertFalse(cityConnector.findCityPair("Fort Worth", "Boston"));		
        
        Assertions.assertFalse(cityConnector.findCityPair("", ""));
        Assertions.assertFalse(cityConnector.findCityPair("", "Boston"));
        Assertions.assertFalse(cityConnector.findCityPair("Fort Worth", ""));
	}

}
