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

import com.citydigitalpulse.OfflineStatistic.model.EmotionObj;

import uk.ac.wlv.sentistrength.SentiStrength;

/**
 * @author Zhongli Li
 *
 */
public class NLPModel {

	private static SentiStrength sentiStrength = new SentiStrength();

	// 这是静态初始化块
	static {
		String ssthInitialisation[] = { "sentidata", "SentStrength_Data/",
				"scale" };
		sentiStrength.initialise(ssthInitialisation); // Initialise
	}

	/**
	 * 使用SentiStrenge获得情感标记
	 * 
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param text
	 * @return
	 */
	public static EmotionObj getTextEmotion_en(String text) {
		EmotionObj res = new EmotionObj();
		res.setScale(5);
		String[] result_text = sentiStrength.computeSentimentScores(text)
				.split(" ");
		// 解析返回的字符串得到感情
		int score = Integer.parseInt(result_text[2]);
		res.setValue(score);
		if (score > 0) {
			res.setEmotion("positive");
		} else if (score < 0) {
			res.setEmotion("negative");
		} else {
			res.setEmotion("neutral");
		}
		return res;
	}

	// public static String getTextEmotion_fr(String text) {
	// // babababa
	// return "";
	// }
}
