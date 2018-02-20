package de.thatsich.autosort.cli;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class HelpPrinter {

	private final HelpFormatter formatter;
	private final Options options;

	public HelpPrinter(HelpFormatter formatter, Options options) {
		this.formatter = formatter;
		this.options = options;
	}

	public void printHelp() {
		formatter.setOptionComparator(null);
		formatter.setWidth(140);
		formatter.printHelp("autosort", options);
	}

	public void printOptions(Options options) {
		formatter.setOptionComparator(null);
		formatter.setWidth(140);
		formatter.printHelp("autosort", options);
	}
}
