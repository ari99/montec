package com.montevar.monte;

import javax.inject.Named;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 * Creates the covariance and correlation statistics and saves them in a
 * RelationshipStats object ({@link RelationshipStats}).
 *
 */
@Named
public class RelationshipGenerator {

	public RelationshipStats makeRelationshipStats(double[][] featuresMatrix) {
		double[][] featureCovariances = new Covariance(featuresMatrix).getCovarianceMatrix().getData();
		double[][] featureCorrelations = new PearsonsCorrelation(featuresMatrix).getCorrelationMatrix().getData();
		RelationshipStats stats = new RelationshipStats(featureCovariances, featureCorrelations);
		return stats;
	}
}
