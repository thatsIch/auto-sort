package de.thatsich.autosort.cli;

import de.thatsich.autosort.PreferenceManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

public class FilterProcessor implements Processor<Void> {
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
	private static final String ARG_NAME = Processor.constructArgNames(ADD_ARGS, DEL_ARGS, LIST_ARGS);
	private static final String PREF_KEY = "filters";

	private static final Logger LOGGER = LogManager.getLogger();

	private final HelpPrinter helpPrinter;
	private final PreferenceManager preferences;

	public FilterProcessor(final HelpPrinter helpPrinter, PreferenceManager preferences) {
		this.helpPrinter = helpPrinter;
		this.preferences = preferences;
	}

	@Override
	public Option constructOption() {
		return Option.builder(SHORT_COMMAND)
				.longOpt(LONG_COMMAND)
				.desc(DESCRIPTION)
				.hasArgs()
				.valueSeparator(' ')
				.argName(ARG_NAME)
				.build();
	}

	@Override
	public Void processCommandLine(CommandLine cl) {
		if (cl.hasOption(LONG_COMMAND)) {
			// can never be null because the cl filters that case
			final String[] filterArgs = cl.getOptionValues(LONG_COMMAND);

			final Option option = this.constructOption();
			final Options options = new Options();
			options.addOption(option);

			if (filterArgs.length > MAX_ARGS) {
				helpPrinter.printOptions(options);

				throw new IllegalArgumentException("filter commands requires specific arguments with max '"+MAX_ARGS+"' arguments.");
			}

			final String subCommand = filterArgs[0];
			final String rawFilters = this.preferences.get(PREF_KEY, "{}");
			final JsonReader reader = Json.createReader(new StringReader(rawFilters));
			final JsonObject jsonObject = reader.readObject();
			if (subCommand.equals(ADD_ARGS[0])) {
				// should check length of args
				// should check if regex
				// should check if destination is either path or alias
				final String regex = filterArgs[1];
				final String destinationOrAlias = filterArgs[2];

				final String persistable =  Json.createObjectBuilder(jsonObject)
						.add(regex, destinationOrAlias)
						.build()
						.toString();

				this.preferences.put(PREF_KEY, persistable);
			} else if (subCommand.equals(DEL_ARGS[0])) {
				// should check length of args
				// should check if regex
				final String regex = filterArgs[1];

				final String persistable = Json.createObjectBuilder(jsonObject)
						.remove(regex)
						.build()
						.toString();

				this.preferences.put(PREF_KEY, persistable);
			} else if (subCommand.equals(LIST_ARGS[0])) {
				// should check length of args
				jsonObject.forEach((regex, desintation) -> LOGGER.info(regex + " -> " + desintation));
			} else {
				helpPrinter.printOptions(options);

				throw new UnsupportedOperationException("unknown sub-command '"+subCommand+"' was given but should be one of the supported ones.");
			}
		}

		return null;
	}
}
