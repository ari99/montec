package com.montevar.preprocessing;

import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link FeatureMaker}.
 *
 */
public class FeatureMakerTest {

	private static ArrayList<Double> returns = new ArrayList<>();
	FeatureMaker featureMaker = new FeatureMaker();

	@BeforeClass
	public static void setup() {
		returns.add(.16);
		returns.add(4.0);
		returns.add(9.0);
	}

	/**
	 * {@link FeatureMaker#addFeatures(ArrayList)}
	 */
	@Test
	public void testAddFeatures() {
		List<List<Double>> allFeatures = this.featureMaker.addFeatures(returns);
		assertEquals(3, allFeatures.size());
		assertEquals(.0256, allFeatures.get(0).get(0), .00001);
		assertEquals(.4, allFeatures.get(1).get(0), .00001);
		assertEquals(.16, allFeatures.get(2).get(0), .00001);

	}

	/**
	 * {@link FeatureMaker#squareReturns(ArrayList)}
	 */
	@Test
	public void testSquareReturns() {
		List<Double> squareds = this.featureMaker.squareReturns(returns);
		assertEquals(.0256, squareds.get(0), .00001);
		assertEquals(16.0, squareds.get(1), .00001);
	}

	/**
	 * {@link FeatureMaker#squareRootReturns(ArrayList)}
	 */
	@Test
	public void testSquareRootsReturns() {
		List<Double> squareds = this.featureMaker.squareRootReturns(returns);
		assertEquals(.4, squareds.get(0), .00001);
		assertEquals(2.0, squareds.get(1), .00001);
	}

}
