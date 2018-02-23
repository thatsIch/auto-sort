package de.thatsich.autosort.cli.alias;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

class AliasJUPreferencesPersistenceIT {


	private Preferences preferences;

	@BeforeEach
	void setUp() {
		this.preferences = Preferences.userNodeForPackage(AliasJUPreferencesPersistenceIT.class);
	}

	@AfterEach
	void tearDown() throws BackingStoreException {
		preferences.clear();
	}

	@Test
	void testPersistRetrieve() {
		final Persistence persistence = new AliasJUPreferencesPersistence(this.preferences);
		final String expected = "Test";
		persistence.persist(expected);
		final String actual = persistence.retrieve();

		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testRetrieveWithoutPersist() {
		final Persistence  persistence = new AliasJUPreferencesPersistence(this.preferences);
		final String expected = "";
		final String actual = persistence.retrieve();

		Assertions.assertEquals(expected, actual);
	}

	@Test
	void testRetrieveWithoutPersistFromPreferences() {
		// given
		final String expected = "Test";
		this.preferences.put("alias", expected);
		final AliasJUPreferencesPersistence persistence = new AliasJUPreferencesPersistence(this.preferences);

		// when
		final String actual = persistence.retrieve();

		// then
		Assertions.assertEquals(expected, actual);
	}
}
