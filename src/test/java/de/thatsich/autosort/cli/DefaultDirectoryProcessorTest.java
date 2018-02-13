package de.thatsich.autosort.cli;

import de.thatsich.autosort.PreferenceManager;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DefaultDirectoryProcessorTest {

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void constructOption() {
		final HelpFormatter formatter = new HelpFormatter();
		final Options options = new Options();
		final HelpPrinter helpPrinter = new HelpPrinter(formatter, options);

		final PreferenceManager preferenceManager = Mockito.mock(PreferenceManager.class);
		Mockito.when(preferenceManager.get("default", "")).thenReturn("C:\\");
		final DefaultDirectoryProcessor defaultDirectoryProcessor = new DefaultDirectoryProcessor(helpPrinter, preferenceManager);

		final Option constructedOption = defaultDirectoryProcessor.constructOption();

		System.out.println(constructedOption);
	}

	@Test
	void processCommandLine() {
	}
}
