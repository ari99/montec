package com.montevar.monte;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

/**
 * Makes Linear Regression models
 * ({@link org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression})
 * and uses the models to get the weights for prediction calculations.
 *
 */
@Named
public class RegressionModel {
	private JavaSparkContext sc;

	@Inject
	public RegressionModel(JavaSparkContext sc) {
		this.sc = sc;
	}

	/**
	 * Creates a prediction model.
	 * 
	 * @param instrument
	 *            Target data.
	 * @param featureMatrix
	 *            Feature data.
	 * @return {@link OLSMultipleLinearRegression} model.
	 */
	private OLSMultipleLinearRegression makeModel(List<Double> instrument, double[][] featureMatrix) {
		OLSMultipleLinearRegression model = new OLSMultipleLinearRegression();
		Double[] instrumentArray = instrument.toArray(new Double[instrument.size()]);
		model.newSampleData(ArrayUtils.toPrimitive(instrumentArray), featureMatrix);
		return model;
	}

	/**
	 * Creates prediction models and determines the feature weights from them.
	 * 
	 * @param stockReturns
	 *            Target variables.
	 * @param featureMatrix
	 *            Feature variables.
	 * @return A List containing one weight array for each stock return.
	 */
	public Broadcast<List<double[]>> makeFeatureWeights(List<List<Double>> stockReturns, double[][] featureMatrix) {
		Stream<OLSMultipleLinearRegression> models = stockReturns.stream().map(x -> this.makeModel(x, featureMatrix));

		List<double[]> featureWeights = models.map(x -> x.estimateRegressionParameters()).collect(Collectors.toList());
		return sc.broadcast(featureWeights);

	}
}
