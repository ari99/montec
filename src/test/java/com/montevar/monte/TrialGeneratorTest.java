package com.montevar.monte;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.montevar.AppConfig;
import com.montevar.preprocessing.FeatureAggregator;

/**
 * Tests for {@link TrialGenerator}.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class TrialGeneratorTest extends TrialTest {

	private TrialGenerator trialGenerator;
	private FeatureAggregator featureAggregator;
	private RelationshipGenerator relationshipGenerator;

	@Inject
	public void setDependencies(TrialGenerator generator, FeatureAggregator featureAggregator,
			RelationshipGenerator relationshipGenerator) {
		this.trialGenerator = generator;
		this.featureAggregator = featureAggregator;
		this.relationshipGenerator = relationshipGenerator;
	}

	/**
	 * {@link TrialGenerator#makeTrialReturnPredictions(List, List, double[][], double[][])}
	 */
	@Test
	public void testMakeTrialReturnPredictions() {
		List<List<Double>> stockReturns = this.makeStubReturns(300);
		List<List<Double>> featureReturns = this.makeStubReturns(100);
		double[][] featureMatrix = this.featureAggregator.makeFeatureMatrix(featureReturns);
		RelationshipStats relationshipStats = this.relationshipGenerator.makeRelationshipStats(featureMatrix);
		double[][] featureCovariances = relationshipStats.getCovariances();

		JavaRDD<Double> trials = this.trialGenerator.makeTrialReturnPredictions(stockReturns, featureReturns,
				featureMatrix, featureCovariances);
		List<Double> trialsList = trials.collect();

		assertEquals(TrialGenerator.NUM_TRIALS, trialsList.size());
	}

}
