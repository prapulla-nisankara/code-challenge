package com.mastercard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mastercard.search.CityConnectionSearch;

/**
 * @author Prapulla Nisankara
 *
 */
@RestController
@RequestMapping("connected")
public class CityConnect {
	
	@Autowired	
	private CityConnectionSearch cityConnectionSearch;

	/**
	 * @param originCity
	 * @param destinationCity
	 * @return Yes if cities are connected. otherwise No
	 */
	@RequestMapping( method = RequestMethod.GET)
    public String citiesConnected(@RequestParam(value = "origin", required = false) String originCity, @RequestParam(value = "destination", required = false) String destinationCity) {
		if (originCity != null && !originCity.isEmpty() && destinationCity != null && !destinationCity.isEmpty()
				&& cityConnectionSearch.findCityPair(originCity, destinationCity)) {
			return "Yes";
		}
        return "No";
    }
	
}
