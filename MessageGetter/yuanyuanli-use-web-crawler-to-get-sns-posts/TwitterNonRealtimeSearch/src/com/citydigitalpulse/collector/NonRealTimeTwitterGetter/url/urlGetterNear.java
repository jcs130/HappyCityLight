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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.url;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.queuing;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.parser.HtmlParser;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool.urlConnection.urlConnection;

/**
 * send GET request using urlConnection and place name
 * 
 * @author yuanyuan
 */
public class urlGetterNear {

	private long last_note_ts = 1;
	private String place_name = null;
	private String startDate = null;
	private String endDate = null;
	// private String language = "en";
	private int crawlStatus = 1;
	private queuing que;
	// private JsonParser jsonParser;
	private HtmlParser htmlParser;
	private urlConnection connection;

	/**
	 * @param queuing
	 * @return
	 */
	public void Getter(queuing queInfor) {
		this.que = queInfor;
		this.place_name = que.get_place_fullName();
		this.startDate = que.get_start_date();
		this.endDate = que.get_end_date();
		/*
		 * if (!que.get_in_what_lan().isEmpty()) { this.language =
		 * que.get_in_what_lan(); }
		 */
		place_name = place_name.replace(",", "%2c");
		place_name = place_name.replace(" ", "%20");
		String url = "https://twitter.com/search?f=tweets&vertical=default&q=near%3A%22" + place_name
				+ "%22%20within%3A15mi%20since%3A" + startDate + "%20until%3A" + endDate;
		/*
		 * String url =
		 * "https://twitter.com/search?f=tweets&vertical=default&q=lang%3A" +
		 * language + "%20place%3A" + place_id + "%20since%3A" + startDate +
		 * "%20until%3A" + endDate;
		 */

		this.FirstTimeGetter(url, que);
		// crawlStatus 0: no results 1: finished
	}

	// Language option disabled
	/**
	 * run for the 1st time to get the MaxID and MinID
	 * 
	 * @param que
	 */
	public void FirstTimeGetter(String urlID, queuing queInfor) {
		queuing que = queInfor;
		connection = new urlConnection();
		String MinTweetID = "null";
		String MaxTweetID = "null";
		String suffix = "&src=typd&lang=en";
		try {

			String url = urlID + suffix;
			System.out.println("url is " + url);
			String reponse = connection.getResponse(url);
			if (reponse.equals("null")) {
				crawlStatus = 0;
			} else {
				// Append(reponse);
				// System.out.println(result);
				htmlParser = new HtmlParser(que, reponse);
				boolean has_more_items = htmlParser.has_more_items();
				System.out.println("has_more_items " + has_more_items);
				if (has_more_items) {
					htmlParser.Parsing();
					MinTweetID = htmlParser.ReturnMinId();
					MaxTweetID = htmlParser.ReturnMaxID();
					System.out.print("Get Request sent succesfully " + last_note_ts + "\n");
					PostGet(MinTweetID, MaxTweetID, url, que);
				} // else if (has_more_items == false)

				if (has_more_items == false || MinTweetID.equals("null") || MaxTweetID.equals("null")) {
					// No result should change status to 4 in queuing and 3 in
					// asking table
					crawlStatus = 0;
					System.out.println("No Results");
				} // if it has no results, change the status of DB else continue
			}
		}

		catch (Exception e) {
		}

	}

	/**
	 * Get tweets response after get Max and Min ID
	 * 
	 * @param queInfor
	 */
	public void PostGet(String minID, String maxID, String urlid, queuing queInfor) {
		boolean flag = true;
		queuing que = queInfor;
		connection = new urlConnection();
		String MinTweetID = minID;
		String MaxTweetID = maxID;
		String urlFixPart = urlid;
		String suffix = "&src=typd&lang=en";
		String placeHolder = "-BD1UO2FFu9QAAAAAAAAETAAAAAcAAAASAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAA&reset_error_state=false";

		while (flag == true) {
			try {

				String url = urlFixPart + "&include_available_features=1&include_entities=1&last_note_ts="
						+ last_note_ts + "&max_position=TWEET-" + MinTweetID + "-" + MaxTweetID + placeHolder + suffix;
				System.out.println("url is second " + url);
				String response = connection.getResponse(url);
				boolean has_more_items = true;
				if (response.equals("null")) {
					flag = false;
					crawlStatus = 0;
				} else { /*
							 * jsonParser = new JsonParser(result.toString());
							 * String JsonItmes = jsonParser.ReturnItems();
							 * MinTweetID = jsonParser.ReturnMinID(); MaxTweetID
							 * = jsonParser.ReturnMaxID();
							 */
					// Append("reponse of http is " + response + "\n");
					htmlParser = new HtmlParser(que, response);
					has_more_items = htmlParser.has_more_items();
					System.out.println("has_more_items " + has_more_items);
					String lastMinTWeetID = MinTweetID;
					if (has_more_items) {
						htmlParser.Parsing();
						MinTweetID = htmlParser.ReturnMinId();
						System.out.println("MinTweetID " + MinTweetID);
						System.out.println("lastMinTWeetID true " + lastMinTWeetID);
					}

					// If lastMinTweetID is same as MinTweetID or MinTweetID is
					// null then stop
					if (lastMinTWeetID.equals(MinTweetID) || has_more_items == false || MinTweetID.equals("null")) {
						flag = false;
						crawlStatus = 1;
						System.out.println("Crawl Finished");
						break;
					}
					lastMinTWeetID = MinTweetID;
				}

				System.out.print("Get Request sent succesfully " + last_note_ts + "\n");
				// let the wait time to be random
				final double doubleRandom = Math.random();
				final int sleepTime = (int) (doubleRandom * 50);
				Thread.currentThread();
				Thread.sleep(sleepTime * 1000);
				last_note_ts++;
			} catch (Exception e) {
				// System.out.println("Couln'd find the page" + e);
				e.printStackTrace();
			}
		}
	}

	public int get_CrawlStatus() {
		// TODO Auto-generated method stub
		return crawlStatus;
	}

}
