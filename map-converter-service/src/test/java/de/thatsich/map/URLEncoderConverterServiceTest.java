package de.thatsich.map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

class URLEncoderConverterServiceTest {

	@Test
	void both() throws UnsupportedEncodingException {
		final MapConverterService converterService = new URLEncoderConverterService();
		final Map<String, String> decoded = new HashMap<>();
		decoded.put("anime", "D:\\Download\\Anime");
		decoded.put("love", "D:\\Love Love");

		Assertions.assertEquals(converterService.decode(converterService.encode(decoded)), decoded);
	}

	@Test
	void convertEmptyMap() throws UnsupportedEncodingException {
		final MapConverterService converterService = new URLEncoderConverterService();
		final Map<String, String> empty = new HashMap<>();

		Assertions.assertEquals(converterService.encode(empty), "");
	}

	@Test
	void convertSingleLetter() throws UnsupportedEncodingException {
		final MapConverterService converterService = new URLEncoderConverterService();
		final Map<String, String> decoded = Map.of("k", "v");

		Assertions.assertEquals(converterService.encode(decoded), "k=v");
	}

	@Test
	void convertEmptyStrings() throws UnsupportedEncodingException {
		final MapConverterService converterService = new URLEncoderConverterService();
		final Map<String, String> decoded = Map.of("k", "");

		Assertions.assertEquals(converterService.encode(decoded), "k=");
	}

	@Test
	void convertEmptyKey() {
		final MapConverterService converterService = new URLEncoderConverterService();
		final Map<String, String> decoded = Map.of("", "");

		Assertions.assertThrows(IllegalArgumentException.class, () -> converterService.encode(decoded));
	}
}
