package com.montevar.read;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.inject.Named;

/**
 * Reads input files and directories.
 *
 */
@Named
public class FileReader {

	/**
	 * Creates a stream of Path objects representing paths in a directory.
	 * 
	 * @param directory
	 *            Directory to find file paths in.
	 * @return Paths found.
	 */
	public Stream<Path> getFilePaths(String directory) {
		Path directoryPath = Paths.get(directory);
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath);
			return StreamSupport.stream(stream.spliterator(), false);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public Stream<String> readFile(String fileName) {
		try {
			Stream<String> lines = Files.lines(Paths.get(fileName));
			return lines;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
