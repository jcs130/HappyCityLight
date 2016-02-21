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
package com.citydigitalpulse.OfflineStatistic.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @author Zhongli Li
 *
 */
public class StatiisticsRecord implements Comparator<StatiisticsRecord> {
	// 标准Unix timestamp
	private long date_timestamp_ms;
	// 加上时区之后的日期，方便进行更合理的多城市比较
	private String local_date;
	private RegInfo regInfo;
	private double impuse_value;
	private ImpuseValue impuse_obj;
	private int rank;
	private List<HotTopic> hot_topics;
	private String message_from;
	private String language;
	private HashMap<String, HotTopic> temp_topics;
	/**
	 * 
	 */
	public StatiisticsRecord() {
		super();
		this.date_timestamp_ms = 0;
		this.local_date = "";
		this.regInfo = new RegInfo();
		this.impuse_value = 0;
		this.impuse_obj = new ImpuseValue();
		this.rank = 0;
		this.hot_topics = new ArrayList<HotTopic>();
		this.temp_topics=new HashMap<String, HotTopic>();
		this.message_from = "all";
		this.language = "all";
	}

	/**
	 * @param date_string
	 */
	public StatiisticsRecord(String date_string) {
		this();
		this.local_date = date_string;
	}

	public long getDate_timestamp_ms() {
		return date_timestamp_ms;
	}

	public void setDate_timestamp_ms(long date_timestamp_ms) {
		this.date_timestamp_ms = date_timestamp_ms;
	}

	public String getLocal_date() {
		return local_date;
	}

	public void setLocal_date(String local_date) {
		this.local_date = local_date;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getMessage_from() {
		return message_from;
	}

	public void setMessage_from(String message_from) {
		this.message_from = message_from;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<HotTopic> getHot_topics() {
		return hot_topics;
	}

	public void setHot_topics(List<HotTopic> hot_topics) {
		this.hot_topics = hot_topics;
	}

	public double getImpuse_value() {
		return impuse_value;
	}

	public void setImpuse_value(double impuse_value) {
		this.impuse_value = impuse_value;
	}

	public ImpuseValue getImpuse_obj() {
		return impuse_obj;
	}

	public void setImpuse_obj(ImpuseValue impuse_obj) {
		this.impuse_obj = impuse_obj;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param tempReg
	 * @param temp
	 */
	public void addNewRecord(RegInfo tempReg, StructuredFullMessage temp) {
		if (this.regInfo.getRegName().equals("")) {
			this.regInfo.setRegID(tempReg.getRegID());
			this.regInfo.setBox_points(tempReg.getBox_points());
			this.regInfo.setCenter_lat(tempReg.getCenter_lat());
			this.regInfo.setCenter_lan(tempReg.getCenter_lan());
			this.regInfo.setCountry(tempReg.getCountry());
			this.regInfo.setRegName(tempReg.getRegName());
			this.regInfo.setTime_zone(tempReg.getTime_zone());
		}
		if (this.date_timestamp_ms == 0) {
			this.date_timestamp_ms = temp.getCreat_at();
		}
		this.impuse_obj.addNewValue(temp.getEmotion_text());
		this.impuse_value = impuse_obj.getImpuse_value();
		//如果
	}

	/*
	 * 实现大小比较
	 */
	@Override
	public int compare(StatiisticsRecord o1, StatiisticsRecord o2) {
		double val1 = o1.getImpuse_value();
		double val2 = o2.getImpuse_value();
		return val1 < val2 ? 0 : 1;
	}

}
