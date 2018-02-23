package de.thatsich.autosort.cli.alias;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.*;

public class AliasRepository {

	private final Persistence persistence;
	private final PathConverterService pathConverter;
	private final URLEncoderAliasConverterService aliasConverter;

	private final Map<String, Path> cache;

	public AliasRepository(Persistence persistence, PathConverterService converter, URLEncoderAliasConverterService aliasConverter) {
		this.persistence = persistence;
		this.pathConverter = converter;
		this.aliasConverter = aliasConverter;

		this.cache = new HashMap<>();
	}

	public void persist(String alias, Path path) throws UnsupportedEncodingException {
		if (cache.isEmpty()) {
			final String encoded = this.persistence.retrieve();
			final Map<String, String> decoded = this.aliasConverter.decode(encoded);

			this.cache.putAll(this.pathConverter.toPaths(decoded));
		}

		this.cache.put(alias, path);
		this.persistCache();
	}

	public Optional<Path> find(String alias) {
		return Optional.ofNullable(this.cache.get(alias));
	}

	public Optional<Path> remove(String alias) throws UnsupportedEncodingException {
		final Optional<Path> removed = Optional.ofNullable(this.cache.remove(alias));
		if (removed.isPresent()) {
			this.persistCache();
		}

		return removed;
	}

	public Map<String, Path> unmodifiable() {
		return Collections.unmodifiableMap(this.cache);
	}

	private void persistCache() throws UnsupportedEncodingException {
		final Map<String, String> stringed = this.pathConverter.toStrings(this.cache);
		final String encoded = this.aliasConverter.encode(stringed);
		this.persistence.persist(encoded);
	}
}
