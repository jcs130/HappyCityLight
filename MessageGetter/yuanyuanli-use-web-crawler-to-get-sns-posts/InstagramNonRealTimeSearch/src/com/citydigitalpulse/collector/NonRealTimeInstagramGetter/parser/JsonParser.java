
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
package com.citydigitalpulse.collector.NonRealTimeInstagramGetter.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.dao.ipml.MessageInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model.ListofResponse;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model.MessageModel;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model.ResponseModel;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.tool.Tools;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 *To parser the received json file into String
 *To get the itmes_html attribute from the JSON response
 *@yuanyuan 
 * */

/**
 * Return the items_html attribute from responses
 * 
 * @author yuanyuan
 */
public class JsonParser {

	private int CACHE_NUM = 1000;
	private List<ResponseModel> responseData;
	private MessageModel MsgMod = new MessageModel();
	private ArrayList<MessageModel> msgList = new ArrayList<MessageModel>();
	private MessageInterface_MySQL sFullMsg = new MessageInterface_MySQL();
	// private GetUTCTimeZone getUTCTimeZone;

	public JsonParser(String response) {
		if (response.length() != 0) {
			this.getResponseModelFromHtml(response);
			this.saveToDB();
		}
	}

	/**
	 * get the itmes_html attribute as a string
	 */
	public void getResponseModelFromHtml(String responseValue) {
		if (responseValue.length() != 0) {
			/*
			 * String temReplace = responseValue.substring(0, 21);
			 * System.out.println(temReplace);
			 */
			String dataString = responseValue.replace("\"meta\": {\"code\": 200}, ", "");
			ObjectMapper mapper = new ObjectMapper();
			try {
				responseData = mapper.readValue(dataString, ListofResponse.class).getListOfResponse();
				if (responseData == null || responseData.isEmpty()) {
					System.out.println("No Results");
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("No Results");
		}

	}

	public void saveToDB() {
		if (responseData != null) {

			for (int i = 0; i < responseData.size(); i++) {
				MsgMod.setRaw_id_str(responseData.get(i).getId());
				MsgMod.setCreat_at(responseData.get(i).getCreated_time());
				MsgMod.setProfile_img(responseData.get(i).getUser().getProfile_picture());
				if (responseData.get(i).getUser().getUsername().isEmpty()) {
					MsgMod.setUser_name("");
				} else {
					MsgMod.setUser_name(responseData.get(i).getUser().getUsername());
				}

				if (responseData.get(i).getCaption() == null) {
					MsgMod.setText("");
				} else {
					try {
						MsgMod.setLang(Tools.langugaeDetection(responseData.get(i).getCaption().getText()));
					} catch (IOException e) {
						e.printStackTrace();
					}
					MsgMod.setText(responseData.get(i).getCaption().getText());
				}

				MsgMod.setMessage_from("instagram");
				MsgMod.setisreal("true");
				if (responseData.get(i).getLocation().getName().isEmpty()) {
					MsgMod.setPlace_name("");
				} else {
					MsgMod.setPlace_name(responseData.get(i).getLocation().getName());
				}
				MsgMod.setQuery_location_latitude(responseData.get(i).getLocation().getLatitude());
				MsgMod.setQuery_location_langtitude(responseData.get(i).getLocation().getLongitude());
				if (responseData.get(i).getType().equals("image")) {
					MsgMod.setMedia_types(Tools.buildListFromString("photo"));
					MsgMod.setMedia_urls(Tools
							.buildListFromString(responseData.get(i).getImages().getStandard_resolution().getUrl()));
				} else {
					MsgMod.setMedia_types(Tools.buildListFromString("video"));
					MsgMod.setMedia_urls(Tools
							.buildListFromString(responseData.get(i).getVideos().getStandard_resolution().getUrl()));
				}
				if (responseData.get(i).getTags().isEmpty()) {
					MsgMod.setHashtags(Tools.buildListFromString(""));
				} else {
					MsgMod.setHashtags(Tools.buildListFromString(responseData.get(i).getTags()));
				}
				if (!Tools.cacheUpdateMessages.containsKey(MsgMod.getRaw_id_str())) {
					Tools.cacheUpdateMessages.put(MsgMod.getRaw_id_str(), MsgMod);
					if (Tools.cacheUpdateMessages.size() > CACHE_NUM) {
						Tools.cacheUpdateMessages.clear();
					}
					msgList.add(MsgMod);
				}
				System.out.println(MsgMod.toString());
				sFullMsg.insert(msgList);
			}
		}
	}

}
