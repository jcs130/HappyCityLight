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
package com.citydigitalpulse.OfflineStatistic.tool.senitool;

import com.citydigitalpulse.OfflineStatistic.model.EmotionObj;


/**
 * @author Zhongli Li
 *
 */
public class SentiStrengthNLP_en implements SentimentClassifier {

	/* (non-Javadoc)
	 * @see com.citydigitalpulse.OfflineStatistic.tool.senitool.SentimentClassifier#getTextSentiment(java.lang.String)
	 */
	@Override
	public EmotionObj getTextSentiment(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.citydigitalpulse.OfflineStatistic.tool.senitool.SentimentClassifier#init()
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

//
//	private SentiStrength sentiStrength;
//	private String word_list_forder_path = "SentStrength_Data/";
//
//	/**
//	 * 
//	 */
//	public SentiStrengthNLP_en(String word_list_forder_path) {
//		super();
//		this.word_list_forder_path = word_list_forder_path;
//		this.init();
//	}
//
//	/**
//	 * 使用SentiStrenge获得情感标记
//	 * 
//	 * @Author Zhongli Li Email: lzl19920403@gmail.com
//	 * @param text
//	 * @return
//	 */
//	@Override
//	public EmotionObj getTextSentiment(String text) {
//		if (sentiStrength == null) {
//			init();
//		}
//		EmotionObj res = new EmotionObj();
//		res.setScale(5);
//		String[] result_text = sentiStrength.computeSentimentScores(text)
//				.split(" ");
//		// 解析返回的字符串得到感情
//		int score = Integer.parseInt(result_text[2]);
//		res.setValue(score);
//		if (score > 0) {
//			res.setEmotion("positive");
//		} else if (score < 0) {
//			res.setEmotion("negative");
//		} else {
//			res.setEmotion("neutral");
//		}
//		return res;
//	}
//
//	@Override
//	public void init() {
//		sentiStrength = new SentiStrength();
//		String ssthInitialisation[] = { "sentidata", word_list_forder_path,
//				"scale" };
//		sentiStrength.initialise(ssthInitialisation); // Initialise
//	}
//
//	public void setWordListDir(String dir) {
//		this.word_list_forder_path = dir;
//	}


}
