package de.thatsich.autosort.cli;

import de.thatsich.autosort.PreferenceManager;
import org.apache.commons.cli.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

class DefaultDirectoryProcessorTest {

	private Preferences preferences;
	private DefaultDirectoryProcessor defaultDirectoryProcessor;
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
		this.defaultDirectoryProcessor = new DefaultDirectoryProcessor(helpPrinter, preferenceManager);
		final Option option = defaultDirectoryProcessor.constructOption();
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
		final Preferences preferences = Preferences.userNodeForPackage(DefaultDirectoryProcessorTest.class);
		final PreferenceManager preferenceManager = new PreferenceManager(preferences);
		final DefaultDirectoryProcessor defaultDirectoryProcessor = new DefaultDirectoryProcessor(helpPrinter, preferenceManager);

		final Option constructedOption = defaultDirectoryProcessor.constructOption();

		Assertions.assertNotNull(constructedOption);
	}

	@Test
	void processCommandLine_getter_shouldWork() throws ParseException {
		// given

		// when
		final String[] args = {"--default"};
		final CommandLine cl = argsParser.parse(options, args);
		final Path defaultPath = defaultDirectoryProcessor.processCommandLine(cl);

		// then
		Assertions.assertEquals(defaultPath, Paths.get("").toAbsolutePath());
	}

	@Test
	void processCommandLine_setter_shouldWork() throws ParseException {
		// given

		// when
		final String[] args = {"--default", "C:"};
		final CommandLine cl = argsParser.parse(options, args);
		final Path defaultPath = defaultDirectoryProcessor.processCommandLine(cl);

		// then
		Assertions.assertEquals(defaultPath, Paths.get("C:").toAbsolutePath());
	}

	@Test
	void processCommandLine_getterAfterSetter_shouldWork() throws ParseException {
		// given

		// when
		// setting
		final String[] setArgs = {"--default", "C:"};
		final CommandLine setCL = argsParser.parse(options, setArgs);
		final Path setPath = defaultDirectoryProcessor.processCommandLine(setCL);


		// getting
		// setting
		final String[] getArgs = {"--default"};
		final CommandLine getCL = argsParser.parse(options, getArgs);
		final Path defaultPath = defaultDirectoryProcessor.processCommandLine(getCL);

		// then
		Assertions.assertEquals(defaultPath, setPath);
	}

	@Test
	void processCommandLine_withTooManyArgs_shouldThrow() throws ParseException {
		// given

		// when
		// setting
		final String[] setArgs = {"--default", "C:", "D:"};
		final CommandLine setCL = argsParser.parse(options, setArgs);

		// then
		Assertions.assertThrows(IllegalArgumentException.class, () -> defaultDirectoryProcessor.processCommandLine(setCL));
	}

	@Test
	void processCommandLine_withoutFlag_doNothing() throws ParseException {
		// given

		// when
		// setting
		final String[] setArgs = {};
		final CommandLine setCL = argsParser.parse(options, setArgs);
		final Path absentPath = defaultDirectoryProcessor.processCommandLine(setCL);

		// then
		Assertions.assertNull(absentPath);
	}
}
