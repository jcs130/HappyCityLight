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
package com.zhongli.nlptool.test;

import com.zhongli.nlptool.classifiter.SentenceFeatureClassifier_en;
import com.zhongli.nlptool.classifiter.Translater;
import com.zhongli.nlptool.classifiter.WordVectorClassifier_en;

/**
 * @author Zhongli Li
 *
 */
public class TestClassifiter {

	public static void main(String[] args) {
		TestClassifiter tc = new TestClassifiter();
		 tc.testFunc();
//		tc.testTranslite();
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void testTranslite() {
		Translater t = new Translater();
		System.out.println(t.getEnglish("ar", "@wr_m5 انزين تراه الا بيسك ، بيور ماث عادي نستخدم"));
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void testFunc() {
		try {
			WordVectorClassifier_en wCls = new WordVectorClassifier_en(
					"models/libSVM_(Saima Aman Data Set).model");
			System.out
					.println("1 <>"
							+ wCls.getSentimentValue("#BellLetsTalk your not alone. Ever. No matter if your sad, depressed, scared, or happy. Your never alone. Spread the word ??"));
			SentenceFeatureClassifier_en sCls = new SentenceFeatureClassifier_en(
					"models/big_training_data_fratures_before_remove_useless_part.model");
			System.out
					.println("2 <>"
							+ sCls.getSentiment("Congratulations @LeoDiCaprio YOU DESERVE IT #Oscar"));
			System.out
					.println("3 <>"
							+ wCls.getSentiment("Congratulations @LeoDiCaprio YOU DESERVE IT #Oscar"));
			System.out
					.println("4 <>"
							+ sCls.getSentiment("Congratulations @LeoDiCaprio YOU DESERVE IT #Oscar"));
			System.out
					.println("5 <>"
							+ wCls.getSentiment("Congratulations @LeoDiCaprio YOU DESERVE IT #Oscar"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
