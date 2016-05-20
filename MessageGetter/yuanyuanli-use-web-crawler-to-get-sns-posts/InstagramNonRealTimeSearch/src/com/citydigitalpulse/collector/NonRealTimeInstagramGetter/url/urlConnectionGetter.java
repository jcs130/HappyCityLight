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
package com.citydigitalpulse.collector.NonRealTimeInstagramGetter.url;

import java.text.ParseException;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model.queuing;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.parser.JsonParser;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.tool.ListofToken;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.tool.Tools;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.tool.urlConnection;

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
	private int crawlStatus = 1;
	private queuing que;
	private JsonParser jsonParser;
	private ListofToken Tokenlist = new ListofToken();
	private urlConnection connection = new urlConnection();

	/**
	 * @param city
	 * @param minDate
	 * @param maxDate
	 * @param Lan
	 * @return
	 * @throws ParseException
	 */
	public int Getter(queuing queInfor) throws ParseException {
		this.que = queInfor;
		this.MinTimeStamp = Tools.getTimeStamp(que.get_start_date() + " 00:00:00");
		this.MaxTimeStamp = Tools.getTimeStamp(que.get_end_date() + " 24:00:00");
		this.latitude = que.get_boundingBoxCoordinatesLatitude();
		this.longitude = que.get_boundingBoxCoordinatesLongitude();
		this.radius = 5000;
		this.PostGet(MinTimeStamp, MaxTimeStamp);

		return crawlStatus;
	}

	/**
	 * Get tweets response after get Max and Min ID
	 * 
	 * @param queInfor
	 */

	public void PostGet(long MinTimeStamp, long MaxTimeStamp) {

		boolean flag = true;
		long maximTimeStamp = MaxTimeStamp;
		long minimTimeStamp = MaxTimeStamp-300;
		long stopLine = MinTimeStamp;

		while (flag == true) {
			try {

				String url = "https://api.instagram.com/v1/media/search?min_timestamp=" + minimTimeStamp
						+ "&max_timestamp=" + maximTimeStamp + "&distance=" + radius + "&lat=" + latitude + "&lng="
						+ longitude + "&access_token=" + Tokenlist.geTokens();
				System.out.println(url);
				String response = connection.getResponse(url);
				if (response == null) {
					crawlStatus = 0;
				} else {
					jsonParser = new JsonParser(response);
					System.out.print("Get Request sent succesfully \n");
				}

				// let the wait time to be random
				Thread.currentThread();
				Thread.sleep(300000);
				maximTimeStamp = maximTimeStamp - 300;
				minimTimeStamp = maximTimeStamp - 300;
				if (maximTimeStamp < stopLine) {
					flag = false;
					crawlStatus = 1;
				}
			}

			catch (Exception e) {
				// System.out.println("Couln'd find the page" + e);
				e.printStackTrace();
			}

		}
	}

}