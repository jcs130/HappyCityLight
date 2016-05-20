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
package com.zhongli.test;

import com.zhongli.nlptool.classifiter.SentenceFeatureClassifier_en;
import com.zhongli.nlptool.classifiter.WordVectorClassifier_en;

/**
 * @author Zhongli Li
 *
 */
public class TestMain {
	public static void main(String[] args) {
		TestMain tm = new TestMain();
		tm.testFunc();
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void testFunc() {
		String text = "Proud to be Candian~";
		try {
			WordVectorClassifier_en wCls1 = new WordVectorClassifier_en(
					"models/big_training_data_word_vector_before_remove_useless_part.model");
			SentenceFeatureClassifier_en sCls1 = new SentenceFeatureClassifier_en(
					"models/big_training_data_fratures_before_remove_useless_part.model");
			WordVectorClassifier_en wCls2 = new WordVectorClassifier_en(
					"models/small_training_data_word_vector_before_remove_useless_part.model");
			SentenceFeatureClassifier_en sCls2 = new SentenceFeatureClassifier_en(
					"models/small_training_data_fratures_before_remove_useless_part.model");
			System.out.println("1 <>" + wCls1.getSentiment(text));
			System.out.println("2 <>" + sCls1.getSentiment(text));
			System.out.println("3 <>" + wCls2.getSentiment(text));
			System.out.println("4 <>" + sCls2.getSentiment(text));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
