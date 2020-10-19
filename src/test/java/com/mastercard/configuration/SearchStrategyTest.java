package com.mastercard.configuration;

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
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mastercard.search.FindConnctedCities;

/**
 * @author Prapulla Nisankara
 * 
 * Test Configuration class SearchStrategy 
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class SearchStrategyTest {
	
	SearchStrategy searchStrategy = new SearchStrategy();
	
	@Test
	@Order(1)
	public void getSearchStrategyTest() throws NoSuchFieldException, SecurityException
	{		
		FindConnctedCities findConnctedCities = searchStrategy.getSearchStrategy();		
		Assertions.assertEquals("com.mastercard.search.SimpleSearch", findConnctedCities.getClass().getTypeName());   
		
		FieldSetter.setField(searchStrategy, searchStrategy.getClass().getDeclaredField("seatchStratagy"), "searchWithCallable");		
		Assertions.assertEquals("com.mastercard.search.multithread.SearchWithCallable", searchStrategy.getSearchStrategy().getClass().getTypeName());   
		
		FieldSetter.setField(searchStrategy, searchStrategy.getClass().getDeclaredField("seatchStratagy"), "searchWithFork");
		Assertions.assertEquals("com.mastercard.search.multithread.SearchWithFork", searchStrategy.getSearchStrategy().getClass().getTypeName());
		
	}
}
