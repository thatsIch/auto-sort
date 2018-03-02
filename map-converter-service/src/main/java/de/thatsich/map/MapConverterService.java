package de.thatsich.map;

import java.util.Map;

public interface MapConverterService {
	String encode(Map<String, String> mapping);

	Map<String, String> decode(String encoded);
}
