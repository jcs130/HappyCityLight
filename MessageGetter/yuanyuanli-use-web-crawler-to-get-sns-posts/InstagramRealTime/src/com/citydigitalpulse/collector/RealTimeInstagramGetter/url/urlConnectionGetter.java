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
package com.citydigitalpulse.collector.RealTimeInstagramGetter.url;

import java.text.ParseException;
import java.util.ArrayList;

import com.citydigitalpulse.collector.RealTimeInstagramGetter.model.asking;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.parser.JsonParser;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.tool.ListofToken;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.tool.urlConnection;

/**
 * send GET request using urlConnection Currently the language option is
 * disabled
 * 
 * @author yuanyuan
 */
public class urlConnectionGetter {

	private long MinTimeStamp = 0;
	private long MaxTimeStamp = 0;
	private double latitude = 0;
	private double longitude = 0;
	private int radius = 0;
	private ArrayList<asking> askLists;
	private JsonParser jsonParser;
	private urlConnection connection;
	public long numberIncrease = 0;

	/**
	 * @param city
	 * @param minDate
	 * @param maxDate
	 * @param Lan
	 * @return
	 * @throws ParseException
	 */
	public void Getter(ArrayList<asking> ask, long currentTimeStamp) throws ParseException {
		this.askLists = ask;
		this.MaxTimeStamp = currentTimeStamp;
		this.MinTimeStamp = currentTimeStamp - 300;
		this.numberIncrease = currentTimeStamp * 1000;
		this.radius = 5000;

		for (int i = 0; i < askLists.size(); i++) {
			this.latitude = askLists.get(i).get_latitude();
			this.longitude = askLists.get(i).get_longitude();
			this.PostGet();
		}

	}

	public void PostGet() {

		try {
			String url = "https://api.instagram.com/v1/media/search?min_timestamp=" + MinTimeStamp + "&max_timestamp="
					+ MaxTimeStamp + "&distance=" + radius + "&lat=" + latitude + "&lng=" + longitude + "&access_token="
					+ ListofToken.geTokens();
			System.out.println(url);
			connection = new urlConnection();
			String response = connection.getResponse(url);
			if (response != null) {
				jsonParser = new JsonParser(response, numberIncrease);
				numberIncrease = numberIncrease + jsonParser.GetincreasedBy();
				System.out.print("Get Request sent succesfully \n");
			}
		}

		catch (Exception e) {
			// System.out.println("Couln'd find the page" + e);
			e.printStackTrace();
		}

	}

}