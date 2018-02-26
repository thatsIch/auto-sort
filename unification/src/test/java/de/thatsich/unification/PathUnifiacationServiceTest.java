package de.thatsich.unification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

class PathUnifiacationServiceTest {

	@Test
	void uniquefy() {
		// given
		final PathUnifiacationService service = new PathUnifiacationService();

		final Path start = Paths.get("").toAbsolutePath().resolve("pom.xml");

		// when
		final Path unique = service.uniquefy(start);

		System.out.println("unique = " + Paths.get("D:\\").resolve(unique));

		// then
		Assertions.assertNotEquals(start, unique);
	}
}
