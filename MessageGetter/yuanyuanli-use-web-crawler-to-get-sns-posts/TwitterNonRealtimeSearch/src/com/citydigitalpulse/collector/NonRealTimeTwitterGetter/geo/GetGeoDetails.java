/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.geo;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool.ListOfKeysAndTokens;

import twitter4j.Place;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.Authorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Shows specified place's detailed information
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class GetGeoDetails {
	Configuration conf;
	Authorization auth;

	/**
	 * Usage: java twitter4j.examples.geo.GetGeoDetails [place id]
	 *
	 * @param place_id
	 */
	public Place getLatitudeLongitude(String args) {
		ListOfKeysAndTokens keysAndTokens = new ListOfKeysAndTokens();
		ConfigurationBuilder confiurationBuilder = keysAndTokens.getConfigurationBuilder();
		try {
			Twitter twitter = new TwitterFactory(confiurationBuilder.build()).getInstance();
			Place place = twitter.getGeoDetails(args);
			return place;

		} catch (TwitterException te) {
			Place place = null;
			//te.printStackTrace();
			return place;
		}
	}
}
