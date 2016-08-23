package com.montevar.monte;

/**
 * Holds covariance and correlation data.
 *
 */
public class RelationshipStats {
	private double[][] covariances;
	private double[][] correlations;

	public RelationshipStats(double[][] covariances, double[][] correlations) {
		this.covariances = covariances;
		this.correlations = correlations;
	}

	public double[][] getCovariances() {
		return covariances;
	}

	public double[][] getCorrelations() {
		return correlations;
	}

}
