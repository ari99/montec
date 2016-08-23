package com.montevar.var;

import java.util.ArrayList;

import javax.inject.Inject;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.montevar.AppConfig;

import org.apache.commons.lang3.tuple.Pair;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ValueAtRiskCalculator}.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class ValueAtRiskCalculatorTest {

	private static JavaSparkContext sc;
	private static JavaRDD<Double> distributedData;

	@Inject
	public void setDependencies(JavaSparkContext injectedSparkConext){
		ValueAtRiskCalculatorTest.sc = injectedSparkConext;
		distributedData = makeDataRDD();
	}
	
	/**
	 * {@link ConditionalValueAtRiskCalculator#calculateValueAtRisk(JavaRDD)}
	 */
	@Test
	public void testConditionalCalculateValueAtRisk() {
		ConditionalValueAtRiskCalculator calc = new ConditionalValueAtRiskCalculator();
		double var = calc.calculateValueAtRisk(distributedData);

		assertEquals(25.5, var, 0);
	}

	/**
	 * {@link StandardValueAtRiskCalculator#calculateValueAtRisk(JavaRDD)}
	 */
	@Test
	public void testStandardCalculateValueAtRisk() {
		StandardValueAtRiskCalculator calc = new StandardValueAtRiskCalculator();
		double var = calc.calculateValueAtRisk(distributedData);

		assertEquals(50, var, 0);
	}

	/**
	 * {@link ValueAtRiskCalculator#bootstrappedConfidenceInterval(JavaRDD, int, double)}
	 */
	@Test
	public void testBoostrappedConfidenceInterval() {
		StandardValueAtRiskCalculator spyCalc = spy(StandardValueAtRiskCalculator.class);
		// Return a double and increment the value of the double every time
		// calculateValueAtRisk is called.
		doAnswer(new Answer<Double>() {
			private Double count = 0.0;

			public Double answer(InvocationOnMock invocation) {
				count++;
				return count;
			}
		}).when(spyCalc).calculateValueAtRisk(anyObject());

		Pair<Double, Double> interval = spyCalc.bootstrappedConfidenceInterval(distributedData, 100, .05);

		assertEquals(Pair.of(2.0, 99.0), interval);
	}

	private static ArrayList<Double> makeDataList() {
		ArrayList<Double> data = new ArrayList<>();

		for (double i = 1; i <= 1000; i++) {
			data.add(i);
		}

		return data;
	}

	private static JavaRDD<Double> makeDataRDD() {
		JavaRDD<Double> distData = sc.parallelize(makeDataList());
		return distData;
	}

}
