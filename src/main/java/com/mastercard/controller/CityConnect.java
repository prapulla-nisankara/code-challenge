package com.mastercard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mastercard.resource.CityConnector;

/**
 * @author Prapulla Nisankara
 *
 */
@RestController
@RequestMapping("connected")
public class CityConnect {

	@Autowired
	private CityConnector cityConnector;

	@RequestMapping( method = RequestMethod.GET)
    public String citiesConnected(@RequestParam("origin") String originCity, @RequestParam("destination") String destinationCity) {
		if(!originCity.isEmpty() && !destinationCity.isEmpty() && cityConnector.findCityPair(originCity, destinationCity)) {
			return "Yes";
		}
        return "No";
    }
	
}
