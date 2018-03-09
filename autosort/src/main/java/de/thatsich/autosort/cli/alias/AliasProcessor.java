package de.thatsich.autosort.cli.alias;

import de.thatsich.autosort.cli.BaseProcessor;
import de.thatsich.autosort.cli.HelpPrinter;
import de.thatsich.data.Repository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AliasProcessor extends BaseProcessor<Void> {

	private static final String DESCRIPTION = "manages aliases defined in the alias mapping.";

	private static final Logger LOGGER = LogManager.getLogger();

	private final HelpPrinter helpPrinter;
	private final Repository<String, Path> repository;

	public AliasProcessor(final HelpPrinter helpPrinter, Repository<String, Path> repository) {
		this.helpPrinter = helpPrinter;
		this.repository = repository;
	}

	@Override
	public Option constructOption() {
		return Option.builder(getShortCommand())
				.longOpt(getLongCommand())
				.desc(DESCRIPTION)
				.hasArgs()
				.valueSeparator(' ')
				.argName(getArgNames())
				.build();
	}

	@Override
	public Void processCommandLine(CommandLine cl) {
		if (cl.hasOption(getLongCommand())) {
			final String[] aliasArgs = cl.getOptionValues(getLongCommand());
			final String subCommand = aliasArgs[0];

			final boolean success = this.tryTooManyArgs(aliasArgs) ||
					this.tryAdding(subCommand, aliasArgs) ||
					this.tryDeleting(subCommand, aliasArgs) ||
					this.tryListing(subCommand);

			if (!success) {
				this.throwError(subCommand, aliasArgs);
			}
		}

		// passing through
		return null;
	}

	private boolean tryTooManyArgs(String[] aliasArgs) {
		if (aliasArgs.length > getMaxArgs()) {
			final Options options = new Options();
			options.addOption(this.constructOption());
			this.helpPrinter.printOptions(options);

			throw new IllegalStateException("Too many arguments. Alias requires at most '" + getMaxArgs() + "' arguments.");
		}

		return false;
	}

	private boolean tryAdding(String subCommand, String[] aliasArgs) {
		if (subCommand.equals(getAddArgs().get(0))) {
			final String alias = aliasArgs[1];
			final String destination = aliasArgs[2];

			this.repository.persist(alias, Paths.get(destination));

			return true;
		}

		return false;
	}

	private boolean tryDeleting(String subCommand, String[] aliasArgs) {
		if (subCommand.equals(getDelArgs().get(0))) {
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
		if (subCommand.equals(getListArgs().get(0))) {
			final Map<String, Path> aliases = this.repository.unmodifiable();
			LOGGER.info(aliases.size() + " aliases defined:");
			aliases.forEach((alias, desintation) -> LOGGER.info("* " + alias + " -> " + desintation));

			return true;
		}

		return false;
	}

	private void throwError(String subCommand, String[] aliasArgs) {
		throw new IllegalStateException("processing command 'alias' but found no matching sub-command '" + subCommand + "' with args '"+ Arrays.toString(aliasArgs)+"'.");
	}

	@Override
	public String getShortCommand() {
		return null;
	}

	@Override
	public String getLongCommand() {
		return "alias";
	}

	@Override
	public List<String> getAddArgs() {
		return List.of("add", "al", "dest");
	}

	@Override
	public List<String> getDelArgs() {
		return List.of("del", "al");
	}

	@Override
	public List<String> getListArgs() {
		return List.of("list");
	}

	@Override
	protected String getArgNames() {
		return null;
	}

}
