package de.thatsich.autosort.cli;

import de.thatsich.autosort.PreferenceManager;
import org.apache.commons.cli.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

class AliasProcessorTest {

	private Preferences preferences;
	private AliasProcessor aliasProcessor;
	private DefaultParser argsParser;
	private Options options;

	@BeforeEach
	void setUp() {
		final HelpFormatter formatter = new HelpFormatter();
		this.options = new Options();
		final HelpPrinter helpPrinter = new HelpPrinter(formatter, options);
		this.argsParser = new DefaultParser();
		this.preferences = Preferences.userNodeForPackage(DefaultDirectoryProcessorTest.class);
		final PreferenceManager preferenceManager = new PreferenceManager(preferences);
		this.aliasProcessor = new AliasProcessor(helpPrinter, preferenceManager);
		final Option option = aliasProcessor.constructOption();
		options.addOption(option);
	}

	@AfterEach
	void tearDown() throws BackingStoreException {
		preferences.clear();
	}

	@Test
	void constructOption() {
		final HelpFormatter formatter = new HelpFormatter();
		final Options options = new Options();
		final HelpPrinter helpPrinter = new HelpPrinter(formatter, options);
		final Preferences preferences = Preferences.userNodeForPackage(AliasProcessorTest.class);
		final PreferenceManager preferenceManager = new PreferenceManager(preferences);
		final Processor<Void> aliasProcessor = new AliasProcessor(helpPrinter, preferenceManager);

		final Option constructedOption = aliasProcessor.constructOption();

		Assertions.assertNotNull(constructedOption);
	}

	@Test
	void processCommandLine_noArgs_shouldThrow() throws ParseException, BackingStoreException {
		// given

		// when
		final String[] args = {"--default"};
		final CommandLine cl = argsParser.parse(options, args);
		aliasProcessor.processCommandLine(cl);

		// then
		Assertions.assertTrue(this.preferences.keys().length == 1);
		Assertions.assertEquals("{}", this.preferences.get("filters", "{}"));
	}

	@Test
	void processCommandLine_getter_shouldWork() throws ParseException, BackingStoreException {
		// given

		// when
		final String[] args = {"--default"};
		final CommandLine cl = argsParser.parse(options, args);
		aliasProcessor.processCommandLine(cl);

		// then
		Assertions.assertTrue(this.preferences.keys().length == 1);
		Assertions.assertEquals("{}", this.preferences.get("filters", "{}"));
	}
}
