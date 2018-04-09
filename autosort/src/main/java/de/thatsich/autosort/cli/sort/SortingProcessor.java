package de.thatsich.autosort.cli.sort;

import de.thatsich.autosort.cli.alias.AliasRepository;
import de.thatsich.data.Repository;
import de.thatsich.unification.PathUnifiacationService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortingProcessor {

	private static final Logger LOGGER = LogManager.getLogger();

	private final PathUnifiacationService unifiacationService;
	private final AliasRepository aliasRepository;
	private final Repository<String, String> filterRepository;
	private final TargetSuggester targetSuggester;

	public SortingProcessor(PathUnifiacationService unifiacationService, AliasRepository aliasRepository, Repository<String, String> filterRepository, TargetSuggester targetSuggester) {
		this.unifiacationService = unifiacationService;
		this.aliasRepository = aliasRepository;
		this.filterRepository = filterRepository;
		this.targetSuggester = targetSuggester;
	}

	private void sort(Path workingDirectory) throws IOException {
		final List<Path> files = Files.walk(workingDirectory, 1)
				.filter(Files::isRegularFile)
				.collect(Collectors.toList());

		// we convert the filterings in paths which can be either real paths or using aliases
		// alias -> destination
		final Map<String, Path> pathings = this.aliasRepository.unmodifiable();

		// regex -> destination or alias
		final Map<String, String> filterings = filterRepository.unmodifiable();

		// regex -> destination
		final Map<String, Path> converted = new HashMap<>(filterings.size());
		for (Map.Entry<String, String> filtering : filterings.entrySet()) {
			final String regex = filtering.getKey();
			final String pathOrAlias = filtering.getValue();

			// first we check for alias
			if (pathings.containsKey(pathOrAlias)) {
				final Path destination = pathings.get(pathOrAlias);
				converted.put(regex, destination);
				// else we check if it is a path
			} else {
				final Path path = Paths.get(pathOrAlias);
				if (Files.isRegularFile(path)) {
					converted.put(regex, path);
				// error, need to be handled
				} else {
					LOGGER.error("Unhandeld filtering ["+regex+", "+ pathOrAlias+"]. Considering adding an alias or provide a valid destination.");
				}
			}
		}

		// handle suggestions if found unmatched videos
		for (Path file : files) {
			final String fileName = file.getFileName().toString();
			converted.forEach((regex, dest) -> {
				if (fileName.matches("(?i)" + regex)) {
					try {
						LOGGER.info(fileName);
						LOGGER.info("\tDestination: " + dest);
						final Path existing = getResolvedExistingPath(dest, fileName);
						Files.move(file, existing);
						LOGGER.info("\tMoved.");
					} catch (FileAlreadyExistsException e) {
						final Path existing = dest.resolve(fileName);
						LOGGER.info("\tAlready existing.");
						LOGGER.info("\tChecking for file size.");
						try {
							final long sourceSize = Files.size(existing);
							final long targetSize = Files.size(file);

							// in case the files are called the same and have the same size we can argue, that it is very likely that it is the same video content
							if (sourceSize == targetSize) {
								LOGGER.info("\t\tFound same size: deleting source file.");
								Files.delete(file);
								LOGGER.info("Deleted.");
							} else {
								LOGGER.info("\t\tFound different size: starting unification.");
								final Path unique = unifiacationService.uniquefy(file);
								LOGGER.info("\t\tUnique name: " + unique);
								final Path moved = Files.move(file, dest.resolve(unique));
								LOGGER.info("\t\tMoved: " + moved);
							}
						} catch (IOException el) {
							LOGGER.error("\tDeduplication failed.", el);
						}
					} catch (IOException e) {
						LOGGER.error("Failed", e);
					}
				}
			});
		}

		final List<Path> unmoved = Files.walk(workingDirectory, 1)
				.filter(Files::isRegularFile)
				.collect(Collectors.toList());

		LOGGER.info(unmoved.size() + " unmoved files. Suggesting filters:");
		targetSuggester.printSuggestions(unmoved, converted);
	}

	private Path getResolvedExistingPath(Path parent, String name) throws IOException {
		final Path path = parent.resolve(name);

		if (!Files.exists(parent))
			Files.createDirectory(parent);

		return path;
	}

	public Option constructOption() {
		return Option.builder("s")
				.longOpt("sort")
				.desc("applies filters and alias to sort files in the set working directory.")
				.hasArg(false)
				.build();
	}

	public void processCommandLineInWorkingDirectory(CommandLine cl, Path workingDirectory) throws IOException {
		if (cl.hasOption("sort")) {
			LOGGER.info("Processing directory: " + workingDirectory);
			this.sort(workingDirectory);
			LOGGER.info("Finished auto-sorting.");
		}
	}

}
