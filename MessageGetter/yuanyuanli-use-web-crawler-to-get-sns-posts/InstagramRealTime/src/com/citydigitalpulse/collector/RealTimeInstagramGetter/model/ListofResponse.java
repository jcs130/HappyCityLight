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
package com.citydigitalpulse.collector.RealTimeInstagramGetter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListofResponse {
	   @JsonProperty("data")
	   private List<ResponseModel> listOfResponse;

	public List<ResponseModel> getListOfResponse() {
		return this.listOfResponse;
	}

	public void setListOfResponse(List<ResponseModel> listOfResponse) {
		this.listOfResponse = listOfResponse;
	} 
	   
}
