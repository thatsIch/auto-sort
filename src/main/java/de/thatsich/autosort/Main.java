package de.thatsich.autosort;

import de.thatsich.autosort.cli.AliasProcessor;
import de.thatsich.autosort.cli.DefaultDirectoryProcessor;
import de.thatsich.autosort.cli.FilterProcessor;
import de.thatsich.autosort.cli.HelpPrinter;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * Single entry point for the application providing the main args
 *
 * Handles all the dependency management and constructs the basic application and processing
 *
 * call "autosort --help" for usage
 *
 * @author thatsIch (thatsich@mail.de)
 * @version 1.0-SNAPSHOT 15.11.2017
 * @since 1.0-SNAPSHOT
 */
public class Main {

	private static final Logger LOGGER = LogManager.getLogger();

	public static void main(String[] args) throws IOException, ParseException {
		final CommandLineParser argsParser = new DefaultParser();
		final Options options = new Options();

		final HelpFormatter formatter = new HelpFormatter();
		final HelpPrinter helpPrinter = new HelpPrinter(formatter, options);
		final Preferences preferences = Preferences.userNodeForPackage(Main.class);
		final PreferenceManager preferenceManager = new PreferenceManager(preferences);

		options.addOption("d", "directory", true, "destination which to sort. can override the default-directory set from 'default <directory>'.");

		final DefaultDirectoryProcessor defaultDirectoryProcessor = new DefaultDirectoryProcessor(helpPrinter, preferenceManager);
		options.addOption(defaultDirectoryProcessor.constructOption());

		final AliasProcessor aliasProcessor = new AliasProcessor(helpPrinter, preferences);
		options.addOption(aliasProcessor.constructOption());

		final FilterProcessor filterProcessor = new FilterProcessor(helpPrinter, preferenceManager);
		options.addOption(filterProcessor.constructOption());

		options.addOption("h", "help", false, "displays help. overrides any other command.");

		final CommandLine cl = argsParser.parse(options, args);


		if (cl.hasOption("h")) {
			helpPrinter.printHelp();

			return;
		}

		// we either have a default directory or set manually a directory else we use the current working directory as a meaningful default

		defaultDirectoryProcessor.processCommandLine(cl);

		final Path workingDirectory;
		if (cl.hasOption("d")) {
			workingDirectory = Paths.get(cl.getOptionValue("d"));

			LOGGER.info("Manual directory set. Preceeds default directory.");
		} else {
			// use path stored in the preferences
			final String suggestion = Paths.get("").toAbsolutePath().toString();
			final String defaultDirectory = preferences.get("default-directory", suggestion);
			workingDirectory = Paths.get(defaultDirectory);
		}
		LOGGER.info("Processing directory: " + workingDirectory);

		aliasProcessor.processCommandLine(cl);
		filterProcessor.processCommandLine(cl);

		final VideoProcessor videoProcessor = new VideoProcessor();
		videoProcessor.process(workingDirectory);

//		boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean(). getInputArguments().toString().contains("-agentlib:jdwp");
//		LOGGER.info(isDebug);

		LOGGER.info("Finished auto-sorting.");
	}
}
