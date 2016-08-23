package com.montevar.preprocessing;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 * Fixes and converts input data.
 *
 */
@Named
public class FeatureFixer {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Trim TreeMap to dates between two dates.
	 * 
	 * @param history
	 *            TreeMap of dates mapped to values.
	 * @param start
	 * @param end
	 * @return Trimmed map.
	 */
	public TreeMap<DateTime, Double> trimToRegion(TreeMap<DateTime, Double> history, DateTime start, DateTime end) {
		return new TreeMap<DateTime, Double>(history.subMap(start, true, end, true));
	}

	/**
	 * Get previously known price. Used to fill in missing data for a date with
	 * an unknown price.
	 * 
	 * @param start
	 *            Date with unknown price.
	 * @param history
	 *            Map of prices to values.
	 * @return Previous price.
	 */
	private Double getPreviousPrice(DateTime start, TreeMap<DateTime, Double> history) {
		Double previousPrice = history.get(start);
		int count = 1;
		while (previousPrice == null) {
			previousPrice = history.get(start.minusDays(count));
			if (count > 10) {
				logger.debug("Can't find previous entry");
				break;
			}
			count++;
		}
		return previousPrice;
	}

	/**
	 * Fill in missing data between 'start' and 'end'.
	 * 
	 * @param history
	 * @param start
	 * @param end
	 * @return History with missing data filled in.
	 */
	public TreeMap<DateTime, Double> fillInHistory(TreeMap<DateTime, Double> history, DateTime start, DateTime end) {

		DateTime currentTime = new DateTime(start);
		Double previousPrice = this.getPreviousPrice(start, history);
		TreeMap<DateTime, Double> filledHistory = new TreeMap<DateTime, Double>();

		while (!currentTime.isAfter(end)) {
			int dayOfTheWeek = currentTime.getDayOfWeek();
			Double currentPrice;

			if (dayOfTheWeek < 6) {
				if (history.containsKey(currentTime)) {
					currentPrice = history.get(currentTime);
				} else {
					currentPrice = previousPrice;
				}
				filledHistory.put(currentTime, currentPrice);
				previousPrice = currentPrice;
			}

			// Skip weekends
			if (dayOfTheWeek == 5) {
				currentTime = currentTime.plusDays(3);
			} else {
				currentTime = currentTime.plusDays(1);
			}
		}
		return filledHistory;

	}

	/**
	 * Converts daily price history into two week returns.
	 * 
	 * @param history
	 * @return List of two week returns.
	 */
	public ArrayList<Double> twoWeekReturns(TreeMap<DateTime, Double> history) {

		ArrayList<Double> returns = new ArrayList<>();
		int count = 10;
		Double previous = history.firstEntry().getValue();

		for (Map.Entry<DateTime, Double> entry : history.entrySet()) {
			if (count == 10) {
				previous = entry.getValue();
				count--;
			} else if (count == 1) {
				returns.add((entry.getValue() - previous) / previous);
				count = 10;
			} else {
				count--;
			}
		}
		return returns;

	}
}
