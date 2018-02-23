package de.thatsich.autosort.cli.alias;

import de.thatsich.autosort.cli.HelpPrinter;
import de.thatsich.autosort.cli.Processor;
import de.thatsich.autosort.cli.Repository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

public class AliasProcessor implements Processor<Void> {
	private static final String SHORT_COMMAND = null;
	private static final String LONG_COMMAND = "aliases";
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
	private static final String ARG_NAME = Processor.constructArgNames(ADD_ARGS, DEL_ARGS, LIST_ARGS);

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
			final boolean processed = this.tryAdding(subCommand, aliasArgs) ||
					this.tryDeleting(subCommand, aliasArgs) ||
					this.tryListing(subCommand);

			if (!processed) {
				final String message = "processing command 'alias' but found no matching sub-command '" + subCommand + "' with args '"+ Arrays.toString(aliasArgs)+"'.";
				LOGGER.error(message);
				throw new IllegalStateException(message);
			}
		}

		// passing through
		return null;
	}

	private boolean tryAdding(String subCommand, String[] aliasArgs) throws UnsupportedEncodingException {
		if (subCommand.equals(ADD_ARGS[0])) {
			final String alias = aliasArgs[1];
			final Optional<Path> binding = this.repository.find(alias);
			if (binding.isPresent()) {
				LOGGER.warn("Alias '"+alias+"' is already present with the binding '" + binding.get() + "'.");
			} else {
				final String destination = aliasArgs[2];

				this.repository.persist(alias, Paths.get(destination));
			}

			return true;
		}

		return false;
	}

	private boolean tryDeleting(String subCommand, String[] aliasArgs) throws UnsupportedEncodingException {
		if (subCommand.equals(DEL_ARGS[0])) {
			final String alias = aliasArgs[1];

			final Optional<Path> binding = this.repository.remove(alias);
			if (!binding.isPresent()) {
				LOGGER.warn("No binding found for alias '" + alias + "'.");
			}

			return true;
		}

		return false;
	}

	private boolean tryListing(String subCommand) {
		if (subCommand.equals(LIST_ARGS[0])) {
			this.repository.unmodifiable().forEach((alias, desintation) -> LOGGER.info(alias + " -> " + desintation));

			return true;
		}

		return false;
	}
}
