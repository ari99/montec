package com.montevar.var;

import java.util.List;

import javax.inject.Named;

import org.apache.spark.api.java.JavaRDD;

/**
 * Calculates 5% conditional value at risk.
 *
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Expected_shortfall">https://en.wikipedia.org/wiki/Expected_shortfall</a>
 */
@Named
public class ConditionalValueAtRiskCalculator extends ValueAtRiskCalculator {

	@Override
	public Double calculateValueAtRisk(JavaRDD<Double> trials) {
		int numToTake = (int) trials.count() / 20;

		List<Double> topLosses = trials.takeOrdered(Math.max(numToTake, 1));
		double sum = topLosses.stream().mapToDouble(Double::doubleValue).sum();
		return sum / topLosses.size();
	}

}
