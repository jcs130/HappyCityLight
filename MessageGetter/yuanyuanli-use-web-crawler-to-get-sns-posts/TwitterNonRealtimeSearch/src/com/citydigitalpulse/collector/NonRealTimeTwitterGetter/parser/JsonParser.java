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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 *To parser the received json file into String
 *To get the itmes_html attribute from the JSON response
 *@yuanyuan 
 * */

/**
 * Return the items_html attribute from responses
 */
public class JsonParser {

	private String Items = null;
	private String ID = null;

	/**
	 * Constructor Set status to finished if ResponseValue is null
	 */
	public JsonParser(String ResponseValue) {

		this.gParserHtml(ResponseValue);

	}

	/**
	 * get the itmes_html attribute as a string
	 */
	private void gParserHtml(String responseValue) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(responseValue);
			Items = root.get("items_html").asText();
			ID = root.get("min_position").asText();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String ReturnItems() {
		return Items;
	}

	public String ReturnMinID() {
		String[] minid = ID.split("-");
		if (ID.length() == 7) {
			return null;
		} else
			return minid[1];
	}

	public String ReturnMaxID() {
		String[] maxid = ID.split("-");
		if (ID.length() == 7) {
			return null;
		} else
			return maxid[2];
	}

}
