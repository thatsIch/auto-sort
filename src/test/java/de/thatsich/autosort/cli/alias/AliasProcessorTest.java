package de.thatsich.autosort.cli.alias;

import de.thatsich.autosort.cli.HelpPrinter;
import org.apache.commons.cli.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.UnsupportedEncodingException;

class AliasProcessorTest {

	private AliasProcessor aliasProcessor;
	private DefaultParser argsParser;
	private Options options;

	@BeforeEach
	void setUp() {
		final HelpFormatter formatter = new HelpFormatter();
		this.options = new Options();
		final HelpPrinter helpPrinter = new HelpPrinter(formatter);
		this.argsParser = new DefaultParser();
		final NonPersistentRepository repository = new NonPersistentRepository();
		this.aliasProcessor = new AliasProcessor(helpPrinter, repository);
		final Option option = aliasProcessor.constructOption();
		options.addOption(option);
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

	@Test
	void processCommandLine_tooManyFlags_shouldThrow() throws ParseException {
		// given

		// when
		final String[] args = {"--alias", "just", "too", "many", "args", "more", "than", "expected"};
		final CommandLine cl = argsParser.parse(options, args);
		final Executable process = () ->  this.aliasProcessor.processCommandLine(cl);

		// then
		Assertions.assertThrows(IllegalStateException.class, process);
	}
}
