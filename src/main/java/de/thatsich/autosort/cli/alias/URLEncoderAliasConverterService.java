package de.thatsich.autosort.cli.alias;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class URLEncoderAliasConverterService {
	public String encode(Map<String, String> mapping) throws UnsupportedEncodingException {
		final StringBuilder stringBuilder = new StringBuilder();

		for (Map.Entry<String, String> entry : mapping.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();

			if (stringBuilder.length() > 0) {
				stringBuilder.append("&");
			}

			stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
			stringBuilder.append("=");
			stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
		}

		return stringBuilder.toString();
	}

	public Map<String, String> decode(String encoded) throws UnsupportedEncodingException {
		final Map<String, String> map = new HashMap<>();
		final String[] nameValuePairs = encoded.split("&");

		for (String nameValuePair : nameValuePairs) {
			final String[] nameValue = nameValuePair.split("=");
			final String decodedKey = URLDecoder.decode(nameValue[0], "UTF-8");
			final String decodedValue = nameValue.length > 1 ? URLDecoder.decode(nameValue[1], "UTF-8") : "";
			map.put(decodedKey, decodedValue);
		}

		return map;
	}
}
