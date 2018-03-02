package de.thatsich.map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class URLEncoderConverterService implements MapConverterService {

	private static final String ENCODING = "UTF-8";
	private static final Logger LOGGER = LogManager.getLogger();

	public String encode(Map<String, String> mapping) {
		final StringBuilder stringBuilder = new StringBuilder();

		for (Map.Entry<String, String> entry : mapping.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();

			if (key.isEmpty()) {
				System.out.println("value = " + value);
				System.out.println("mapping = " + mapping.entrySet().size());
				System.out.println("mapping = " + mapping.keySet().size());

				throw new IllegalArgumentException("An empty key is not allowed. Please use some type-safe alternatives in case you require an empty key.");
			}

			if (stringBuilder.length() > 0) {
				stringBuilder.append("&");
			}

			try {
				stringBuilder.append(URLEncoder.encode(key, ENCODING));
				stringBuilder.append("=");
				stringBuilder.append(URLEncoder.encode(value, ENCODING));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("Could not encode information because of missing encoding '" + ENCODING + "'. Skipping entry '" + key + ", " + value + "'.", e);
			}
		}

		return stringBuilder.toString();
	}

	public Map<String, String> decode(String encoded) {
		final Map<String, String> map = new HashMap<>();
		final String[] nameValuePairs = encoded.split("&");

		for (String nameValuePair : nameValuePairs) {
			final String[] nameValue = nameValuePair.split("=");
			final String key = nameValue[0];
			if (!key.isEmpty()) {

				try {
					final String decodedKey = URLDecoder.decode(key, ENCODING);
					final String decodedValue = nameValue.length > 1 ? URLDecoder.decode(nameValue[1], ENCODING) : "";
					map.put(decodedKey, decodedValue);
				} catch (UnsupportedEncodingException e) {
					LOGGER.error("Could not decode information because of missing encoding '" + ENCODING + "'. Skipping entry '" + Arrays.toString(nameValue) + "'.", e);
				}
			}
		}

		return map;
	}
}
