package de.thatsich.autosort.cli.filter;

import de.thatsich.autosort.cli.Persistence;
import de.thatsich.autosort.cli.Repository;
import de.thatsich.map.MapConverterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FilterRepository implements Repository<String, String> {

	private static final Logger LOGGER = LogManager.getLogger();

	private final Persistence persistence;
	private final MapConverterService aliasConverter;

	private final Map<String, String> cache;

	public FilterRepository(Persistence persistence, MapConverterService aliasConverter) {
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
		this.cache.put(key, value);
		this.persistCache();
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
			LOGGER.warn("No filters defined.");
		}

		return Collections.unmodifiableMap(this.cache);
	}

	private void persistCache() {
		final String encoded = this.aliasConverter.encode(this.cache);
		this.persistence.persist(encoded);
	}
}
