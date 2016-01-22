package com.zhongli.TwitterGetter.service.twitter4j;

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
