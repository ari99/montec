package com.montevar.output;

import javax.inject.Named;

import com.montevar.monte.RelationshipStats;
import com.montevar.var.ValueAtRiskStats;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Outputs text results.
 *
 */
@Named
public class TextOutput {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Outputs a two dimensional array in grid form.
	 * 
	 * @param label
	 * @param grid
	 */
	private void printMatrix(String label, double[][] grid) {
		logger.info("\n"+label+"\n");
		for (int r = 0; r < grid.length; r++) {
			for (int c = 0; c < grid[r].length; c++) {
				logger.printf(Level.INFO, "%.4f           ", grid[r][c]);
			}
			logger.info("\n");
		}
	}

	/**
	 * Creates output from {@link ValueAtRiskStats}.
	 * 
	 * @param stats
	 */
	public void outputValueAtRiskStats(ValueAtRiskStats stats) {
		logger.info("VaR: {} \n", stats.getValueAtRisk());
		logger.info("Conditional VaR: {} \n", stats.getConditionalValueAtRisk());
		logger.info("VaR confidence interval: {} \n", stats.getVarConfidenceInterval());
		logger.info("CVaR confidence interval: {} \n", stats.getConditionalVarConfidenceInterval());
	}

	/**
	 * Creates output from {@link RelationshipStats}.
	 * 
	 * @param stats
	 */
	public void outputRelationshipStats(RelationshipStats stats) {
		this.printMatrix("Feature correlation", stats.getCorrelations());
		this.printMatrix("Feature covariance", stats.getCovariances());
	}

}
