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


/**
 * @author Zhongli Li
 *
 */
public interface SentimentClassifier {

	/**
	 * 
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param text
	 * @return
	 */
	public EmotionObj getTextSentiment(String text);

	/**
	 * 
	 * @Author  Zhongli Li Email: lzl19920403@gmail.com
	 */
	public void init();

}
