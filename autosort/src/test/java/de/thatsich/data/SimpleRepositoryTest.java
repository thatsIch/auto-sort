package de.thatsich.data;

import de.thatsich.autosort.cli.JUPreferencesPersistence;
import de.thatsich.autosort.cli.Persistence;
import de.thatsich.map.URLEncoderConverterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

class SimpleRepositoryTest {
	private Preferences preferences;
	private Repository<String, String> repository;

	@BeforeEach
	void setUp() {
		this.preferences = Preferences.userNodeForPackage(SimpleRepositoryTest.class);
		final Persistence persistence = new JUPreferencesPersistence("simple", preferences);
		final URLEncoderConverterService aliasConverterService = new URLEncoderConverterService();
		this.repository = new SimpleRepository(persistence, aliasConverterService);
	}

	@AfterEach
	void tearDown() throws BackingStoreException {
		this.preferences.clear();
	}

	@Test
	void persist() throws UnsupportedEncodingException {
		// given
		final String expected = "D:\\Download";

		// when
		repository.persist("Test", expected);

		// then
		final Optional<String> finding = repository.find("Test");
		Assertions.assertTrue(finding.isPresent());
		Assertions.assertEquals(expected, finding.get());
	}

	@Test
	void persistWithCache() throws UnsupportedEncodingException {
		// given
		repository.persist("Foo", "D:\\Download");
		final String expected = "D:\\Download";

		// when
		repository.persist("Bar", expected);

		// then
		final Optional<String> finding = repository.find("Bar");
		Assertions.assertTrue(finding.isPresent());
		Assertions.assertEquals(expected, finding.get());

	}

	@Test
	void find() throws UnsupportedEncodingException {
		// given
		final String expected = "D:\\Download";
		repository.persist("Test", expected);

		// when
		final Optional<String> finding = repository.find("Test");

		// then
		Assertions.assertEquals(Optional.of(expected), finding);
	}

	@Test
	void remove() throws UnsupportedEncodingException {
		// given
		final String expected = "D:\\Download";
		repository.persist("Test", expected);

		// when
		final Optional<String> maybeRemoved = repository.remove("Test");

		// then
		Assertions.assertTrue(maybeRemoved.isPresent());
	}

	@Test
	void removeNonPresentKey() throws UnsupportedEncodingException {
		// given

		// when
		final Optional<String> maybeRemoved = repository.remove("Test");

		// then
		Assertions.assertFalse(maybeRemoved.isPresent());
	}

	@Test
	void unmodifiable() {
		// given
		final Map<String, String> unmodifiable = repository.unmodifiable();

		// when
		final Executable process = () -> unmodifiable.put("Test", "D:\\Download");

		// then
		Assertions.assertThrows(UnsupportedOperationException.class, process);
	}
}
