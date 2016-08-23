package com.montevar.preprocessing;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for {@link FeatureAggregator}.
 */
public class FeatureAggregatorTest {
	private static final double INSTRUMENT_1_VALUE = 2.0;
	private static final double INSTRUMENT_2_VALUE = 3.0;

	/**
	 * {@link FeatureAggregator#makeFeatureMeans(List)}
	 */
	@Test
	public void testMakeFactorMeans() {
		List<List<Double>> combinedReturns = this.makeLists();
		FeatureAggregator featureAggregator = new FeatureAggregator();

		// Generate means.
		double[] means = featureAggregator.makeFeatureMeans(combinedReturns);
		// Confirm calculations.
		assertEquals(INSTRUMENT_1_VALUE, means[0], 0);
		assertEquals(INSTRUMENT_2_VALUE, means[1], 0);

	}

	/**
	 * {@link FeatureAggregator#makeFeatureMatrix(List)}
	 */
	@Test
	public void testMakeFeatureMatrix() {
		List<List<Double>> combinedReturns = this.makeLists();

		FeatureAggregator featureAggregator = new FeatureAggregator();

		double[][] matrix = featureAggregator.makeFeatureMatrix(combinedReturns);
		int columnLength = matrix.length;
		int rowLength = matrix[0].length;

		// Confirm matrix dimensions.
		assertEquals(4, columnLength);
		assertEquals(2, rowLength);

		// Confirm matrix values.
		assertEquals(INSTRUMENT_1_VALUE, matrix[0][0], 0);
		assertEquals(INSTRUMENT_1_VALUE, matrix[1][0], 0);
		assertEquals(INSTRUMENT_2_VALUE, matrix[0][1], 0);
		assertEquals(INSTRUMENT_2_VALUE, matrix[1][1], 0);

	}

	/**
	 * Make stub lists of values.
	 */
	private List<List<Double>> makeLists() {
		List<Double> returns1 = new ArrayList<>();
		returns1.add(INSTRUMENT_1_VALUE);
		returns1.add(INSTRUMENT_1_VALUE);
		returns1.add(INSTRUMENT_1_VALUE);
		returns1.add(INSTRUMENT_1_VALUE);

		List<Double> returns2 = new ArrayList<>();
		returns2.add(INSTRUMENT_2_VALUE);
		returns2.add(INSTRUMENT_2_VALUE);
		returns2.add(INSTRUMENT_2_VALUE);
		returns2.add(INSTRUMENT_2_VALUE);

		List<List<Double>> combinedReturns = new ArrayList<List<Double>>();
		combinedReturns.add(returns1);
		combinedReturns.add(returns2);
		return combinedReturns;
	}
}
