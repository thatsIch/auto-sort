package de.thatsich.autosort.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class AliasProcessor {
	private static final String SHORT_COMMAND = null;
	private static final String LONG_COMMAND = "alias";
	private static final String[] ADD_ARGS = {
			"add",
			"alias",
			"destination"
	};
	private static final String[] DEL_ARGS = {
			"delete",
			"alias"
	};
	private static final String[] LIST_ARGS = {
			"list"
	};
	private static final int MAX_ARGS = Math.max(ADD_ARGS.length, Math.max(DEL_ARGS.length, LIST_ARGS.length));
	private static final String DESCRIPTION = "manages aliases defined in the alias mapping.";
	private static final String ARG_NAME = new StringJoiner("|")
			.add(Arrays.stream(ADD_ARGS)
				.collect(Collectors.joining(" ")))
			.add(Arrays.stream(DEL_ARGS)
					.collect(Collectors.joining(" ")))
			.add(Arrays.stream(LIST_ARGS)
					.collect(Collectors.joining(" ")))
			.toString();
	private static final String PREF_KEY = "aliases";
	private static final String PREF_DEFAULT = "{}";

	private static final Logger LOGGER = LogManager.getLogger();


	private final HelpPrinter helpPrinter;
	private final Preferences preferences;

	public AliasProcessor(final HelpPrinter helpPrinter, Preferences preferences) {
		this.helpPrinter = helpPrinter;
		this.preferences = preferences;
	}

	public Option constructOption() {
		return Option.builder(SHORT_COMMAND)
				.longOpt(LONG_COMMAND)
				.desc(DESCRIPTION)
				.hasArgs()
				.valueSeparator(' ')
				.argName(ARG_NAME)
				.build();
	}

	public void processCommandLine(CommandLine cl) {
		if (cl.hasOption(LONG_COMMAND)) {
			final String[] aliasArgs = cl.getOptionValues(LONG_COMMAND);
			if (aliasArgs.length > MAX_ARGS) {
				this.helpPrinter.printHelp();
			}
			else {
				final String subCommand = aliasArgs[0];
				final String rawAliases = this.preferences.get(PREF_KEY, PREF_DEFAULT);
				final JsonReader reader = Json.createReader(new StringReader(rawAliases));
				final JsonObject jsonObject = reader.readObject();
				if (subCommand.equals(ADD_ARGS[0])) {
					final String alias = aliasArgs[1];
					final String destination = aliasArgs[2];

					final String persistable = Json.createObjectBuilder(jsonObject)
							.add(alias, destination)
							.build()
							.toString();

					this.preferences.put(PREF_KEY, persistable);
				} else if (subCommand.equals(DEL_ARGS[0])) {
					final String alias = aliasArgs[1];

					final String persistable = Json.createObjectBuilder(jsonObject)
							.remove(alias)
							.build()
							.toString();

					this.preferences.put(PREF_KEY, persistable);
				} else if (subCommand.equals(LIST_ARGS[0])) {
					jsonObject.forEach((alias, desintation) -> {
						LOGGER.info(alias + " -> " + desintation);
					});
				}
			}
		}
	}

	public Map<String, Path> provideAliasToPaths() {
		final String rawAliases = this.preferences.get(PREF_KEY, PREF_DEFAULT);
		final JsonReader reader = Json.createReader(new StringReader(rawAliases));
		final JsonObject jsonObject = reader.readObject();
		final Map<String, Path> mapping = new HashMap<>();
		for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
			final JsonValue value = entry.getValue();
			final String raw = value.toString();
			final Path path = Paths.get(raw);

			mapping.put(entry.getKey(), path);
		}

		return mapping;
	}
}
