package de.thatsich.autosort.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class FilterProcessor {
	private static final String SHORT_COMMAND = null;
	private static final String LONG_COMMAND = "filter";
	private static final String[] ADD_ARGS = {
			"add",
			"regex",
			"destination"
	};
	private static final String[] DEL_ARGS = {
			"delete",
			"regex"
	};
	private static final String[] LIST_ARGS = {
			"list"
	};
	private static final int MAX_ARGS = Math.max(ADD_ARGS.length, Math.max(DEL_ARGS.length, LIST_ARGS.length));
	private static final String DESCRIPTION = "manages filters defined in the filter mapping.";
	private static final String ARG_NAME = new StringJoiner("|")
			.add(Arrays.stream(ADD_ARGS)
					.collect(Collectors.joining(" ")))
			.add(Arrays.stream(DEL_ARGS)
					.collect(Collectors.joining(" ")))
			.add(Arrays.stream(LIST_ARGS)
					.collect(Collectors.joining(" ")))
			.toString();
	private static final String PREF_KEY = "filters";

	private static final Logger LOGGER = LogManager.getLogger();

	private final HelpPrinter helpPrinter;
	private final Preferences preferences;

	public FilterProcessor(final HelpPrinter helpPrinter, Preferences preferences) {
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
			final String[] filterArgs = cl.getOptionValues(LONG_COMMAND);
			if (filterArgs.length > MAX_ARGS) {
				this.helpPrinter.printHelp();
			}
			else {
				final String subCommand = filterArgs[0];
				final String rawFilters = this.preferences.get(PREF_KEY, "{}");
				final JsonReader reader = Json.createReader(new StringReader(rawFilters));
				final JsonObject jsonObject = reader.readObject();
				if (subCommand.equals(ADD_ARGS[0])) {
					final String regex = filterArgs[1];
					final String destinationOrAlias = filterArgs[2];

					final String persistable =  Json.createObjectBuilder(jsonObject)
							.add(regex, destinationOrAlias)
							.build()
							.toString();

					this.preferences.put(PREF_KEY, persistable);
				} else if (subCommand.equals(DEL_ARGS[0])) {
					final String regex = filterArgs[1];

					final String persistable = Json.createObjectBuilder(jsonObject)
							.remove(regex)
							.build()
							.toString();

					this.preferences.put(PREF_KEY, persistable);
				} else if (subCommand.equals(LIST_ARGS[0])) {
					jsonObject.forEach((regex, desintation) -> {
						LOGGER.info(regex + " -> " + desintation);
					});
				}
			}
		}
	}
}
