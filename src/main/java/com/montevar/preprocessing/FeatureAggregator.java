package com.montevar.preprocessing;

import java.util.List;

import java.util.stream.Stream;

import javax.inject.Named;

import org.apache.commons.lang.ArrayUtils;

/**
 * Aggregates and transforms collections.
 *
 */
@Named
public class FeatureAggregator {
	/**
	 * Average returns.
	 * 
	 * @param featureReturns
	 * @return Array of averages.
	 */
	public double[] makeFeatureMeans(List<List<Double>> featureReturns) {

		Double[] featureMeans = featureReturns.stream()
				.map(feature -> feature.stream().mapToDouble(Double::doubleValue).average().getAsDouble())
				.toArray(Double[]::new);
		return ArrayUtils.toPrimitive(featureMeans);
	}

	/**
	 * Create two dimensional array from a List of Lists. The two dimensional
	 * array is needed for prediction models.
	 * 
	 * @param featureReturns
	 * @return Two dimensional array.
	 */
	public double[][] makeFeatureMatrix(List<List<Double>> featureReturns) {

		Stream<Double[]> featureArrays = featureReturns.stream().map(x -> x.toArray(new Double[x.size()]));

		Double[][] allFeaturesMatrix = featureArrays.toArray(Double[][]::new);

		// Need to pivot the matrix for the regression model
		double[][] primitiveFeaturesMatrix = new double[allFeaturesMatrix[0].length][allFeaturesMatrix.length];

		for (int row = 0; row < allFeaturesMatrix[0].length; row++) {
			for (int col = 0; col < allFeaturesMatrix.length; col++) {
				primitiveFeaturesMatrix[row][col] = allFeaturesMatrix[col][row];
			}
		}
		return primitiveFeaturesMatrix;
	}

}
