package de.thatsich.autosort.cli;

import de.thatsich.autosort.PreferenceManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultDirectoryProcessor {
	private static final String SHORT_COMMAND = null;
	private static final String LONG_COMMAND = "default";
	private static final String SET_ARGS = "destination";
	private static final int MAX_ARGS = 1;
	private static final String DESCRIPTION = "manages default-directory.";
	private static final String ARG_NAME = "?" + SET_ARGS;
	private static final String PREF_KEY = "default";

	private static final Logger LOGGER = LogManager.getLogger();

	private final HelpPrinter helpPrinter;
	private final PreferenceManager preferences;

	public DefaultDirectoryProcessor(final HelpPrinter helpPrinter, PreferenceManager preferences) {
		this.helpPrinter = helpPrinter;
		this.preferences = preferences;
	}

	public Option constructOption() {
		return Option.builder(SHORT_COMMAND)
				.longOpt(LONG_COMMAND)
				.desc(DESCRIPTION)
				.hasArg()
				.optionalArg(true)
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
				final String maybeDirectory = filterArgs[0];

				// we want to set the directory
				if (maybeDirectory != null) {
					final Path defaultDirectoryPath = Paths.get(maybeDirectory);
					final String stringified = defaultDirectoryPath.toAbsolutePath().toString();
					preferences.put(PREF_KEY, stringified);

					LOGGER.info("Stored '" + stringified + "' as default directory.");
				// we want to print the default directory
				} else {
					final String suggestion = Paths.get("").toAbsolutePath().toString();
					final String defaultDirectory = preferences.get(PREF_KEY, suggestion);
					if (suggestion.equals(defaultDirectory)) {
						LOGGER.warn("Default directory has yet to be set. Defaulting to current working directory. Use the option 'default <directory>'.");
					}
					final Path defaultDirectoryPath = Paths.get(defaultDirectory);

					LOGGER.info("Default directory: " + defaultDirectoryPath.toString());
				}
			}
		}
	}
}
