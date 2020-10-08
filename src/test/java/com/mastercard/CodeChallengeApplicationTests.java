package com.mastercard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.mastercard.resource.CityConnector;

/**
 * @author Prapulla Nisankara
 *
 */
@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class CodeChallengeApplicationTests {

	@Mock
	ResourceLoader resourceLoader;
	
	@Mock
	Resource mockResource;
	
	@Test
	void testCityConnector() throws IOException {		

		String mockFile = "Boston, New York\n" + 
							"Philadelphia, Newark\n" + 
							"Newark, Boston\n" + 
							"Trenton, Albany\n" + 
							"Fort Worth, Dallas\n" + 
							"Dallas, Austin\n" + 
							"Austin, San Antonio\n" + 
							"San Antonio, Houston";
		InputStream is = new ByteArrayInputStream(mockFile.getBytes());          
                
        Mockito.when(resourceLoader.getResource(Mockito.anyString())).thenReturn(mockResource);
        Mockito.when(mockResource.exists()).thenReturn(true);       
        Mockito.when(mockResource.getInputStream()).thenReturn(is);
               
        CityConnector cityConnector = new CityConnector(resourceLoader);
        
        Mockito.verify(resourceLoader).getResource(Mockito.anyString());
        Mockito.verify(mockResource).exists();
        Mockito.verify(mockResource).getInputStream();        
        
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
