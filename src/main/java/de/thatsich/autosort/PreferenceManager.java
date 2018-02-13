package de.thatsich.autosort;

import java.util.prefs.Preferences;

/**
 * Allows wrapping the {@link java.util.prefs.Preferences}
 */
public class PreferenceManager {
	private final Preferences preferences;

	public PreferenceManager(Preferences preferences) {
		this.preferences = preferences;
	}

	public String get(String key, String defaultValue) {
		return this.preferences.get(key, defaultValue);
	}

	public void remove(String key) {
		preferences.remove(key);
	}

	public void put(String key, String value) {
		preferences.put(key, value);
	}
}
