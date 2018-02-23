package de.thatsich.autosort.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

class URLEncoderConverterServiceTest {

	@Test
	void reverse() throws UnsupportedEncodingException {
		final URLEncoderConverterService converterService = new URLEncoderConverterService();
		final Map<String, String> decoded = new HashMap<>();
		decoded.put("anime", "D:\\Download\\Anime");
		decoded.put("love", "D:\\Love Love");

		Assertions.assertEquals(converterService.decode(converterService.encode(decoded)), decoded);
	}

	@Test
	void convertEmptyMap() throws UnsupportedEncodingException {
		final URLEncoderConverterService converterService = new URLEncoderConverterService();
		final Map<String, String> empty = new HashMap<>();

		Assertions.assertEquals(converterService.encode(empty), "");
	}

	@Test
	void convert1() {
	}
}