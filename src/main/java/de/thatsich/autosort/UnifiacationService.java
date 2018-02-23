package de.thatsich.autosort;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.UUID.randomUUID;

public class UnifiacationService {
	public Path uniquefy(Path file) {
		final String fileName = file.getFileName().toString();

		final String extension = FilenameUtils.getExtension(fileName);
		final boolean hasExtension = !extension.isEmpty();
		final String base = FilenameUtils.removeExtension(fileName);
		final String uniqueFactor = randomUUID().toString();

		final String uniqueName = base + "-" + uniqueFactor + (hasExtension? "." + extension : "");
		final Path uniquePath = Paths.get(uniqueName);

		return uniquePath;
	}
}
