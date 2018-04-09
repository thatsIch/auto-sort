package de.thatsich.autosort.cli.help;

import de.thatsich.autosort.cli.HelpPrinter;
import de.thatsich.autosort.cli.Processor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class HelpProcessor {

	private final HelpPrinter helpPrinter;

	public HelpProcessor(HelpPrinter helpPrinter) {
		this.helpPrinter = helpPrinter;
	}

	public Option constructOption() {
		return Option.builder("h")
				.longOpt("help")
				.required(false)
				.desc("displays help. overrides any other command.")
				.build();
	}

	public void processCommandLineOptions(CommandLine cl, Options options) {
		final boolean hasHelpOption = cl.hasOption("h");
		final boolean hasNoOption = cl.getOptions().length == 0;

		if (hasHelpOption || hasNoOption) {
			helpPrinter.printOptions(options);
		}
	}
}
