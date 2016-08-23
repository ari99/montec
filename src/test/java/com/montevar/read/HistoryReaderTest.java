package com.montevar.read;

import org.joda.time.DateTime;
import org.junit.Test;
import com.montevar.read.FileReader;
import com.montevar.read.HistoryReader;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests for {@link HistoryReader}.
 *
 */
public class HistoryReaderTest extends HistoryTest {

	Stream<String> stockNotFoundResult = Stream.of(
			"<!doctype html public \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">",
			"<html><head><title>Yahoo! - 404 Not Found</title><style>", "/* nn4 hide */ ", "/*/*/", "body {f...");

	Stream<Path> pathsStream = Stream.of(Paths.get("foo"), Paths.get("bar"));

	/**
	 * {@link HistoryReader#readInvestingDotComHistory(String)}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadInvestingDotComHistory() throws IOException {
		FileReader spyFileReader = spy(FileReader.class);
		HistoryReader historyReader = new HistoryReader(spyFileReader);

		doReturn(this.getInvestingDotComStream()).doReturn(this.getInvestingDotComStream()).when(spyFileReader)
				.readFile(anyString());

		TreeMap<DateTime, Double> values = historyReader.readInvestingDotComHistory("any_string").get();
		assertEquals(values.firstEntry().getValue().doubleValue(), 45.41, 0.0);

	}

	/**
	 * {@link HistoryReader#readYahooHistory(String)}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadYahooHistory() throws IOException {
		FileReader spyFileReader = spy(FileReader.class);
		HistoryReader historyReader = new HistoryReader(spyFileReader);

		doReturn(this.getYahooStream()).doReturn(this.getYahooStream()).when(spyFileReader).readFile(anyString());

		TreeMap<DateTime, Double> historys = historyReader.readYahooHistory("any_string").get();
		assertEquals(historys.firstEntry().getValue().doubleValue(), 95.699997, 0.0);
	}

	@Test
	public void testReadMalformedYahooHistory() {
		FileReader spyFileReader = spy(FileReader.class);
		HistoryReader historyReader = new HistoryReader(spyFileReader);
		// Return malformed data.
		doReturn(stockNotFoundResult).when(spyFileReader).readFile(anyString());
		// Assert the Optional object is not present.
		assertTrue(!historyReader.readYahooHistory("any_string").isPresent());
	}

	/**
	 * {@link HistoryReader#loadYahooStocks()}
	 */
	@Test
	public void testLoadStocks() {
		FileReader spyFileReader = spy(FileReader.class);
		HistoryReader historyReader = new HistoryReader(spyFileReader);

		doReturn(this.getYahooStream()).doReturn(this.getYahooStream()).doReturn(this.getSecondYahooStream())
				.doReturn(this.getSecondYahooStream()).when(spyFileReader).readFile(anyString());

		doReturn(pathsStream).when(spyFileReader).getFilePaths(anyString());

		List<TreeMap<DateTime, Double>> stocks = historyReader.loadYahooStocks().collect(Collectors.toList());
		assertEquals(stocks.get(0).firstEntry().getValue().doubleValue(), 95.699997, 0.0);
		assertEquals(stocks.get(1).firstEntry().getValue().doubleValue(), 85.699997, 0.0);
	}

}
