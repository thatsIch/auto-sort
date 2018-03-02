package de.thatsich.autosort.cli;

import java.util.Map;
import java.util.Optional;

public interface Repository<K, V> {
	void initialize();

	void persist(K key, V value);

	Optional<V> find(K key);

	Optional<V> remove(K alias);

	Map<K, V> unmodifiable();
}
