/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Yuanyuan Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool;

import java.util.ArrayList;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.TwitteroAuthModel;

import twitter4j.conf.ConfigurationBuilder;

/**
 * List of Twitter tokens and keys
 */
public class ListOfKeysAndTokens {
	private int times = 0;
	private ArrayList<TwitteroAuthModel> twitterKeyTokenList;

	public ListOfKeysAndTokens() {
		twitterKeyTokenList = new ArrayList<TwitteroAuthModel>();
		init();
	}

	private void init() {
		// first
		String accessToken = "1658418043-Amp1uTD7PjwOmYODxdfydk5pB3xZpeD1PLSecUF";
		String accessTokenSecret = "geMSBOCwW31yPdF8JJOmF1l7U2CR5Ee1tAsISC6s7Ytyq";
		String consumerKey = "ZhSfNIcMv8nPn1PHc5a62nlNh";
		String consumerSecret = "VkmXXn1tYDMkofTlm1OdGrARr9lNGvgTZLu931VW3xRaEcNqIt";
		TwitteroAuthModel twitterKeyToken = new TwitteroAuthModel();
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
		// second
		accessToken = "1658418043-MmlLPoL0ijkJ8K9PPvc4AVpnGCF9KFzBXv47wD5";
		accessTokenSecret = "aZ7Ui4cUOhJyd7PAqmxSLbVtswyjL7tnJvUqQg40WC5xs";
		consumerKey = "xoRDJ5oHzZ2rLSceUQIegNhW7";
		consumerSecret = "ZKznzjYBrYu90Isdzk7O5ibLJgQDWc3KcXXsoEck4UkuWQuH6O";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
		// third
		accessToken = "1658418043-aBA8eeQvPiLxKiIOFXdNeZvqoQEpBfrrwKUlU1r";
		accessTokenSecret = "Kqm6LRC73SAoSg73JqHTVMGaDDQdxdMssqVNf0VMKh1co";
		consumerKey = "GG2KG2eeRv24mjPwlpgdF8yTs";
		consumerSecret = "1u3ziLpb0FuupcVATrYKCid6Zdf36kiyA5aAVPZTINhl261H2V";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
		// fourth
		accessToken = "1658418043-L3rZZSVVcfPlmPv90gTqE5fjoItjp49V4HBahgG";
		accessTokenSecret = "2KPLBuyIcSVtTsQHG3Q5Bqwpok6Uzizj3NXySr2FuWcJJ";
		consumerKey = "RbWmC7o4ksr8nCJw2IbT8mTQ6";
		consumerSecret = "Apg6nYC5K7vtQTdpTG60teU4QjVAdeLMmJlIA4kDJee4Wgnoyw";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
		// fifth
		accessToken = "4795783994-1HhWvF8QPA3kJVzYcZm79Dbe8xQr4xyJoW23B4P";
		accessTokenSecret = "48EeFi3H7O7UrCSzV8mDnF9rwbvb1JVJCgBy7BIkCKLcl";
		consumerKey = "GVHxTRlcE9nPMUaXI0WWC4ok4";
		consumerSecret = "0mfMMA0qzG1TzfYNvb6QYkARdwFZOIscZeuarnaVVzbmAtorJh";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
		// six
		accessToken = "4795783994-4Fh9U2LWAcs4kCu5oVhZpoa0PYmj451GgoWknbR";
		accessTokenSecret = "sUlfQW3eCeYW9F1ADcHzXXQWBnFr3YjvTZZGaZZFWrgoy";
		consumerKey = "aGJaY402Vc8R0VwpYFRbvf7zG";
		consumerSecret = "zYn9lcgYgEPTUYpaMMwKfjDRWRqgED0gIhIckxWNXCaQM5Fikv";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
		// seven
		accessToken = "4795783994-N1fXgzEeXZHmxm2iUh7hWTO6Wf361seAw0adTPP";
		consumerKey = "i6alJ4WdxajMEMR9GjujXNO6T";
		consumerSecret = "Z7wuFWsAWugwsDhrCTb6tu5hytq25Rg6LBEIl9ati2Jib8Ndsl";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
		// eight
		accessToken = "4795783994-uWy2F2HCkndSfgvslk0yE30KeLWqKEQohTck3sW";
		accessTokenSecret = "I6ffpTsHFNGbSIexd0a7znOJTQtGfoPaqBe2KFi3W3whP";
		consumerKey = "ryJkQcmwdeRH9Tqsq63WPO0Yr";
		consumerSecret = "HNzj4lZsF7BWqx0WmTKVzShOBxKHPfiVXmor39ZgBQeLwSEmbn";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
		// nine
		accessToken = "712102481573052416-kTpAAPgiR5GYDqPzgbvA6ysLo7VpeFD";
		accessTokenSecret = "ptleTGekJDqb7aeNYvTefGKdkf0VWowRnzTptE7KSIVSn";
		consumerKey = "WrCPAKycjS2tqKcwytjIBpPjS";
		consumerSecret = "CuzTpq2wYQzRw51Myc98ZpyG1kpwYnu4C21XQWQOk2nwBJt05K";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);

