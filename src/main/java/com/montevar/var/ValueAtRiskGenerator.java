package com.montevar.var;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;

/**
 * Uses {@link StandardValueAtRiskCalculator} and
 * {@link ConditionalValueAtRiskCalculator} to build {@link ValueAtRiskStats}.
 * 
 */
@Named
public class ValueAtRiskGenerator {
	private static final Logger logger = LogManager.getLogger();

	private StandardValueAtRiskCalculator standardValueAtRiskCalculator;
	private ConditionalValueAtRiskCalculator conditionalValueAtRiskCalculator;
	private static final int NUM_RESAMPLES = 100;
	private static final double P_VALUE = .05;

	@Inject
	public ValueAtRiskGenerator(StandardValueAtRiskCalculator standardValueAtRiskCalculator,
			ConditionalValueAtRiskCalculator conditionalValueAtRiskCalculator) {
		this.standardValueAtRiskCalculator = standardValueAtRiskCalculator;
		this.conditionalValueAtRiskCalculator = conditionalValueAtRiskCalculator;
	}

	public ValueAtRiskStats makeValueAtRiskStats(JavaRDD<Double> trials) {
		logger.debug("Calculating VaR");
		double valueAtRisk = this.standardValueAtRiskCalculator.calculateValueAtRisk(trials);
		double conditionalValueAtRisk = this.conditionalValueAtRiskCalculator.calculateValueAtRisk(trials);

		logger.debug("Calculating confidence intervals");
		Pair<Double, Double> varConfidenceInterval = this.standardValueAtRiskCalculator
				.bootstrappedConfidenceInterval(trials, NUM_RESAMPLES, P_VALUE);
		Pair<Double, Double> conditionalVarConfidenceInterval = this.conditionalValueAtRiskCalculator
				.bootstrappedConfidenceInterval(trials, NUM_RESAMPLES, P_VALUE);

		ValueAtRiskStats stats = new ValueAtRiskStats.ValueAtRiskStatsBuilder().valueAtRisk(valueAtRisk)
				.conditionalValueAtRisk(conditionalValueAtRisk).varConfidenceInterval(varConfidenceInterval)
				.conditionalVarConfidenceInterval(conditionalVarConfidenceInterval).createValueAtRiskStats();

		return stats;

	}
}
