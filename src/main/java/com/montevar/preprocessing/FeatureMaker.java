package com.montevar.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.apache.commons.math3.analysis.function.Signum;

/**
 * Creates additional features from the original input features.
 *
 */
@Named
public class FeatureMaker {

	public List<List<Double>> addFeatures(ArrayList<Double> returns) {
		List<List<Double>> featurized = new ArrayList<>();
		List<Double> squaredReturns = this.squareReturns(returns);
		List<Double> squareRootReturns = this.squareRootReturns(returns);

		featurized.add(squaredReturns);
		featurized.add(squareRootReturns);
		featurized.add(returns);

		return featurized;
	}

	public List<Double> squareReturns(ArrayList<Double> returns) {
		return returns.stream().map(x -> new Signum().value(x) * x * x).collect(Collectors.toList());
	}

	public List<Double> squareRootReturns(ArrayList<Double> returns) {
		return returns.stream().map(x -> new Signum().value(x) * Math.sqrt(Math.abs(x))).collect(Collectors.toList());
	}
}
