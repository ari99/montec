package com.montevar;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.montevar.monte.RelationshipStats;
import com.montevar.monte.RelationshipGenerator;
import com.montevar.monte.TrialGenerator;
import com.montevar.output.Plotter;
import com.montevar.output.TextOutput;
import com.montevar.preprocessing.FeatureAggregator;
import com.montevar.read.ReturnGenerator;
import com.montevar.var.ValueAtRiskGenerator;
import com.montevar.var.ValueAtRiskStats;

import org.apache.spark.api.java.JavaRDD;

/**
 * App class is the main controller of the project. Loads the data, does the
 * calculations, and produces the output.
 *
 */
@Named
public class App {
	private Plotter plotter;
	private ReturnGenerator returnGenerator;
	private FeatureAggregator featureAggregator;
	private TextOutput textOutput;
	private TrialGenerator trialGenerator;
	private ValueAtRiskGenerator valueAtRiskGenerator;
	private RelationshipGenerator relationshipGenerator;

	@Inject
	public App(Plotter plotter, ReturnGenerator returnGenerator, TextOutput textOutput, TrialGenerator trialGenerator,
			ValueAtRiskGenerator valueAtRiskGenerator, RelationshipGenerator relationshipGenerator,
			FeatureAggregator featureAggregator) {

		this.plotter = plotter;
		this.returnGenerator = returnGenerator;
		this.textOutput = textOutput;
		this.trialGenerator = trialGenerator;
		this.valueAtRiskGenerator = valueAtRiskGenerator;
		this.relationshipGenerator = relationshipGenerator;
		this.featureAggregator = featureAggregator;

	}

	/**
	 * Controls the main flow of logic.
	 */
	public void start() {
		List<List<Double>> stockReturns = this.returnGenerator.makeStockReturns();
		List<List<Double>> featureReturns = this.returnGenerator.makeFeatureReturns();
		double[][] featureMatrix = this.featureAggregator.makeFeatureMatrix(featureReturns);

		RelationshipStats relationshipStats = this.relationshipGenerator.makeRelationshipStats(featureMatrix);
		JavaRDD<Double> trials = this.trialGenerator.makeTrialReturnPredictions(stockReturns, featureReturns,
				featureMatrix, relationshipStats.getCovariances());
		ValueAtRiskStats varStats = this.valueAtRiskGenerator.makeValueAtRiskStats(trials);

		this.doOutput(trials, featureReturns, varStats, relationshipStats);

	}

	/**
	 * Outputs the results.
	 * 
	 * @param trials
	 *            The results of the Monte Carlo simulation.
	 * @param featureReturns
	 * @param varStats
	 *            {@link ValueAtRiskStats}
	 * @param relationshipStats
	 *            {@link RelationshipStats}
	 */
	private void doOutput(JavaRDD<Double> trials, List<List<Double>> featureReturns, ValueAtRiskStats varStats,
			RelationshipStats relationshipStats) {
		this.plotter.doPlots(trials, featureReturns);
		this.textOutput.outputValueAtRiskStats(varStats);
		this.textOutput.outputRelationshipStats(relationshipStats);

	}

}
