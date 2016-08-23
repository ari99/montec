package com.montevar.read;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;

import com.montevar.preprocessing.FeatureFixer;
import com.montevar.preprocessing.FeatureMaker;

/**
 * Reads and cleans the input data. Converts the data into two week time
 * intervals.
 *
 */
@Named
public class ReturnGenerator {
	private HistoryReader reader;
	private FeatureFixer featureFixer;
	private FeatureMaker featureMaker;
	public static final DateTime START_DATE = new DateTime(2010, 10, 11, 0, 0);
	public static final DateTime END_DATE = new DateTime(2015, 10, 11, 0, 0);

	@Inject
	public ReturnGenerator(HistoryReader reader, FeatureFixer featureFixer, FeatureMaker featureMaker) {
		this.reader = reader;
		this.featureMaker = featureMaker;
		this.featureFixer = featureFixer;
	}

	/**
	 * Make two week returns for the stock data.
	 * 
	 * @return The two week returns.
	 */
	public List<List<Double>> makeStockReturns() {
		Stream<TreeMap<DateTime, Double>> rawStocks = this.reader.loadYahooStocks()
				.filter(history -> history.firstKey().isBefore(START_DATE))
				.filter(history -> history.lastKey().isAfter(END_DATE))
				.filter(history -> history.containsKey(new DateTime(2011, 10, 11, 0, 0)))
				.filter(history -> history.containsKey(new DateTime(2012, 10, 11, 0, 0)))
				.filter(history -> history.containsKey(new DateTime(2013, 10, 11, 0, 0)))
				.filter(history -> history.containsKey(new DateTime(2014, 10, 10, 0, 0)))
				.filter(history -> history.containsKey(new DateTime(2015, 10, 9, 0, 0)));

		Stream<TreeMap<DateTime, Double>> stocks = rawStocks
				.map(rawStock -> this.featureFixer.fillInHistory(rawStock, START_DATE, END_DATE));

		Stream<List<Double>> stocksReturns = stocks.map(this.featureFixer::twoWeekReturns);
		return stocksReturns.collect(Collectors.toList());
	}

	/**
	 * Make two week returns for the feature data.
	 * 
	 * @return The two week returns.
	 */
	public List<List<Double>> makeFeatureReturns() {
		Stream<TreeMap<DateTime, Double>> yahooFeatures = this.reader.loadYahooFeatures();
		Stream<TreeMap<DateTime, Double>> investingFeatures = this.reader.loadInvestingDotComFeatures();
		Stream<TreeMap<DateTime, Double>> rawFeatures = Stream.concat(yahooFeatures, investingFeatures);

		Stream<ArrayList<Double>> featureReturns = rawFeatures
				.map(rawFactor -> this.featureFixer.fillInHistory(rawFactor, START_DATE, END_DATE))
				.map(this.featureFixer::twoWeekReturns);

		List<List<Double>> featureReturnsList = featureReturns.map(x -> this.featureMaker.addFeatures(x))
				.flatMap(x -> x.stream()).collect(Collectors.toList());

		return featureReturnsList;
	}
}
