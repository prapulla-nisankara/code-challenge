package com.mastercard.search;

import java.util.HashSet;
import java.util.Map;

public interface FindConnctedCities {
	public boolean isCitiesConnected(String originCity, String destinationCity, final Map<String, HashSet<String>> cityRepo);
}
