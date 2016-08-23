package com.montevar.monte;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link RelationshipGenerator}.
 *
 */
public class RelationshipGeneratorTest {

	/**
	 * {@link RelationshipGenerator#makeRelationshipStats(double[][])}
	 */
	@Test
	public void testMakeRelationshipStats() {
		RelationshipGenerator generator = new RelationshipGenerator();
		double[][] featuresMatrix = this.makeStubMatrix();

		RelationshipStats stats = generator.makeRelationshipStats(featuresMatrix);

		assertNotNull(stats.getCorrelations());
		assertNotNull(stats.getCovariances());
		assertEquals(10, stats.getCorrelations().length);
		assertEquals(10, stats.getCovariances().length);
	}

	private double[][] makeStubMatrix() {
		double[][] featuresMatrix = new double[10][10];
		for (int i = 0; i < featuresMatrix.length; i++) {
			for (int c = 0; c < featuresMatrix[i].length; c++) {
				featuresMatrix[i][c] = 1;
			}
		}
		return featuresMatrix;
	}

}
