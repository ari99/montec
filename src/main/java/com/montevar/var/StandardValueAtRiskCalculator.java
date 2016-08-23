package com.montevar.var;

import java.util.List;

import javax.inject.Named;

import org.apache.spark.api.java.JavaRDD;

/**
 * Calculates 5% value at risk.
 * 
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Value_at_risk">https://en.wikipedia.org/wiki/Value_at_risk</a>
 *
 */
@Named
public class StandardValueAtRiskCalculator extends ValueAtRiskCalculator {

	@Override
	public Double calculateValueAtRisk(JavaRDD<Double> trials) {
		List<Double> topLosses = trials.takeOrdered(Math.max(((int) trials.count()) / 20, 1));
		return topLosses.get(topLosses.size() - 1);
	}
}
