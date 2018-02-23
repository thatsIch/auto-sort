package de.thatsich.autosort.cli.alias;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

class URLEncoderAliasConverterServiceTest {

	@Test
	void reverse() throws UnsupportedEncodingException {
		final URLEncoderAliasConverterService converterService = new URLEncoderAliasConverterService();
		final Map<String, String> decoded = new HashMap<>();
		decoded.put("anime", "D:\\Download\\Anime");
		decoded.put("love", "D:\\Love Love");

		Assertions.assertEquals(converterService.decode(converterService.encode(decoded)), decoded);
	}

	@Test
	void convertEmptyMap() throws UnsupportedEncodingException {
		final URLEncoderAliasConverterService converterService = new URLEncoderAliasConverterService();
		final Map<String, String> empty = new HashMap<>();

		Assertions.assertEquals(converterService.encode(empty), "");
	}

	@Test
	void convert1() {
	}
}
