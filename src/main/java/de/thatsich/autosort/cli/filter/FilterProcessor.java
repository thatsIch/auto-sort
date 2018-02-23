package de.thatsich.autosort.cli.filter;

import de.thatsich.autosort.cli.HelpPrinter;
import de.thatsich.autosort.cli.Processor;
import de.thatsich.autosort.cli.Repository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Optional;

public class FilterProcessor implements Processor<Void> {
	private static final String SHORT_COMMAND = null;
	private static final String LONG_COMMAND = "filter";
	private static final String[] ADD_ARGS = {
			"add",
			"regex",
			"destination"
	};
	private static final String[] DEL_ARGS = {
			"delete",
			"regex"
	};
	private static final String[] LIST_ARGS = {
			"list"
	};
	private static final int MAX_ARGS = Math.max(ADD_ARGS.length, Math.max(DEL_ARGS.length, LIST_ARGS.length));
	private static final String DESCRIPTION = "manages filters defined in the filter mapping.";
	private static final String ARG_NAME = Processor.constructArgNames(ADD_ARGS, DEL_ARGS, LIST_ARGS);

	private static final Logger LOGGER = LogManager.getLogger();

	private final HelpPrinter helpPrinter;
	private final Repository<String, String> repository;

	public FilterProcessor(final HelpPrinter helpPrinter, Repository<String, String> repository) {
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
			// can never be null because the cl filters that case
			final String[] filterArgs = cl.getOptionValues(LONG_COMMAND);

			final Option option = this.constructOption();
			final Options options = new Options();
			options.addOption(option);

			if (filterArgs.length > MAX_ARGS) {
				helpPrinter.printOptions(options);

				throw new IllegalArgumentException("filter commands requires specific arguments with max '"+MAX_ARGS+"' arguments.");
			}

			final String subCommand = filterArgs[0];
			final boolean processed = this.tryAdding(subCommand, filterArgs) ||
					this.tryDeleting(subCommand, filterArgs) ||
					this.tryListing(subCommand);

			if (!processed) {
				final String message = "processing command 'filter' but found no matching sub-command '" + subCommand + "' with args '"+ Arrays.toString(filterArgs)+"'.";
				LOGGER.error(message);

				helpPrinter.printOptions(options);

				throw new UnsupportedOperationException(message);
			}
		}

		return null;
	}

	private boolean tryAdding(String subCommand, String[] aliasArgs) throws UnsupportedEncodingException {
		if (subCommand.equals(ADD_ARGS[0])) {
			final String regex = aliasArgs[1];
			final Optional<String> binding = this.repository.find(regex);
			if (binding.isPresent()) {
				LOGGER.warn("Filter '"+regex+"' is already present with the binding '" + binding.get() + "'.");
			} else {
				final String destination = aliasArgs[2];

				this.repository.persist(regex, destination);
			}

			return true;
		}

		return false;
	}

	private boolean tryDeleting(String subCommand, String[] aliasArgs) throws UnsupportedEncodingException {
		if (subCommand.equals(DEL_ARGS[0])) {
			final String regex = aliasArgs[1];

			final Optional<String> binding = this.repository.remove(regex);
			if (!binding.isPresent()) {
				LOGGER.warn("No binding found for regex '" + regex + "'.");
			}

			return true;
		}

		return false;
	}

	private boolean tryListing(String subCommand) {
		if (subCommand.equals(LIST_ARGS[0])) {
			this.repository.unmodifiable().forEach((regex, binding) -> LOGGER.info(regex + " -> " + binding));

			return true;
		}

		return false;
	}
}
