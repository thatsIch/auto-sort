package de.thatsich.autosort.cli;

import de.thatsich.autosort.cli.XMLEncoderConverterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

class XMLEncoderConverterServiceTest {

	@Test
	void reverse() throws UnsupportedEncodingException {
		final XMLEncoderConverterService converterService = new XMLEncoderConverterService();
		final Map<String, String> decoded = new HashMap<>();
		decoded.put("anime", "D:\\Download\\Anime");
		decoded.put("love", "D:\\Love Love");

		Assertions.assertEquals(converterService.decode(converterService.encode(decoded)), decoded);
	}

	@Test
	void convert() {
	}

	@Test
	void convert1() {
	}
}
