package de.thatsich.autosort.cli.alias;

public interface Persistence {
	void persist(String toBePersisted);

	String retrieve();
}
