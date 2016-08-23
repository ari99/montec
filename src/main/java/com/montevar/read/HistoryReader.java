package com.montevar.read;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.joda.time.DateTime;

/**
 * Uses {@link FileReader} to read data from various sources. Data files that
 * come from Yahoo and Investing.com are in different formats.
 *
 */
@Named
public class HistoryReader {
	private static final Logger logger = LogManager.getLogger();
	private FileReader fileReader;
	private static final String STOCK_PREFIX = "src/main/resources/data/stocks";
	private static final String FEATURE_PREFIX = "src/main/resources/data/features/";

	@Inject
	public HistoryReader(FileReader fileReader) {
		this.fileReader = fileReader;
	}

	public Stream<TreeMap<DateTime, Double>> loadYahooStocks() {
		Stream<Path> paths = fileReader.getFilePaths(STOCK_PREFIX);
		return this.loadYahooHistories(paths);
	}

	public Stream<TreeMap<DateTime, Double>> loadYahooFeatures() {
		System.out.println(new File(".").getAbsolutePath());
		Stream<Path> paths = Stream.of(Paths.get(FEATURE_PREFIX + "gspc.csv"), Paths.get(FEATURE_PREFIX + "ixic.csv"));

		return this.loadYahooHistories(paths);
	}

	public Stream<TreeMap<DateTime, Double>> loadInvestingDotComFeatures() {
		Stream<Path> paths = Stream.of(Paths.get(FEATURE_PREFIX + "crudeOil.txt"),
				Paths.get(FEATURE_PREFIX + "tbonds.txt"));

		return this.loadInvestingDotComHistories(paths);
	}

	private Stream<TreeMap<DateTime, Double>> loadYahooHistories(Stream<Path> paths) {
		Stream<TreeMap<DateTime, Double>> rawStocks;
		rawStocks = paths.map(path -> this.readYahooHistory(path.toString()))
				.filter(optionalTreeMap -> optionalTreeMap.isPresent()).map(optionalTreeMap -> optionalTreeMap.get());
		return rawStocks;
	}

	private Stream<TreeMap<DateTime, Double>> loadInvestingDotComHistories(Stream<Path> paths) {
		Stream<TreeMap<DateTime, Double>> rawStocks;
		rawStocks = paths.map(path -> this.readInvestingDotComHistory(path.toString()))
				.filter(optionalTreeMap -> optionalTreeMap.isPresent()).map(optionalTreeMap -> optionalTreeMap.get());
		return rawStocks;
	}

	public Optional<TreeMap<DateTime, Double>> readInvestingDotComHistory(String fileName) {
		return this.readHistory(fileName, "\t", "MMM d, yyyy", false);

	}

	public Optional<TreeMap<DateTime, Double>> readYahooHistory(String fileName) {
		return this.readHistory(fileName, ",", "yyyy-MM-dd", true);

	}

	/**
	 * Reads both yahoo and investing.com stock data.
	 * 
	 * @param fileName
	 *            File to read.
	 * @param delimiter
	 * @param dateFormat
	 * @param containsHeader
	 *            Whether or not the first line is a header line.
	 * @return Optional map of dates to prices.
	 */
	private Optional<TreeMap<DateTime, Double>> readHistory(String fileName, String delimiter, String dateFormat,
			boolean containsHeader) {

		logger.debug("Loading file {}", fileName);
		// Test to see if the URL crawler response is malformed.
		// If stock quotes are missing, error HTML will be returned instead of
		// delimited data.
		if (this.malformedHistoryResponse(fileName)) {
			return Optional.empty();
		}

		Stream<String> stream = fileReader.readFile(fileName);

		// Skip the first line if it is a header.
		if (containsHeader) {
			stream = stream.skip(1L);
		}

		Stream<String[]> cols = stream.map(string -> string.split(delimiter));

		// The first column should be a date and the second the price
		// in both investing.com and yahoo formats.
		Map<DateTime, Double> valuesMap = cols.collect(
				Collectors.toMap(array -> parseDate(array[0], dateFormat), array -> Double.parseDouble(array[1])));

		return Optional.of(new TreeMap<DateTime, Double>(valuesMap));

	}

	/**
	 * Creates a {@link DateTime} object from a String.
	 * 
	 * @param dateString
	 * @param format
	 *            Format used to parse the string.
	 * @return DateTime result.
	 */
	private DateTime parseDate(String dateString, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			return new DateTime(dateFormat.parse(dateString));
		} catch (ParseException e) {
			logger.catching(e);
			return null;
		}
	}

	/**
	 * Tests to see if the data returned from an http request is malformed. If
	 * the stock doesn't exist an HTML error will be returned instead of
	 * delimited data.
	 * 
	 * @param fileName
	 * @return true if the file contains a malformed response.
	 */
	private boolean malformedHistoryResponse(String fileName) {
		Stream<String> stream = fileReader.readFile(fileName);

		Optional<String> first = stream.findFirst();
		if (first.isPresent()) {
			String firstLine = first.get();
			if (firstLine.contains("doctype")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

}
