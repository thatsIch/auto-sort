package de.thatsich.autosort;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

class UnifiacationServiceTest {

	@Test
	void uniquefy() {
		// given
		final UnifiacationService service = new UnifiacationService();

		final Path start = Paths.get("").toAbsolutePath().resolve("pom.xml");

		// when
		final Path unique = service.uniquefy(start);

		// then
		Assertions.assertNotEquals(start, unique);
	}
}
