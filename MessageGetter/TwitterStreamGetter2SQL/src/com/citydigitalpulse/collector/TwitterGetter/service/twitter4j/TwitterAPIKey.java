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
package com.citydigitalpulse.collector.TwitterGetter.service.twitter4j;

public class TwitterAPIKey {
	private String sKey;
	private String sScret;
	private String sToken;
	private String sToSecret;

	public TwitterAPIKey(String sKey, String sScret, String sToken,
			String sToSecret) {
		this.sKey = sKey;
		this.sScret = sScret;
		this.sToken = sToken;
		this.sToSecret = sToSecret;
	}

	public String getsKey() {
		return sKey;
	}

	public String getsScret() {
		return sScret;
	}

	public String getsToken() {
		return sToken;
	}

	public String getsToSecret() {
		return sToSecret;
	}

}
