package com.montevar.monte;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

import com.montevar.preprocessing.FeatureAggregator;

/**
 * Runs the actual Monte Carlo simulation to predict the returns of random
 * samples.
 *
 */
@Named
public class TrialGenerator {
	private static final Logger logger = LogManager.getLogger();
	private static final int PARALLESLISM = 10;
	public static final int NUM_TRIALS = 10000;

	private FeatureAggregator featureAggregator;
	private JavaSparkContext sc;
	private RegressionModel modeler;

	@Inject
	public TrialGenerator(JavaSparkContext sc, FeatureAggregator featureAggregator, RegressionModel modeler) {
		this.sc = sc;
		this.featureAggregator = featureAggregator;
		this.modeler = modeler;

	}

	/**
	 * Runs the Monte Carlo simulation. Creates prediction models and determines
	 * weights from them. Creates a JavaRDD of seeds to create randomized
	 * distributions and predicts returns from the distributions.
	 * 
	 * @param stocksReturns
	 *            Target variables for the regression models.
	 * @param featureReturns
	 *            Feature variables for the regression models.
	 * @param featureMatrix
	 * @param featureCovariances
	 * @return Returns of trials.
	 */
	public JavaRDD<Double> makeTrialReturnPredictions(List<List<Double>> stocksReturns,
			List<List<Double>> featureReturns, double[][] featureMatrix, double[][] featureCovariances) {

		double[] featureMeans = this.makeFeatureMeans(featureReturns);

		// Calculate weights.
		Broadcast<List<double[]>> featureWeights = this.modeler.makeFeatureWeights(stocksReturns, featureMatrix);

		JavaRDD<Long> seeds = this.makeSeeds(PARALLESLISM);

		// Calculate predictions.
		logger.debug("Making trial predictions");
		JavaRDD<Double> trialReturnPredictions = seeds.flatMap(new TrialReturnFunction(NUM_TRIALS / PARALLESLISM,
				featureWeights.getValue(), featureMeans, featureCovariances));
		return trialReturnPredictions;
	}

	private double[] makeFeatureMeans(List<List<Double>> featureReturns) {
		logger.debug("Making feature means array from two dimentional list, total size: {} first element size {}",
				featureReturns.size(), featureReturns.get(0).size());
		double[] featureMeans = this.featureAggregator.makeFeatureMeans(featureReturns);
		return featureMeans;
	}

	private JavaRDD<Long> makeSeeds(int parallelism) {
		logger.debug("Making seeds for random number generation.");
		long baseSeed = 1496;
		ArrayList<Long> seeds = new ArrayList<Long>();
		for (long i = baseSeed; i < (baseSeed + parallelism); i++) {
			seeds.add(i);
		}
		return sc.parallelize(seeds, PARALLESLISM);
	}

}
