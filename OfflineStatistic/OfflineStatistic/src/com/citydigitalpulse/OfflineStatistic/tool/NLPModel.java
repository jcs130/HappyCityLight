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
package com.citydigitalpulse.OfflineStatistic.tool;

import uk.ac.wlv.sentistrength.SentiStrength;

/**
 * @author Zhongli Li
 *
 */
public class NLPModel {

	private static SentiStrength sentiStrength = new SentiStrength();

	// 这是静态初始化块
	static {
		String ssthInitialisation[] = { "sentidata", "lib/SentStrength_Data/",
				"trinary" };
		sentiStrength.initialise(ssthInitialisation); // Initialise
	}

	/**
	 * 使用SentiStrenge获得情感标记
	 * 
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param text
	 * @return
	 */
	public static String getTextEmotion(String text) {
		String emotion = "";
		String[] result_text = sentiStrength.computeSentimentScores(text)
				.split(" ");
		// 解析返回的字符串得到感情
		int score = Integer.parseInt(result_text[2]);
		if (score > 0) {
			emotion = "positive";
		} else if (score < 0) {
			emotion = "negative";
		} else {
			emotion = "neutral";
		}
		return emotion;
	}
}
