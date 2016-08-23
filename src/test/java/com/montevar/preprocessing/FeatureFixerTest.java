package com.montevar.preprocessing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import java.util.ArrayList;
import java.util.TreeMap;
import org.joda.time.DateTime;
import org.junit.Test;
import com.montevar.preprocessing.FeatureFixer;
import com.montevar.read.FileReader;
import com.montevar.read.HistoryReader;
import com.montevar.read.HistoryTest;

/**
 * Tests for {@link FeatureFixer}.
 *
 */
public class FeatureFixerTest extends HistoryTest {

	/**
	 * {@link FeatureFixer#fillInHistory(TreeMap, DateTime, DateTime)}
	 */
	@Test
	public void testFillInHistory() {
		FileReader spyFileReader = spy(FileReader.class);
		HistoryReader historyReader = new HistoryReader(spyFileReader);
		// Have the spy return a stream missing a day.
		doReturn(this.getMissingDayYahooStream()).doReturn(this.getMissingDayYahooStream()).when(spyFileReader)
				.readFile(anyString());
		TreeMap<DateTime, Double> history = historyReader.readYahooHistory("any_string").get();
		DateTime start = new DateTime(2016, 7, 10, 0, 0);
		DateTime end = new DateTime(2016, 7, 13, 0, 0);
		DateTime missing = new DateTime(2016, 7, 12, 0, 0);

		assertTrue(!history.containsKey(missing));
		// Fill in missing data.
		FeatureFixer featureFixer = new FeatureFixer();
		history = featureFixer.fillInHistory(history, start, end);

		// Confirm missing data is equal to the previous days value.
		assertEquals(history.get(missing), 86.75, 0.0);

	}

	/**
	 * {@link FeatureFixer#trimToRegion(TreeMap, DateTime, DateTime)}
	 */
	@Test
	public void testTrimToRegion() {
		FileReader spyFileReader = spy(FileReader.class);
		HistoryReader historyReader = new HistoryReader(spyFileReader);
		doReturn(this.getYahooStream()).doReturn(this.getYahooStream()).when(spyFileReader).readFile(anyString());
		TreeMap<DateTime, Double> history = historyReader.readYahooHistory("any_string").get();
		DateTime start = new DateTime(2016, 7, 11, 0, 0);
		DateTime end = new DateTime(2016, 7, 13, 0, 0);

		FeatureFixer featureFixer = new FeatureFixer();

		// Trim
		history = featureFixer.trimToRegion(history, start, end);

		// Confirm results
		DateTime resultStart = history.firstKey();
		DateTime resultEnd = history.lastKey();

		assertEquals(start, resultStart);
		assertEquals(end, resultEnd);

	}

	/**
	 * {@link FeatureFixer#twoWeekReturns(TreeMap)}
	 */
	@Test
	public void testTwoWeekReturns() {
		FeatureFixer featureFixer = new FeatureFixer();
		TreeMap<DateTime, Double> history = this.createHistoryStub();
		// Convert price history to two week returns.
		ArrayList<Double> returns = featureFixer.twoWeekReturns(history);

		assertEquals(returns.get(0), 9, 0);
		assertEquals(returns.get(1), .81, .01);
	}

	/**
	 * Create stub history data.
	 */
	protected TreeMap<DateTime, Double> createHistoryStub() {
		TreeMap<DateTime, Double> history = new TreeMap<DateTime, Double>();
		DateTime current = new DateTime(2010, 10, 23, 0, 0);
		DateTime end = new DateTime(2015, 10, 23, 0, 0);
		double value = 1.0;
		while (!current.isAfter(end)) {
			history.put(current, value);
			value += 1;
			current = current.plusDays(1);
		}

		return history;
	}
}
