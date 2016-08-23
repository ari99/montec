package com.montevar.monte;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Provided as a superclass for subclasses that need stub return data.
 */
public class TrialTest {

	protected List<List<Double>> makeStubReturns(int numberOfInstruments) {
		Random randomGenerator = new Random();

		List<List<Double>> returns = new ArrayList<>();
		int numberOfReturnsPerInstrument = 1000;

		for (int i = 0; i < numberOfInstruments; i++) {
			List<Double> instrumentReturn = new ArrayList<>();
			for (double c = 0; c < numberOfReturnsPerInstrument; c++) {
				int randomInt = randomGenerator.nextInt(100);

				instrumentReturn.add(c + randomInt);
			}
			returns.add(instrumentReturn);
		}

		return returns;
	}

}
