package de.thatsich.autosort.cli.alias;

import de.thatsich.autosort.cli.HelpPrinter;
import de.thatsich.autosort.cli.Processor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class AliasProcessor implements Processor<Void> {
	private static final String SHORT_COMMAND = null;
	private static final String LONG_COMMAND = "alias";
	private static final String[] ADD_ARGS = {
			"add",
			"alias",
			"destination"
	};
	private static final String[] DEL_ARGS = {
			"delete",
			"alias"
	};
	private static final String[] LIST_ARGS = {
			"list"
	};
	private static final int MAX_ARGS = Math.max(ADD_ARGS.length, Math.max(DEL_ARGS.length, LIST_ARGS.length));
	private static final String DESCRIPTION = "manages aliases defined in the alias mapping.";
	private static final String ARG_NAME = new StringJoiner("|")
			.add(Arrays.stream(ADD_ARGS)
				.collect(Collectors.joining(" ")))
			.add(Arrays.stream(DEL_ARGS)
					.collect(Collectors.joining(" ")))
			.add(Arrays.stream(LIST_ARGS)
					.collect(Collectors.joining(" ")))
			.toString();

	private static final Logger LOGGER = LogManager.getLogger();


	private final HelpPrinter helpPrinter;
	private final Repository<String, Path> repository;

	public AliasProcessor(final HelpPrinter helpPrinter, Repository<String, Path> repository) {
		this.helpPrinter = helpPrinter;
		this.repository = repository;
	}

	@Override
	public Option constructOption() {
		return Option.builder(SHORT_COMMAND)
				.longOpt(LONG_COMMAND)
				.desc(DESCRIPTION)
				.hasArgs()
				.valueSeparator(' ')
				.argName(ARG_NAME)
				.build();
	}

	@Override
	public Void processCommandLine(CommandLine cl) throws UnsupportedEncodingException {
		if (cl.hasOption(LONG_COMMAND)) {
			final String[] aliasArgs = cl.getOptionValues(LONG_COMMAND);
			if (aliasArgs.length > MAX_ARGS) {
				final Options options = new Options();
				options.addOption(this.constructOption());
				this.helpPrinter.printOptions(options);

				throw new IllegalStateException("Too many arguments. Alias requires at most '" + MAX_ARGS + "' arguments.");
			}
			
			final String subCommand = aliasArgs[0];
			if (subCommand.equals(ADD_ARGS[0])) {
				final String alias = aliasArgs[1];
				final String destination = aliasArgs[2];

				this.repository.persist(alias, Paths.get(destination));
			} else if (subCommand.equals(DEL_ARGS[0])) {
				final String alias = aliasArgs[1];

				this.repository.remove(alias);
			} else if (subCommand.equals(LIST_ARGS[0])) {
				this.repository.unmodifiable().forEach((alias, desintation) -> LOGGER.info(alias + " -> " + desintation));
			}
		}

		// passing through
		return null;
	}
}
