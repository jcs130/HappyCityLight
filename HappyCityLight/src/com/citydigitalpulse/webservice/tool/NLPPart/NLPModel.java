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
package com.citydigitalpulse.webservice.tool.NLPPart;

import java.net.URISyntaxException;

import com.citydigitalpulse.webservice.model.message.EmotionObj;

//import uk.ac.wlv.sentistrength.SentiStrength;

/**
 * @author Zhongli Li
 *
 */
public class NLPModel {
	private SentimentClassifier ZLSentiment_en;

	// private static SentiStrength sentiStrength = new SentiStrength();

	// 这是静态初始化块
	 static {
	// String ssthInitialisation[] = { "sentidata",
	// "lib/SentStrength_Data/",
	// "scale" };
	// sentiStrength.initialise(ssthInitialisation); // Initialise
	 }
	/**
 * 
// */
	public NLPModel() {
		 try {
			 String path=this.getClass().getClassLoader().getResource("models/training_data_normal_before_remove_useless_part.model").getPath();
			System.out.println(path);
			 ZLSentiment_en = new ZLSentiment_en("",path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 使用SentiStrenge获得情感标记
	 * 
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param text
	 * @return
	 */
	// public static EmotionObj getTextEmotion(String text) {
	// EmotionObj res = new EmotionObj();
	// res.setScale(5);
	// String[] result_text = sentiStrength.computeSentimentScores(text)
	// .split(" ");
	// System.out.println("emotion result: pos:" +
	// result_text[0]+" neg: "+result_text[1]);
	// // 解析返回的字符串得到感情
	// int score = Integer.parseInt(result_text[2]);
	// res.setValue(score);
	// if (score > 0) {
	// res.setEmotion("positive");
	// } else if (score < 0) {
	// res.setEmotion("negative");
	// } else {
	// res.setEmotion("neutral");
	// }
	// return res;
	// }

	public EmotionObj getTextEmotion(String text) {
		return ZLSentiment_en.getTextSentiment(text);
	}

}
