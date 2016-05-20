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
package com.citydigitalpulse.collector.RealTimeInstagramGetter.parser;

import java.io.IOException;
import java.util.List;

import com.citydigitalpulse.collector.RealTimeInstagramGetter.app.Config;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.dao.ipml.MessageInterface_MySQL;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.model.ListofResponse;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.model.MessageModel;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.model.ResponseModel;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.tool.Tools;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * To parser the received json file into String To get the itmes_html attribute
 * from the JSON response
 */
public class JsonParser {

	private int CACHE_NUM = 1000;
	private List<ResponseModel> responseData;
	private MessageInterface_MySQL sFullMsg;
	private long LocalNumberIncrease = 0;
	private long increasedBy = 0;

	public JsonParser(String response, long numberIncrease) {
		if (response.length() != 0) {
			this.LocalNumberIncrease = numberIncrease;
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
				// responseData = mapper.readTree(dataString);
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
		if (responseData != null && !responseData.isEmpty()) {
			for (int i = 0; i < responseData.size(); i++) {
				MessageModel MsgMod = new MessageModel();
				MsgMod.setRaw_id_str(responseData.get(i).getId());
				MsgMod.setCreat_at(responseData.get(i).getCreated_time() * 1000);
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MsgMod.setText(responseData.get(i).getCaption().getText());
				}

				MsgMod.setMessage_from("instagram");
				MsgMod.setisreal("true");
				if (responseData.get(i).getLocation().getName().isEmpty()) {
					MsgMod.setPlace_fullname("");
				} else {
					MsgMod.setPlace_fullname(responseData.get(i).getLocation().getName());
				}
				MsgMod.setQuery_location_latitude(responseData.get(i).getLocation().getLatitude());
				MsgMod.setQuery_location_langtitude(responseData.get(i).getLocation().getLongitude());
				if (responseData.get(i).getType().equals("image")) {
					MsgMod.setMedia_types(Tools.buildArrayListFromString("photo"));
					MsgMod.setMedia_urls(Tools.buildArrayListFromString(
							responseData.get(i).getImages().getStandard_resolution().getUrl()));
				} else {
					MsgMod.setMedia_types(Tools.buildArrayListFromString("video"));
					MsgMod.setMedia_urls(Tools.buildArrayListFromString(
							responseData.get(i).getVideos().getStandard_resolution().getUrl()));
				}
				if (responseData.get(i).getTags().isEmpty()) {
					MsgMod.setHashtags(Tools.buildArrayListFromString(""));
				} else {
					MsgMod.setHashtags(Tools.buildArrayListFromString(responseData.get(i).getTags()));
				}

				if (!Tools.cacheUpdateMessages.containsKey(MsgMod.getRaw_id_str())) {
					Tools.msgList.add(MsgMod);
					Tools.cacheUpdateMessages.put(MsgMod.getRaw_id_str(), MsgMod);
					if (Tools.cacheUpdateMessages.size() > CACHE_NUM) {
						Tools.cacheUpdateMessages.clear();
					}
					// insert to DB and get the num_id
					long num_id = LocalNumberIncrease + increasedBy;
					increasedBy++;
					System.out.println("num_id" + num_id);
					if (num_id != 0) {
						MsgMod.setNum_id(num_id);
						if (Boolean.valueOf(MsgMod.getisreal())) {
							System.out.println(MsgMod.getText());
							// Send the posts to server
							Tools.sendNewMessage(Config.DCI_SERVER_URL + "message/uploadnewmessage",
									Config.UPLOAD_TOKEN, MsgMod);
						}
					} else {
						System.out.println("Error with the num_id:" + num_id);
					}
				}
			}
			if (Tools.msgList != null && !Tools.msgList.isEmpty()) {
				if (Tools.msgList.size() > 10) {
					sFullMsg = new MessageInterface_MySQL();
					sFullMsg.insert(Tools.msgList);
					Tools.msgList.clear();
				}
			}
		}
	}

	public long GetincreasedBy() {
		return increasedBy;
	}

}
