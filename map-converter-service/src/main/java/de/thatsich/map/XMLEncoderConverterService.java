package de.thatsich.map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class XMLEncoderConverterService implements MapConverterService {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String ENCODING = StandardCharsets.UTF_8.name();

	@Override
	public String encode(Map<String, String> mapping) {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final XMLEncoder xmlEncoder = new XMLEncoder(bos);
		xmlEncoder.writeObject(mapping);
		xmlEncoder.close();

		try {
			return bos.toString(ENCODING);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Could not encode information because of missing encoding '" + ENCODING + "'.", e);
			return "";
		}
	}

	@Override
	public Map<String, String> decode(String encoded) {
		final byte[] bytes = encoded.getBytes(StandardCharsets.UTF_8);
		final XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(bytes));

		//noinspection unchecked
		return (Map<String, String>) xmlDecoder.readObject();
	}
}
