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
import com.zhongli.nlptool.classifiter.SentenceFeatureClassifier_en;
import com.zhongli.nlptool.classifiter.WordVectorClassifier_en;

/**
 * @author Zhongli Li
 *
 */
public class ZLSentiment_en implements SentimentClassifier {
	private WordVectorClassifier_en wCls1;
	private SentenceFeatureClassifier_en sCls1;
	private WordVectorClassifier_en wCls2;
	private SentenceFeatureClassifier_en sCls2;
	private String models_dir = "";
	private String word_vector_model_1, word_vector_model_2,
			word_feature_model_1, word_feature_model_2;

	/**
	 * 
	 */
	public ZLSentiment_en(String models_dir, String word_vector_model_1,
			String word_vector_model_2, String word_feature_model_1,
			String word_feature_model_2) {
		this.models_dir = models_dir;
		this.word_vector_model_1 = word_vector_model_1;
		this.word_vector_model_2 = word_vector_model_2;
		this.word_feature_model_1 = word_feature_model_1;
		this.word_feature_model_2 = word_feature_model_2;
		this.init();
	}

	@Override
	public EmotionObj getTextSentiment(String text) {
		double score = 0;
		EmotionObj res = new EmotionObj();
		double class1 = getScoreFromSentimentString(wCls1.getSentiment(text));
		double class2 = getScoreFromSentimentString(wCls2.getSentiment(text));
		double class3 = getScoreFromSentimentString(sCls1.getSentiment(text));
		double class4 = getScoreFromSentimentString(sCls2.getSentiment(text));
		// 综合四个结果然后求平均数与方差
		score = (class2 + class3 + class4) / 3.0;
		res.setScale(5);
		res.setValue(score);
		if (score > 3) {
			res.setEmotion("positive");
		} else if (score < 1) {
			res.setEmotion("negative");
		} else {
			res.setEmotion("neutral");
		}
		return res;
	}

	private double getScoreFromSentimentString(String sentiment) {
		if (sentiment.equals("positive")) {
			return 5.0;
		} else if (sentiment.equals("negative")) {
			return -5.0;
		} else {
			return 0.0;
		}
	}

	@Override
	public void init() {
		try {
			wCls1 = new WordVectorClassifier_en(models_dir
					+ word_vector_model_1);
			sCls1 = new SentenceFeatureClassifier_en(models_dir
					+ word_feature_model_1);
			wCls2 = new WordVectorClassifier_en(models_dir
					+ word_vector_model_2);
			sCls2 = new SentenceFeatureClassifier_en(models_dir
					+ word_feature_model_2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
