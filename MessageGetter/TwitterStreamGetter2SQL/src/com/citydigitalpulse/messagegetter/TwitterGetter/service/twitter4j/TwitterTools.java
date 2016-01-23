package com.citydigitalpulse.messagegetter.TwitterGetter.service.twitter4j;

import java.util.ArrayList;

import twitter4j.conf.ConfigurationBuilder;


public class TwitterTools {
	private ArrayList<TwitterAPIKey> twitterKeys;
	private int time = 0;

	public TwitterTools() {
		twitterKeys = new ArrayList<TwitterAPIKey>();
		init();
	}

	private void init() {
		// 1
		String sKey = "GAF7Toy0ZP64zK6I7NjWlNgNe";
		String sScret = "UU7dej1gEFNMLRMZDPp62W8qHvGTLigl7DMPcns3gp2q5lmfxq";
		String sToken = "2471483082-u6004tYHe80nqL7iWiocc1eKU7NnRuTCCHNPHaF";
		String sToSecret = "XGHFvP9IHikZ7zEmgGaWWPFesC5mNwKvkEb1MuYG2UhrU";
		twitterKeys.add(new TwitterAPIKey(sKey, sScret, sToken, sToSecret));
		// 2
		sKey = "8NvYH2r8t46lM6FCXOo4tFrDD";
		sScret = "9WLHOQygK8lmI4ZWzo3srNKlWwzryZBFgtZ2OGm6lkRqfNVj36";
		sToken = "2988801783-f8BVRqv2ODtKkyQPHeBxLhjrAOXcRsrUXyvZYaS";
		sToSecret = "5XGeW3IjXMimz6PlJKzO5VLN0CjZo3UmVHFyIXHgSAaQB";
		twitterKeys.add(new TwitterAPIKey(sKey, sScret, sToken, sToSecret));
		// 3
		sKey = "9MAxLmpUHei92zYrKhlMETmeU";
		sScret = "1Tg2NeFh1ZwzPWi4XZ9fY43rJgTdZfMaQHzcikWg9gjCIF2Dy9";
		sToken = "2988919121-4lqvSak1rL8Btgaq9Y3U8oziWwQO3slB7qHwtbB";
		sToSecret = "MgFTOG3HTdaD5dSaYdFfWsYFbqD19Qp9GQtXLfm1SzKOl";
		twitterKeys.add(new TwitterAPIKey(sKey, sScret, sToken, sToSecret));
		// 4
		sKey = "3ugoipdQkE9Fh9XwmA0WsuW8t";
		sScret = "CaAhBt46vseuJOL89M8YGpdYw5p5NAZHMdpUjikIAGkN9r6bIE";
		sToken = "2988919121-LXSsMnVWaIsJEw3huaBeyJBjp928zjtrcttfcNw";
		sToSecret = "8CImJpSlaJK8WwzMcwWa7lanuf5nFWsN9S3BM7tFvJAtj";
		twitterKeys.add(new TwitterAPIKey(sKey, sScret, sToken, sToSecret));
		// 5
		sKey = "TDojHbeQrnDSmfqrnhYOnzIR2";
		sScret = "OHHqVFYQuZPV9iLBe37zbjRhB08cMW0nVg5QV7YcSsl1UnOINL";
		sToken = "2988919121-WD5PlXGykxw10kC8HBBOG02pj6c5e8i6ruCgzJY";
		sToSecret = "vS9xc9kx41s1OgwlqSsY7KuYQVfE6gZvzVbrVkixkbavO";
		twitterKeys.add(new TwitterAPIKey(sKey, sScret, sToken, sToSecret));
		// 6
		sKey = "gGC9U5qobeYXdDGkslMIFg8fd";
		sScret = "fu7ODvdUYLLYE3EcsAw7NHZ5MbrxrwnE7moIbZ6d7TG2RDdms6";
		sToken = "2988801783-IFEDhWW2cmEa7wysRPAtV36WmKLdfLbAcbQdjGU";
		sToSecret = "wZ1nk3ZH2C1cDs42FmfKMtHJ0rsFcruIGbEbvVWKnVMGo";
		twitterKeys.add(new TwitterAPIKey(sKey, sScret, sToken, sToSecret));
		// 7
		sKey = "4moUpaUZE1wwmV1ASjm4DCo5s";
		sScret = "CZhI6yAUkCVpLq8zZ4tzV6tuSwG3c8BH1wfVIXJMHUynoQgbp7";
		sToken = "1663910887-tR8kQSAsXhCJVeQdAseENtntkDVKAbEO9ecX7ee";
		sToSecret = "6NaZeNI28L9kGYTXhbDGeXod26tnBcGA6jPRCAPvXABnf";
		twitterKeys.add(new TwitterAPIKey(sKey, sScret, sToken, sToSecret));
		// 8
		sKey = "gbhBdE5LRxn8MqtjFydk3FqK8";
		sScret = "EKVzdMnt8hBhcfSmVafKyTDJBm2TBiiTEBXhGqYeoXLhR2mgps";
		sToken = "1663910887-92fDMthObMqMTBxBgpmX9D7MyMihNneg4H5TVlL";
		sToSecret = "WHaroUMeTTG2IkOHMHTJ3oyHxZAxt6gREPmtfTk7eYuxR";
		twitterKeys.add(new TwitterAPIKey(sKey, sScret, sToken, sToSecret));
		// 9
		sKey = "5E6x6WA8ecsWoQvFMtB0E5gUF";
		sScret = "YjTBjpNwNoj0LyCkmgcgsnlJJSiHYRTUu9EYfxm2CNRI7iWV3N";
		sToken = "1663910887-vG9MoHyDgBrKiPR4eKCH15CEqFiQSPwVwYvamjQ";
		sToSecret = "0ITZbWDYvn7eDf834BXuo9A8rgCZUad0iCW2IjvRcU3bm";
		twitterKeys.add(new TwitterAPIKey(sKey, sScret, sToken, sToSecret));
	}

	/**
	 *  循环几个KEY
	 * 
	 * @return
	 */
	public ConfigurationBuilder getConfigurationBuilder() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		int num = time % twitterKeys.size();
		System.out.println("key:" + num);
		TwitterAPIKey tKey = twitterKeys.get(num);
		cb.setDebugEnabled(true).setOAuthConsumerKey(tKey.getsKey())
				.setOAuthConsumerSecret(tKey.getsScret())
				.setOAuthAccessToken(tKey.getsToken())
				.setOAuthAccessTokenSecret(tKey.getsToSecret());
		time++;
		return cb;
	}

	public void addKey(TwitterAPIKey key) {
		twitterKeys.add(key);
	}
}
