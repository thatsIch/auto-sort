package de.thatsich.autosort.cli;

import java.util.prefs.Preferences;

public class JUPreferencesPersistence implements Persistence {
	private static final String KEY = "alias";

	private final Preferences preferences;
	private String cache;

	public JUPreferencesPersistence(Preferences preferences) {
		this.preferences = preferences;
	}

	@Override
	public void persist(String toBePersisted) {
		this.preferences.put(KEY, toBePersisted);

		this.cache = toBePersisted;
	}

	@Override
	public String retrieve() {
		if (this.cache == null) {
			this.cache = this.preferences.get(KEY, "");
		}

		return this.cache;
	}
}
