package com.montevar.monte;

import static org.junit.Assert.*;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.apache.spark.broadcast.Broadcast;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import com.google.common.collect.Iterators;
import com.montevar.AppConfig;
import com.montevar.preprocessing.FeatureAggregator;

/**
 * Tests for {@link TrialReturnFunction}.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class TrialReturnFunctionTest extends TrialTest {
	private FeatureAggregator featureAggregator;
	private RelationshipGenerator relationshipGenerator;
	private RegressionModel modeler;

	@Inject
	public void setDependencies(FeatureAggregator featureAggregator, RelationshipGenerator relationshipGenerator,
			RegressionModel modeler) {
		this.featureAggregator = featureAggregator;
		this.relationshipGenerator = relationshipGenerator;
		this.modeler = modeler;
	}

	/**
	 * {@link TrialReturnFunction#call(Long)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTrialReturnFunction() throws Exception {
		List<List<Double>> stocksReturns = this.makeStubReturns(300);
		List<List<Double>> featureReturns = this.makeStubReturns(100);
		double[] featureMeans = this.featureAggregator.makeFeatureMeans(featureReturns);
		double[][] featureMatrix = this.featureAggregator.makeFeatureMatrix(featureReturns);
		RelationshipStats relationshipStats = this.relationshipGenerator.makeRelationshipStats(featureMatrix);
		double[][] featureCovariances = relationshipStats.getCovariances();
		Broadcast<List<double[]>> featureWeights = this.modeler.makeFeatureWeights(stocksReturns, featureMatrix);

		TrialReturnFunction func = new TrialReturnFunction(TrialGenerator.NUM_TRIALS, featureWeights.getValue(),
				featureMeans, featureCovariances);
		long seed = 1493;
		Iterator<Double> returns = func.call(seed);

		int size = Iterators.size(returns);
		assertNotNull(returns);
		assertEquals(TrialGenerator.NUM_TRIALS, size);

	}

}
