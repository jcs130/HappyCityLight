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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.parser;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml.MessageInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.MessageModel;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.queuing;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool.Tools;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool.urlConnection.urlConnection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Extract relevant tweets content from the html file
 *
 */

public class HtmlParser {
	private int CACHE_NUM = 1000;
	private String MinimumID = "";
	private String MaximumID = "";
	private String htmlString = "";
	private boolean has_more_items = true;
	private Document doc;
	private Elements content;
	private queuing que;
	private urlConnection getResponse;
	private MessageInterface_MySQL sFullMsg;
	// private AppendToFile writer = new AppendToFile();

	public HtmlParser(queuing que, String htmlFile) {
		this.que = que;
		this.htmlString = htmlFile;
		this.doc = Jsoup.parse(htmlString, "utf-8");// File to be processed
		this.content = doc.select("li.js-stream-item");
		System.out.println("content.size() " + content.size());
		// writer.appendMethodB(content.text());
		if (content.size() == 0) {
			has_more_items = false;
		}
	}

	/**
	 * Extract tweet content and save to the MessageModel object. Save to DB
	 * 
	 * @param que
	 */
	public void Parsing() {
		MaximumID = content.get(0).attr("data-item-id");
		MinimumID = content.get((content.size() - 1)).attr("data-item-id");
		/*
		 * System.out.println("MaximumID " + MaximumID); System.out.println(
		 * "MinimumID " + MinimumID);
		 */
		Elements profileContent = doc.select("div.js-profile-popup-actionable");
		// profile images
		Elements profileImage = doc.select("img.js-action-profile-avatar");
		// System.out.println("profileContent.text() " +
		// profileContent.text());
		Elements tweetTime = doc.select("span._timestamp");
		// System.out.println("tweetTime.text() " + tweetTime.text());
		Elements pText = doc.select("p.TweetTextSize");
		// System.out.println("pText.size() " + pText.size());
		// Elements tweet_Geo = doc.select("span.Tweet-geo");
		for (int i = 0; i < content.size(); i++) {
			MessageModel MsgMod = new MessageModel();
			// System.out.println("i is " + i);
			// The url is q=palce, so all the tweets get is from that
			// place. Using information for queuing table as the geometry
			// information of the tweets
			MsgMod.setMessage_from("twitter");
			MsgMod.setPlace_name(que.get_place_name());
			MsgMod.setPlace_fullname(que.get_place_fullName());
			MsgMod.setCountry(que.get_country());
			MsgMod.setQuery_location_latitude(que.get_boundingBoxCoordinatesLatitude());
			MsgMod.setQuery_location_langtitude(que.get_boundingBoxCoordinatesLongitude());
			MsgMod.setPlace_type(que.get_placeType());
			MsgMod.setisreal("false");
			MsgMod.setMessage_from("twitter");
			// Set city from the tweets
			// System.out.println(content.get(i).select("span.Tweet-geo").isEmpty());
			if (content.get(i).select("span.Tweet-geo").isEmpty()) {
				MsgMod.setCity(null);
				MsgMod.setProvince(null);
			} else {
				// the geometry information by Twitter is usually two part
				// looks like "city, province"
				ArrayList<String> geoArray = Tools
						.buildListFromString(content.get(i).select("span.Tweet-geo").attr("title"));
				MsgMod.setCity(geoArray.get(0));
				if (geoArray.size() == 1) {
					MsgMod.setProvince(null);
				} else
					MsgMod.setProvince(geoArray.get(1));
			}
			if (content.get(i).hasAttr("data-item-id")) {
				// tweet id
				MsgMod.setRaw_id_str(String.valueOf(content.get(i).attr("data-item-id")));

			}
			// System.out.println(content.get(i).select("div.AdaptiveMedia-photoContainer").isEmpty());
			if (!content.get(i).select("div.AdaptiveMedia-photoContainer").isEmpty()) {
				String imgUrl = "";
				for (int q = 0; q < content.get(i).select("div.AdaptiveMedia-photoContainer").size(); q++) {
					imgUrl = imgUrl
							+ content.get(i).select("div.AdaptiveMedia-photoContainer").get(q).attr("data-image-url");

					// System.out.println(imgUrl);
				}
				MsgMod.setMedia_types(Tools.buildListFromString("photo"));
				MsgMod.setMedia_urls(Tools.buildListFromString(imgUrl));
			}

			if (!content.get(i).select("div.AdaptiveMedia-videoContainer").isEmpty()) {
				MsgMod.setMedia_types(Tools.buildListFromString("video"));
				String url = "https://twitter.com/i/videos/tweet/" + String.valueOf(content.get(i).attr("data-item-id"))
						+ "?embed_source=clientlib&player_id=0&rpc_init=1"; //
				System.out.println("videoGetterUrl " + url);
				getResponse = new urlConnection();
				String htmlResponse = getResponse.getResponse(url);
				Document urlDoc = Jsoup.parse(htmlResponse, "utf-8");
				Elements urlContents = urlDoc.select("div.player-container");

				urlDoc = Jsoup.parse(htmlResponse, "UTF-8");
				urlContents = urlDoc.select("div.player-container");
				String temp = urlContents.attr("data-config");
				ObjectMapper mapper = new ObjectMapper();
				String videoURL = "";
				try {
					JsonNode root = mapper.readTree(temp);
					if (root != null) {
						if (root.size() != 0) {
							if (root.get("video_url") != null) {
								if (root.get("video_url").asText() != null) {
									videoURL = root.get("video_url").asText();
									MsgMod.setMedia_urls(Tools.buildListFromString(videoURL));
								}
							}
						}
					} else {

						MsgMod.setMedia_urls_local(Tools.buildListFromString(""));
					}

				} catch (IOException e)

				{
					e.printStackTrace();
				}
			}

			// All the hashtags, urls, languages and media urls are got form
			// the text to avoid malposition. Not all tweets have all the
			// information so a judge statement is essential
			// System.out.println(pText.contains(pText.get(i)));
			if (pText.contains(pText.get(i))) {
				MsgMod.setText(pText.get(i).text());
				// System.out.println("pText.text() " +
				// pText.get(i).text());
				if (pText.get(i).hasAttr("lang")) {
					MsgMod.setLang(pText.get(i).attr("lang"));
					// System.out.println("pText.get(i).hasAttrlang" +
					// pText.get(i).attr("lang"));
				}
				if (!pText.get(i).select("s").isEmpty()) {
					// MsgMod.setHashtags(Tools.buildArrayListFromString("null"));
					Elements linksTag = pText.get(i).select("s");
					Elements linksContent = pText.get(i).select("b");
					String hashTag = "";
					for (int j = 0; j < linksTag.size(); j++) {
						// if it's hashtag #xxx or @ some user
						if (linksTag.get(j).text().equals("#")) {
							// hashTag = hashTag + linksTag.get(j).text() +
							// linksContent.get(j).text() + " ";
							// don't need the hashtag symbol
							hashTag = hashTag + linksContent.get(j).text() + " ";
						}
					}

					MsgMod.setHashtags(Tools.buildListFromString(hashTag));
				}

			}
			if (profileImage.contains(profileImage.get(i))) {
				MsgMod.setProfile_image(profileImage.get(i).attr("src"));
			}

			if (profileContent.contains(profileContent.get(i))) {
				// user name
				MsgMod.setUser_name(String.valueOf(profileContent.get(i).attr("data-name")));
			}
			if (tweetTime.contains(tweetTime.get(i))) {
				// time created timestamp use ms
				MsgMod.setCreat_at(Long.parseLong(tweetTime.get(i).attr("data-time-ms")));
			}
			if (!Tools.cacheUpdateMessages.containsKey(MsgMod.getRaw_id_str())) {
				Tools.cacheUpdateMessages.put(Long.parseLong(MsgMod.getRaw_id_str()), MsgMod);
				Tools.msgList.add(MsgMod);
				if (Tools.msgList.size() > 10) {
					sFullMsg = new MessageInterface_MySQL();
					System.out.println("Writing into Database");
					sFullMsg.insert(Tools.msgList);
					Tools.msgList.clear();
				}
				if (Tools.cacheUpdateMessages.size() > CACHE_NUM) {
					Tools.cacheUpdateMessages.clear();
				}
			}
		}
	}

	public boolean has_more_items() {
		return has_more_items;
	}

	public String ReturnMinId() {
		return MinimumID;
	}

	public String ReturnMaxID() {
		return MaximumID;
	}
}
