package com.mastercard.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.mastercard.resource.CityConnector;
import com.mastercard.search.multithread.SearchWithCallable;
import com.mastercard.search.multithread.SearchWithFork;

@PropertySource("classpath:application.properties")
@Component
public class CityConnectionSearch {
	
	private static String searchCallableStr = "searchWithCallable";
	
	private static String searchForkStr = "searchWithFork";
	
	/**
	 * Easy to inject city pair repo
	 */
	@Autowired
	private CityConnector cityConnector;
	
	@Value("${city.search}")
	private String seatchStratagy;
	
	/**
	 * Easy to inject alternate search algorithm
	 */
	@Autowired
	@Qualifier("simpleSearch")
	private FindConnctedCities simpleSearch;
	
	@Autowired
	@Qualifier("searchWithCallable")
	private SearchWithCallable searchWithCallable;
	
	@Autowired
	@Qualifier("searchWithFork")
	private SearchWithFork searchWithFork;
	
	public boolean findCityPair(String originCity, String destinationCity) {
		if(searchCallableStr.equalsIgnoreCase(seatchStratagy))
		{
			return cityConnector.isCitiesConnected(originCity, destinationCity, searchWithCallable);
		}
		else if(searchForkStr.equalsIgnoreCase(seatchStratagy))
		{
			return cityConnector.isCitiesConnected(originCity, destinationCity, searchWithFork);
		}
		else
		{
			return cityConnector.isCitiesConnected(originCity, destinationCity, simpleSearch);
		}
	}
}
