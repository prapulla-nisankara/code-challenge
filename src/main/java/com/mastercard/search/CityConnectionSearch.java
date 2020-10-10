package com.mastercard.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.mastercard.resource.CityConnector;

@Component
public class CityConnectionSearch {
	
	/**
	 * Easy to inject city pair repo
	 */
	@Autowired
	private CityConnector cityConnector;
	
	/**
	 * Easy to inject alternate search algorithm
	 */
	@Autowired
	@Qualifier("simpleSearch")
	private SimpleSearch simpleSearch;
	
	public boolean findCityPair(String originCity, String destinationCity) {
		return cityConnector.isCitiesConnected(originCity, destinationCity, simpleSearch);
	}
}
