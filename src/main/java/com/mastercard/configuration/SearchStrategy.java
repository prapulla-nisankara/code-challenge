package com.mastercard.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import com.mastercard.search.FindConnctedCities;
import com.mastercard.search.SimpleSearch;
import com.mastercard.search.multithread.SearchWithCallable;
import com.mastercard.search.multithread.SearchWithFork;

/**
 * @author Prapulla Nisankara
 * 
 * Configuration class that return SearchStrategy based on  city.search property
 *
 */

@Configuration
@PropertySource("classpath:application.properties")
public class SearchStrategy {
	private static final Logger logger = LoggerFactory.getLogger(SearchStrategy.class);
	
	private static String searchCallableStr = "searchWithCallable";

	private static String searchForkStr = "searchWithFork";

	/**
	 * Check properties file for configured value
	 */
	@Value("${city.search}")
	private String seatchStratagy;

	@Bean
	@Primary
	public FindConnctedCities getSearchStrategy() {
		logger.debug("Use seatchStratagy------------------------->"+seatchStratagy);
		if (searchCallableStr.equalsIgnoreCase(seatchStratagy)) {
			logger.debug("Use SearchWithCallable");
			return new SearchWithCallable();
		} else if (searchForkStr.equalsIgnoreCase(seatchStratagy)) {
			logger.debug("Use SearchWithFork");
			return new SearchWithFork();
		} else {
			logger.debug("Use SimpleSearch");
			return new SimpleSearch();
		}
	}
}
