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

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool.ListOfKeysAndTokens;

/**
 * Given a latitude and a longitude or IP address, searches for up to 20 places
 * that can be used as a place_id when updating a status.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class SearchPlaces {
	/**
	 * Usage: java twitter4j.examples.geo.SearchPlaces [ip address] or
	 * [latitude] [longitude] Return place_ids by lat and long
	 * 
	 * @param a
	 *            pair of latitude and longitude
	 */

	public ArrayList<String> getPlaceID(String[] latlong) {
		ListOfKeysAndTokens keysAndTokens = new ListOfKeysAndTokens();
		ConfigurationBuilder confiurationBuilder = keysAndTokens.getConfigurationBuilder();
		String[] latlongvalue = latlong;
		ArrayList<String> place_id = new ArrayList<String>();
		if (latlongvalue.length < 1) {
			return null;
		}
		try {
			// new twitter instance with oAuth properties
			Twitter twitter = new TwitterFactory(confiurationBuilder.build()).getInstance();
			GeoQuery query;
			if (latlongvalue.length == 2) {
				query = new GeoQuery(
						new GeoLocation(Double.parseDouble(latlongvalue[0]), Double.parseDouble(latlongvalue[1])));
			} else {
				query = new GeoQuery(latlongvalue[0]);
			}
			ResponseList<Place> places = twitter.searchPlaces(query);
			if (places.size() == 0) {
				return null;
			} else {
				for (Place place : places) {
					place_id.add(place.getId());
					Place[] containedWithinArray = place.getContainedWithIn();
					if (containedWithinArray != null && containedWithinArray.length != 0) {
						for (Place containedWithinPlace : containedWithinArray) {
							place_id.add(containedWithinPlace.getId());
						}
					}
				}
			}
		} catch (TwitterException te) {
			//te.printStackTrace();
			return null;
		}

		// remove duplicated elements
		HashSet<String> HshSet = new HashSet<String>();
		HshSet.addAll(place_id);
		place_id.clear();
		place_id.addAll(HshSet);
		return place_id;
	}
}
