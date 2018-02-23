package de.thatsich.autosort.cli.alias;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;

public interface Repository<Key, Value> {
	void persist(Key key, Value value) throws UnsupportedEncodingException;

	Optional<Value> find(Key key);

	Optional<Value> remove(Key alias) throws UnsupportedEncodingException;

	Map<Key, Value> unmodifiable();
}
