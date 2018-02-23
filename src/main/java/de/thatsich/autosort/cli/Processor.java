package de.thatsich.autosort.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public interface Processor<T> {
	Option constructOption();

	T processCommandLine(CommandLine cl) throws UnsupportedEncodingException;

	static String constructArgNames(String[]... args) {
		final StringJoiner joiner = new StringJoiner("|");
		for (String[] arg : args) {
			joiner.add(Arrays.stream(arg).collect(Collectors.joining(" ")));
		}

		return joiner.toString();
	};
}
