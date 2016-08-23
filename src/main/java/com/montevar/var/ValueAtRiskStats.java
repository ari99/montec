package com.montevar.var;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Holds the results of value at risk calculations.
 *
 */
public class ValueAtRiskStats {
	private double valueAtRisk;
	private double conditionalValueAtRisk;
	private Pair<Double, Double> varConfidenceInterval;
	private Pair<Double, Double> conditionalVarConfidenceInterval;

	private ValueAtRiskStats(double valueAtRisk, double conditionalValueAtRisk,
			Pair<Double, Double> varConfidenceInterval, Pair<Double, Double> conditionalVarConfidenceInterval) {
		this.valueAtRisk = valueAtRisk;
		this.conditionalValueAtRisk = conditionalValueAtRisk;
		this.varConfidenceInterval = varConfidenceInterval;
		this.conditionalVarConfidenceInterval = conditionalVarConfidenceInterval;

	}

	public double getValueAtRisk() {
		return valueAtRisk;
	}

	public double getConditionalValueAtRisk() {
		return conditionalValueAtRisk;
	}

	public Pair<Double, Double> getVarConfidenceInterval() {
		return varConfidenceInterval;
	}

	public Pair<Double, Double> getConditionalVarConfidenceInterval() {
		return conditionalVarConfidenceInterval;
	}

	public static class ValueAtRiskStatsBuilder {
		private double valueAtRisk;
		private double conditionalValueAtRisk;
		private Pair<Double, Double> varConfidenceInterval;
		private Pair<Double, Double> conditionalVarConfidenceInterval;

		public ValueAtRiskStatsBuilder valueAtRisk(double valueAtRisk) {
			this.valueAtRisk = valueAtRisk;
			return this;
		}

		public ValueAtRiskStatsBuilder conditionalValueAtRisk(double conditionalValueAtRisk) {
			this.conditionalValueAtRisk = conditionalValueAtRisk;
			return this;

		}

		public ValueAtRiskStatsBuilder varConfidenceInterval(Pair<Double, Double> varConfidenceInterval) {
			this.varConfidenceInterval = varConfidenceInterval;
			return this;

		}

		public ValueAtRiskStatsBuilder conditionalVarConfidenceInterval(
				Pair<Double, Double> conditionalVarConfidenceInterval) {
			this.conditionalVarConfidenceInterval = conditionalVarConfidenceInterval;
			return this;

		}

		public ValueAtRiskStats createValueAtRiskStats() {
			return new ValueAtRiskStats(valueAtRisk, conditionalValueAtRisk, varConfidenceInterval,
					conditionalVarConfidenceInterval);
		}

	}

}
