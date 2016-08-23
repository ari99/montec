package com.montevar.read;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;
import com.montevar.preprocessing.FeatureFixer;
import com.montevar.preprocessing.FeatureMaker;

/**
 * Tests for {@link ReturnGenerator}.
 */
public class ReturnGeneratorTest extends HistoryTest {

	/**
	 * {@link ReturnGenerator#makeStockReturns()}
	 */
	@Test
	public void testMakeStockReturns() {
		HistoryReader mockHistoryReader = mock(HistoryReader.class);
		int numFeatures = 10;
		doReturn(this.makeStubHistory(numFeatures)).when(mockHistoryReader).loadYahooStocks();

		ReturnGenerator returnGenerator = new ReturnGenerator(mockHistoryReader, new FeatureFixer(),
				new FeatureMaker());
		List<List<Double>> stockReturns = returnGenerator.makeStockReturns();
		assertEquals(this.getExpectedTwoWeekIntervals(), stockReturns.get(0).size());
		assertEquals(numFeatures, stockReturns.size());
	}

	/**
	 * {@link ReturnGenerator#makeFeatureReturns()}
	 */
	@Test
	public void testMakeFeatureReturns() {
		HistoryReader mockHistoryReader = mock(HistoryReader.class);
		int numFeatures = 10;
		doReturn(this.makeStubHistory(numFeatures)).when(mockHistoryReader).loadYahooFeatures();
		doReturn(this.makeStubHistory(numFeatures)).when(mockHistoryReader).loadInvestingDotComFeatures();

		ReturnGenerator returnGenerator = new ReturnGenerator(mockHistoryReader, new FeatureFixer(),
				new FeatureMaker());
		List<List<Double>> featureReturns = returnGenerator.makeFeatureReturns();
		assertEquals(this.getExpectedTwoWeekIntervals(), featureReturns.get(0).size());
		// The number of numFeatures is multiplied by 3 because we add a square
		// root feature and squared feature
		// for each original feature .
		assertEquals(numFeatures * 2 * 3, featureReturns.size());
	}

	/**
	 * Calculates the number of two week intervals between two dates.
	 */
	private int getExpectedTwoWeekIntervals() {
		DateTime start = ReturnGenerator.START_DATE;
		DateTime end = ReturnGenerator.END_DATE;
		int days = 0;
		DateTime currentTime = new DateTime(start);
		while (!currentTime.isAfter(end)) {
			int dayOfTheWeek = currentTime.getDayOfWeek();

			if (dayOfTheWeek < 6) {
				days++;
			}
			// Skip weekends
			if (dayOfTheWeek == 5) {
				currentTime = currentTime.plusDays(3);
			} else {
				currentTime = currentTime.plusDays(1);
			}
		}
		return days / 10;
	}

	/**
	 * Makes a stream of TreeMap object to hold fake pricing data.
	 * 
	 * @param numFeatures
	 *            Number of TreeMaps to create. Each TreeMap represents pricing
	 *            data for a security.
	 * @return Stream of TreeMaps holding pricing data.
	 */
	private Stream<TreeMap<DateTime, Double>> makeStubHistory(int numFeatures) {
		DateTime start = ReturnGenerator.START_DATE;
		DateTime end = ReturnGenerator.END_DATE;

		List<TreeMap<DateTime, Double>> history = new ArrayList<>();
		int numDaysPerFeature = Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays() + 10;
		for (int i = 0; i < numFeatures; i++) {
			TreeMap<DateTime, Double> feature = new TreeMap<>();
			for (int c = -2; c < numDaysPerFeature; c++) {
				DateTime current = start.plusDays(c);
				feature.put(current, (double) c + 3);
			}
			history.add(feature);
		}

		return history.stream();
	}
}
