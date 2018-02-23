package de.thatsich.autosort.cli;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class XMLEncoderConverterService {
	public String encode(Map<String, String> mapping) throws UnsupportedEncodingException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final XMLEncoder xmlEncoder = new XMLEncoder(bos);
		xmlEncoder.writeObject(mapping);
		xmlEncoder.close();

		return bos.toString(StandardCharsets.UTF_8.name());
	}

	public Map<String, String> decode(String encoded) {
		final byte[] bytes = encoded.getBytes(StandardCharsets.UTF_8);
		final XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(bytes));

		//noinspection unchecked
		return (Map<String, String>) xmlDecoder.readObject();
	}
}