		// ten
		accessToken = "712102481573052416-RUicfBty8u7vQK1fU8jPizYZJsthYCc";
		accessTokenSecret = "nWK89ijqex2KuXc4EaQoHTLGf4tEa0JPiOlT9XEbPWVQP";
		consumerKey = "Z3BhJr8xUJeHdGWl9YzLKg2c3";
		consumerSecret = "krYBqPaACXtxCoObMBUwTMeI7HXUX0FRIawEpXh8ZwEIg5Lqwd";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);

		// eleven
		accessToken = "712102481573052416-21DsI25mbpybsjaojraIJI0j4j3Zlhy";
		accessTokenSecret = "G0gBO8S3zEXQxRt0KGUU7HChIc5KkenfeYMoqyYzij3KX";
		consumerKey = "akPtNL9Dll5Jook3IKihFSbi9";
		consumerSecret = "av2VAF2Uz1eQtV8diDYAhHW8a9MFZYCMES1q2UTVeuKufTquN6";
		twitterKeyToken = new TwitteroAuthModel();
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);

		// twelve

		accessToken = "712105055336472576-CSvzM0N1Lvf4IM9GjtIntuppQ4fw6a1";
		accessTokenSecret = "5XK14etSye6z52aQWMvaAeeDEkxij8E3g7jtxtoon6scJ";
		consumerKey = "pdEDJvG6hQFRt8nh29sRG77FY";
		consumerSecret = "LYjeWOEqlLHzhuNCZuZ0N2xxEGVHQY9l11sDbAsHOFpS6O2XX7";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);

		// thirteen

		accessToken = "712105055336472576-RtaEUV7jQsy62ymZ2NPOfbk2kdZzBLO";
		accessTokenSecret = "yMlyEgbAkZbEOenNxJVaq9Ea9UwUSeB5YJ8JdJE2uN8ad";
		consumerKey = "asSIQxuKIZbtpGc5UpX1vDeLp";
		consumerSecret = "Y77MrjUsvH7WxXVyu48iI6M7SHqKm0LEEev6zvNyPjilrbz4n8";
		twitterKeyToken = new TwitteroAuthModel();
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);

		// fourteen

		accessToken = "712105055336472576-ATAS63YGw5qxeCoi3z9UmB8x7tTgbdr";
		accessTokenSecret = "3UoJdvmMhl0cBwD5p1XBj6nobeiUARrbHawCak6cvl8Bd";
		consumerKey = "m9FRbY5KJ5bacu3K69QDjDFqS";
		consumerSecret = "F717wa26ryZvCAlKEvRdUoB2YPJIhuplAU7MFGfxslfTaGkq7Q";
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
		twitterKeyToken = new TwitteroAuthModel();
		twitterKeyToken.setAccessToken(accessToken);
		twitterKeyToken.setAccessTokenSecret(accessTokenSecret);
		twitterKeyToken.setConsumerKey(consumerKey);
		twitterKeyToken.setConsumerSecret(consumerSecret);
		twitterKeyTokenList.add(twitterKeyToken);
	}

	private TwitteroAuthModel getTwitteroAuthModel() {
		int whichToken = times % twitterKeyTokenList.size();
		times++;
		if (times > 100000) {
			times = 0;
		}
		return twitterKeyTokenList.get(whichToken);
	}

	public ConfigurationBuilder getConfigurationBuilder() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		TwitteroAuthModel twitteroAuthModel = getTwitteroAuthModel();
		configurationBuilder.setDebugEnabled(true).setOAuthAccessToken(twitteroAuthModel.getAccessToken())
				.setOAuthAccessTokenSecret(twitteroAuthModel.getAccessTokenSecret())
				.setOAuthConsumerKey(twitteroAuthModel.getConsumerKey())
				.setOAuthConsumerSecret(twitteroAuthModel.getConsumerSecret());
		return configurationBuilder;
	}

}
