package de.thatsich.autosort.cli.alias;

import de.thatsich.autosort.cli.HelpPrinter;
import org.apache.commons.cli.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.UnsupportedEncodingException;
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
		this.preferences = Preferences.userNodeForPackage(AliasProcessorTest.class);
		final AliasJUPreferencesPersistence persistence = new AliasJUPreferencesPersistence(preferences);
		final PathConverterService pathConverterService = new PathConverterService();
		final URLEncoderAliasConverterService aliasConverterService = new URLEncoderAliasConverterService();
		final AliasRepository aliasRepository = new AliasRepository(persistence, pathConverterService, aliasConverterService);
		this.aliasProcessor = new AliasProcessor(helpPrinter, aliasRepository);
		final Option option = aliasProcessor.constructOption();
		options.addOption(option);
	}

	@AfterEach
	void tearDown() throws BackingStoreException {
		preferences.clear();
	}

	@Test
	void constructOption() {
		final Option constructedOption = aliasProcessor.constructOption();

		Assertions.assertNotNull(constructedOption);
	}

	@Test
	void processCommandLine_noArgs_doesNothing() throws ParseException, UnsupportedEncodingException {
		// given

		// when
		final String[] args = {};
		final CommandLine cl = argsParser.parse(options, args);
		final Void processed = aliasProcessor.processCommandLine(cl);

		// then
		Assertions.assertNull(processed);
	}

	/**
	 * This is generally guarded by the CLI
	 */
	@Test
	void processCommandLine_onlyFlag_shouldNotWork() {
		// given

		// when
		final String[] args = {"--alias"};
		final Executable process = () -> argsParser.parse(options, args);

		// then
		Assertions.assertThrows(MissingArgumentException.class, process);
	}
}
