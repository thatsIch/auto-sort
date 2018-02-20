package de.thatsich.autosort.cli;

import de.thatsich.autosort.PreferenceManager;
import org.apache.commons.cli.*;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

class FilterProcessorTest {

	private Preferences preferences;
	private FilterProcessor filterProcessor;
	private DefaultParser argsParser;
	private Options options;

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests().enableLog();

	@BeforeEach
	void setUp() {
		final HelpFormatter formatter = new HelpFormatter();
		this.options = new Options();
		final HelpPrinter helpPrinter = new HelpPrinter(formatter, options);
		this.argsParser = new DefaultParser();
		this.preferences = Preferences.userNodeForPackage(FilterProcessorTest.class);
		final PreferenceManager preferenceManager = new PreferenceManager(preferences);
		this.filterProcessor = new FilterProcessor(helpPrinter, preferenceManager);
		final Option option = filterProcessor.constructOption();
		options.addOption(option);
	}

	@AfterEach
	void tearDown() throws BackingStoreException {
		preferences.clear();
	}

	@Test
	void constructOption() {
		Assertions.assertEquals(options.getOptions().size(), 1);
	}

	@Test
	void processCommandLine_noInput_shouldDoNothing() throws ParseException, BackingStoreException {
		// given
//		System.setOut(new PrintStream());

		// when
		final String[] args = {};
		final CommandLine setCL = argsParser.parse(options, args);
		filterProcessor.processCommandLine(setCL);

		// then
		Assertions.assertTrue(this.preferences.keys().length == 0);
	}

	@Test
	void processCommandLine_withFlag_throwsExceptionByFramework() throws ParseException, BackingStoreException {
		// given
//		System.setOut(new PrintStream());

		// when
		final String[] args = {"--filter"};
		final Executable setCL = () -> argsParser.parse(options, args);

		// then
		Assertions.assertThrows(MissingArgumentException.class, setCL);
	}

	@Test
	void processCommandLine_withTooManyFlags_throwsException() throws ParseException, BackingStoreException {
		// given
//		System.setOut(new PrintStream());

		// when
		final String[] args = {"--filter", "foo", "bar", "batz", "foobar"};
		final CommandLine setCL = argsParser.parse(options, args);
		final Executable process = () -> filterProcessor.processCommandLine(setCL);

		// then
		Assertions.assertThrows(IllegalArgumentException.class, process);
	}

	@Test
	void processCommandLine_withUnknownArg_throwsException() throws ParseException, BackingStoreException {
		// given
//		System.setOut(new PrintStream());

		// when
		final String[] args = {"--filter", "foo"};
		final CommandLine setCL = argsParser.parse(options, args);
		final Executable process = () -> filterProcessor.processCommandLine(setCL);

		// then
		Assertions.assertThrows(UnsupportedOperationException.class, process);
	}

	@Test
	void processCommandLine_withCorrectAdd_shouldWork() throws ParseException, BackingStoreException {
		// given

		// when
		final String[] args = {"--filter", "add", "*.mp4", "D:\\Download\\Anime"};
		final CommandLine setCL = argsParser.parse(options, args);
		filterProcessor.processCommandLine(setCL);

		// then
		Assertions.assertTrue(this.preferences.keys().length == 1);
		Assertions.assertNotEquals("{}", this.preferences.get("filters", "{}"));
	}

	@Test
	void processCommandLine_withCorrectRemove_shouldWork() throws ParseException, BackingStoreException {
		// given

		// when
		final String[] args = {"--filter", "delete", "*.mp4"};
		final CommandLine setCL = argsParser.parse(options, args);
		filterProcessor.processCommandLine(setCL);

		// then
		Assertions.assertTrue(this.preferences.keys().length == 1);
		Assertions.assertEquals("{}", this.preferences.get("filters", "{}"));
	}

	@Test
	void processCommandLine_withList_prints() throws ParseException, BackingStoreException {
		// given

		// when
		final String[] args = {"--filter", "list"};
		final CommandLine setCL = argsParser.parse(options, args);
		filterProcessor.processCommandLine(setCL);

		// then
		Assertions.assertNotNull(systemOutRule.getLog());
	}

	// needs to list all filters
	// should work with empty list
	// one filter
	// more filters
	// can check against the output stream and see if logs contains them
	//	final String[] args = {"--filter", "list"};

	// missing argument
	//	final String[] args = {"--filter", "delete"};
	// should work if filter exists, else it doesnt
	//	final String[] args = {"--filter", "delete", "non-existing-filter"};

	// missing arguments
	//	final String[] args = {"--filter", "add"};
	//	final String[] args = {"--filter", "add", "regex"};

	// invalid case if neither of list, delete or add is being called
	// validate regex being a regex

	//	final String[] args = {};
	//	final String[] args = {};
	//	final String[] args = {};

}
