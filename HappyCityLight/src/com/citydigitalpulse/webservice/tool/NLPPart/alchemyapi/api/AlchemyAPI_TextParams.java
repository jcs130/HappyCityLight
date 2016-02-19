/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Zhongli Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.webservice.tool.NLPPart.alchemyapi.api;


public class AlchemyAPI_TextParams extends AlchemyAPI_Params{
	private Boolean useMetaData;
	private Boolean extractLinks;
	
	public boolean isUseMetaData() {
		return useMetaData;
	}
	
	public void setUseMetaData(boolean useMetaData) {
		this.useMetaData = useMetaData;
	}
	
	public boolean isExtractLinks() {
		return extractLinks;
	}
	
	public void setExtractLinks(boolean extractLinks) {
		this.extractLinks = extractLinks;
	}
	
	public String getParameterString(){
		String retString = super.getParameterString();
		
		if(useMetaData!=null) retString+="&useMetaData="+(useMetaData?"1":"0");
		if(extractLinks!=null) retString+="&extractLinks="+(extractLinks?"1":"0");
		
		return retString;
	}
}
