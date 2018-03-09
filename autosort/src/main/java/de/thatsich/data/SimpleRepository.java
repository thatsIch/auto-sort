package de.thatsich.data;

import de.thatsich.autosort.cli.Persistence;
import de.thatsich.map.MapConverterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SimpleRepository implements Repository<String, String> {
	private static final Logger LOGGER = LogManager.getLogger();

	private final Persistence persistence;
	private final MapConverterService aliasConverter;

	private final Map<String, String> cache;

	public SimpleRepository(Persistence persistence, MapConverterService aliasConverter) {
		this.persistence = persistence;
		this.aliasConverter = aliasConverter;

		this.cache = new HashMap<>();
	}

	@Override
	public void initialize() {
		if (cache.isEmpty()) {
			final String encoded = this.persistence.retrieve();
			final Map<String, String> decoded = this.aliasConverter.decode(encoded);

			this.cache.putAll(decoded);
		}
	}

	@Override
	public void persist(String key, String value) {
		if (cache.containsKey(value)) {
			LOGGER.warn("Value '"+value+"' is already present with the binding '" + cache.get(value) + "'.");
		}
		else {
			this.cache.put(key, value);
			this.persistCache();
		}
	}

	@Override
	public Optional<String> find(String alias) {
		return Optional.ofNullable(this.cache.get(alias));
	}

	@Override
	public Optional<String> remove(String alias) {
		final Optional<String> removed = Optional.ofNullable(this.cache.remove(alias));
		if (removed.isPresent()) {
			this.persistCache();
		}

		return removed;
	}

	@Override
	public Map<String, String> unmodifiable() {
		if (this.cache.isEmpty()) {
			LOGGER.warn("Cache is empty.");
		}

		return Collections.unmodifiableMap(this.cache);
	}

	private void persistCache() {
		final String encoded = this.aliasConverter.encode(this.cache);
		this.persistence.persist(encoded);
	}
}
