/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Zhongli Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.webservice.tool.NLPPart.SentiStrengthAPI;

import uk.ac.wlv.sentistrength.SentiStrength;

/**
 * @author Zhongli Li
 *
 */
public class SentiStrengthTest {
	public static void main(String[] args) {
		// Method 1: one-off classification (inefficient for multiple
		// classifications)
		// Create an array of command line parameters, including text or file to
		// process
		// String ssthInitialisationAndText[] = { "sentidata",
		// "lib/SentStrength_Data/", "text", "I+hate+frogs+but+love+dogs.",
		// "explain" };
		// SentiStrength.main(ssthInitialisationAndText);

		// Method 2: One initialisation and repeated classifications
		SentiStrength sentiStrength = new SentiStrength();
		// Create an array of command line parameters to send (not text or file
		// to process)
		String ssthInitialisation[] = { "sentidata", "lib/SentStrength_Data/",
				"trinary" };
		sentiStrength.initialise(ssthInitialisation); // Initialise
		// can now calculate sentiment scores quickly without having to
		// initialise again
		System.out.println(sentiStrength
				.computeSentimentScores("I have a baseball"));
		System.out
				.println(sentiStrength.computeSentimentScores("I love dogs."));
		System.out.println(sentiStrength
				.computeSentimentScores("That hat is too bad ridiculous, Charles."));
	}
}
