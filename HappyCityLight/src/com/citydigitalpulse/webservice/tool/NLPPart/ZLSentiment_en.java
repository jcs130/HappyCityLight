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

import com.citydigitalpulse.webservice.model.message.EmotionObj;
import com.zhongli.nlptool.classifiter.WordVectorClassifier_en;

/**
 * @author Zhongli Li
 *
 */
public class ZLSentiment_en implements SentimentClassifier {
	private WordVectorClassifier_en wCls;
	private String models_dir = "";
	private String word_vector_model;

	/**
	 * 
	 */
	public ZLSentiment_en(String models_dir, String word_vector_model) {
		this.models_dir = models_dir;
		this.word_vector_model = word_vector_model;
		this.init();
	}

	@Override
	public EmotionObj getTextSentiment(String text) {
		EmotionObj res = new EmotionObj();
		try {
			double score = wCls.getSentimentValue(text) * 5.0;

			res.setScale(5);
			res.setValue(score);
			if (score > 0) {
				res.setEmotion("positive");
			} else if (score < 0) {
				res.setEmotion("negative");
			} else {
				res.setEmotion("neutral");
			}
		} catch (Exception e) {
			System.out.println(text);
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public void init() {
		try {
			wCls = new WordVectorClassifier_en(models_dir + word_vector_model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
