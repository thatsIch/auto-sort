package de.thatsich.autosort.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public interface Processor<T> {
	Option constructOption();

	T processCommandLine(CommandLine cl);
}
