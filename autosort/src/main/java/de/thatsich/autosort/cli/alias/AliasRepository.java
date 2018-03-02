package de.thatsich.autosort.cli.alias;

import de.thatsich.autosort.cli.Persistence;
import de.thatsich.autosort.cli.Repository;
import de.thatsich.map.MapConverterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AliasRepository implements Repository<String, Path> {

	private static final Logger LOGGER = LogManager.getLogger();

	private final Persistence persistence;
	private final PathConverterService pathConverter;
	private final MapConverterService aliasConverter;

	private final Map<String, Path> cache;

	public AliasRepository(Persistence persistence, PathConverterService converter, MapConverterService aliasConverter) {
		this.persistence = persistence;
		this.pathConverter = converter;
		this.aliasConverter = aliasConverter;

		this.cache = new HashMap<>();
	}

	@Override
	public void initialize() {
		if (cache.isEmpty()) {
			final String encoded = this.persistence.retrieve();
			final Map<String, String> decoded = this.aliasConverter.decode(encoded);

			this.cache.putAll(this.pathConverter.toPaths(decoded));
		}
	}

	@Override
	public void persist(String alias, Path path) {
		if (cache.containsKey(alias)) {
			LOGGER.warn("Alias '"+alias+"' is already present with the binding '" + cache.get(alias) + "'.");
		}
		else {
			this.cache.put(alias, path);
			this.persistCache();
		}
	}

	@Override
	public Optional<Path> find(String alias) {
		return Optional.ofNullable(this.cache.get(alias));
	}

	@Override
	public Optional<Path> remove(String alias) {
		final Optional<Path> removed = Optional.ofNullable(this.cache.remove(alias));
		if (removed.isPresent()) {
			this.persistCache();
		}

		return removed;
	}

	@Override
	public Map<String, Path> unmodifiable() {
		if (this.cache.isEmpty()) {
			LOGGER.warn("No alias defined.");
		}

		return Collections.unmodifiableMap(this.cache);
	}

	private void persistCache() {
		final Map<String, String> stringed = this.pathConverter.toStrings(this.cache);
		final String encoded = this.aliasConverter.encode(stringed);
		this.persistence.persist(encoded);
	}
}
