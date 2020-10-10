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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.mastercard.exception.CityLoadException;

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
	
	/**
	 * Test city names exists in CityConntector
	 */
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
	
	/**
	 * Test cities are connected
	 */
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
	
	/**
	 * Test 102, "Reading City resource file failed"
	 * @throws IOException
	 */
	@Test
	@Order(3)
	void testCityConnectorResourcIOException() throws IOException {	
		ResourceLoader resourceLoader =  Mockito.mock(ResourceLoader.class);		
		Resource mockResource = Mockito.mock(Resource.class);  
		
		Mockito.when(resourceLoader.getResource(Mockito.anyString())).thenReturn(mockResource);
		Mockito.when(mockResource.exists()).thenReturn(true);       
		Mockito.when(mockResource.getInputStream()).thenThrow(IOException.class);
		Assertions.assertThrows(CityLoadException.class, ()-> new CityConnector(resourceLoader));
		
		try {
			 new CityConnector(resourceLoader);
		}
		catch (CityLoadException e) 
		{
			Assertions.assertEquals(102, e.getExitCode());
		}
	}
	
	/**
	 * Test CityLoadException  101, "Resource city.txt not found"
	 * 			
	 * @throws IOException
	 * @throws CityLoadException   
	 * 			101, "Resource city.txt not found"
	 * 			102, "Reading City resource file failed"
	 * 			103, "Load resource went smooth. But No cities found"
	 */
	@Test
	@Order(4)
	void testCityConnectorResourcNotExists() throws IOException, CityLoadException {	
	
		ResourceLoader resourceLoader =  Mockito.mock(ResourceLoader.class);		
		Resource mockResource = Mockito.mock(Resource.class);  
		
		Mockito.when(resourceLoader.getResource(Mockito.anyString())).thenReturn(mockResource);
		Mockito.when(mockResource.exists()).thenReturn(false);       
		   
		Assertions.assertThrows(CityLoadException.class, ()-> new CityConnector(resourceLoader));
		try {
			 new CityConnector(resourceLoader);
		}
		catch (CityLoadException e) 
		{
			Assertions.assertEquals(101, e.getExitCode());
		}
	}
	
	/**
	 * Test CityLoadException 103, "Load resource went smooth. But No cities found"
	 * @throws IOException
	 * @throws CityLoadException 
	 * 			101, "Resource city.txt not found"
	 * 			102, "Reading City resource file failed"
	 * 			103, "Load resource went smooth. But No cities found" 
	 */
	@Test
	@Order(5)
	void testCityConnectorResourcNoContent() throws IOException, CityLoadException {	
		String mockFile = "";
		InputStream is = new ByteArrayInputStream(mockFile.getBytes());          
		
		ResourceLoader resourceLoader =  Mockito.mock(ResourceLoader.class);		
		Resource mockResource = Mockito.mock(Resource.class);  
		
		Mockito.when(resourceLoader.getResource(Mockito.anyString())).thenReturn(mockResource);
		Mockito.when(mockResource.exists()).thenReturn(true);       
		Mockito.when(mockResource.getInputStream()).thenReturn(is);
		   
		Assertions.assertThrows(CityLoadException.class, ()-> new CityConnector(resourceLoader));
		try {
			 new CityConnector(resourceLoader);
		}
		catch (CityLoadException e) 
		{
			Assertions.assertEquals(103, e.getExitCode());
		}
	}

}
