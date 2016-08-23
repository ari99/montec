package com.montevar.monte;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.spark.api.java.function.FlatMapFunction;

/**
 * Uses
 * {@link org.apache.commons.math3.distribution.MultivariateNormalDistribution}
 * to predict the return of a trial based on a single random number seed, feature
 * means, and feature covariances.
 *
 */
public class TrialReturnFunction implements FlatMapFunction<Long, Double> {
	private static final long serialVersionUID = 4726548419395006538L;
	private int numTrials;
	private List<double[]> featureWeights;
	private double[] featureMeans;
	private double[][] featureCovariances;

	public TrialReturnFunction(int numTrials, List<double[]> featureWeights, double[] featureMeans,
			double[][] featureCovariances) {
		this.numTrials = numTrials;
		this.featureWeights = featureWeights;
		this.featureMeans = featureMeans;
		this.featureCovariances = featureCovariances;
	}

	/**
	 * The entry point for a {@link FlatMapFunction} object.
	 */
	@Override
	public Iterator<Double> call(Long seed) throws Exception {
		Double[] trialReturns = this.trialReturns(seed);

		return Arrays.asList(trialReturns).iterator();
	}

	/**
	 * Creates an array of returns from randomized trials.
	 * 
	 * @param seed
	 *            Seed to create a random.
	 * @return Double array of trial returns.
	 */
	private Double[] trialReturns(long seed) {
		MersenneTwister rand = new MersenneTwister(seed);
		MultivariateNormalDistribution multivariateNormal = new MultivariateNormalDistribution(rand, featureMeans,
				featureCovariances);

		Double[] trialReturns = new Double[numTrials];
		for (int i = 0; i < numTrials; i++) {
			double[] trial = multivariateNormal.sample();
			trialReturns[i] = averageTrialReturnPrediction(trial);

		}
		return trialReturns;

	}

	/**
	 * Calculates the average of returns for each feature weight array and a
	 * single trial.
	 * 
	 * @param trial
	 * @return The average.
	 */
	private double averageTrialReturnPrediction(double[] trial) {
		double totalReturn = 0.0;
		for (double[] featureWeight : featureWeights) {
			totalReturn += singleTrialReturnPrediction(featureWeight, trial);
		}
		return totalReturn / featureWeights.size();
	}

	/**
	 * Calculates the return for a single feature weight array and trial.
	 * 
	 * @param featureWeight
	 * @param trial
	 * @return The predicted return for this trial and weights combination.
	 */
	private Double singleTrialReturnPrediction(double[] featureWeight, double[] trial) {
		double instrumentTrialReturn = featureWeight[0];
		for (int i = 0; i < trial.length; i++) {
			instrumentTrialReturn += trial[i] * featureWeight[i + 1];
		}
		return instrumentTrialReturn;
	}

}
