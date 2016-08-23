package com.montevar.var;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Named;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.api.java.JavaRDD;

/**
 * Abstract class to share bootstrappedConfidenceInterval.
 *
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Confidence_interval">https://en.wikipedia.org/wiki/Confidence_interval</a>
 *      <a href=
 *      "https://en.wikipedia.org/wiki/Bootstrapping_(statistics)">https://en.wikipedia.org/wiki/Bootstrapping_(statistics)</a>
 * 
 */
@Named
public abstract class ValueAtRiskCalculator {
	public abstract Double calculateValueAtRisk(JavaRDD<Double> trials);

	public Pair<Double, Double> bootstrappedConfidenceInterval(JavaRDD<Double> trials, int numResamples,
			double pValue) {
		ArrayList<Double> stats = new ArrayList<>();
		for (int i = 0; i < numResamples; i++) {
			JavaRDD<Double> resample = trials.sample(true, 1.0);
			double var = this.calculateValueAtRisk(resample);
			stats.add(var);
		}

		Collections.sort(stats);
		int lowerIndex = (int) (((numResamples * pValue) / 2) - 1);
		int upperIndex = (int) Math.ceil(numResamples * (1 - (pValue / 2)));

		return Pair.of(stats.get(lowerIndex), stats.get(upperIndex));

	}

}
