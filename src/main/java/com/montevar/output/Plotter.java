package com.montevar.output;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.apache.spark.mllib.stat.KernelDensity;

/**
 * Creates and displays charts to visualize data.
 *
 */
@Named
public class Plotter {
	private JavaSparkContext sc;

	@Inject
	public Plotter(JavaSparkContext sc) {
		this.sc = sc;
	}

	public void doPlots(JavaRDD<Double> trials, List<List<Double>> featureReturns) {
		this.plotRddDistribution(trials, "Trials");
		this.plotListDistribution(featureReturns.get(2), sc.parallelize(featureReturns.get(2)), "S&P 500 Returns");
		this.plotListDistribution(featureReturns.get(5), sc.parallelize(featureReturns.get(5)), "Crude Oil Returns");
	}

	private void plot(double[] xData, double[] yData, String plotName) {

		XYChart chart = QuickChart.getChart(plotName, "Two Week Return", "Density", "KernelDensity(x)", xData,
				yData);

		new SwingWrapper(chart).displayChart();
	}

	public void plotRddDistribution(JavaRDD<Double> samples, String plotName) {

		List<Double> samplesList = samples.collect();
		this.plotListDistribution(samplesList, samples, plotName);

	}

	/**
	 * Plot probability density of input samples.
	 * https://en.wikipedia.org/wiki/Probability_density_function
	 * 
	 * @param samplesList
	 *            Samples in a List.
	 * @param samplesRdd
	 *            Samples in a JavaRDD.
	 * @param plotName
	 *            Name to put at the top of the plot.
	 */
	public void plotListDistribution(List<Double> samplesList, JavaRDD<Double> samplesRdd, String plotName) {

		DescriptiveStatistics stats = this.makeStats(samplesList);

		double max = stats.getMax();
		double min = stats.getMin();
		double[] domain = this.makeRange(min, max, (max - min) / 100);

		double bandwidth = this.chooseBandwidth(stats);

		// Calculate probability density
		KernelDensity kd = new KernelDensity().setSample(samplesRdd).setBandwidth(bandwidth);
		double[] densities = kd.estimate(domain);

		this.plot(domain, densities, plotName);
	}

	/**
	 * Creates a {@link DescriptiveStatistics} object from a list of Double's.
	 * 
	 * @param samplesList
	 *            Input data.
	 * @return Resulting statistics.
	 */
	private DescriptiveStatistics makeStats(List<Double> samplesList) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (Double value : samplesList) {
			stats.addValue(value);
		}
		return stats;
	}

	/**
	 * Approximates a good bandwidth for KernelDensity calculations.
	 * 
	 * @param stats
	 * @return Bandwidth.
	 */
	public double chooseBandwidth(DescriptiveStatistics stats) {
		long size = stats.getN();
		double std = stats.getStandardDeviation();
		double bandwidth = 1.06 * std * Math.pow(size, -.2);
		return bandwidth;
	}

	/**
	 * Creates an array of doubles between two numbers.
	 * 
	 * @param min
	 * @param max
	 * @param step
	 * @return
	 */
	public double[] makeRange(double min, double max, double step) {
		ArrayList<Double> list = new ArrayList<>();
		for (double i = min; i <= max; i += step) {
			list.add(i);
		}
		return ArrayUtils.toPrimitive(list.toArray(new Double[0]));
	}
}
