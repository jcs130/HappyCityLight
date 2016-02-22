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
package com.citydigitalpulse.webservice.model.message;

import java.util.Arrays;
import java.util.HashMap;

import com.citydigitalpulse.webservice.model.collector.RegInfo;

/**
 * @author Zhongli Li
 *
 */
public class StatiisticsRecord implements Comparable<StatiisticsRecord> {
	private long record_id;
	// 标准Unix timestamp
	private long date_timestamp_ms;
	// 加上时区之后的日期，方便进行更合理的多城市比较
	private String local_date;
	private RegInfo regInfo;
	private ImpuseValue impuse;
	private int rank;
	private HotTopic[] hot_topics;
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
		this.setImpuse(new ImpuseValue());
		this.rank = 0;
		this.temp_topics = new HashMap<String, HotTopic>();
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

	public RegInfo getRegInfo() {
		return regInfo;
	}

	public void setRegInfo(RegInfo regInfo) {
		this.regInfo = regInfo;
	}

	public HotTopic[] getHot_topics() {
		return hot_topics;
	}

	public void setHot_topics(HotTopic[] hot_topics) {
		this.hot_topics = hot_topics;
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

	public ImpuseValue getImpuse() {
		return impuse;
	}

	public void setImpuse(ImpuseValue impuse) {
		this.impuse = impuse;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void sortHotTopics() {
		hot_topics = temp_topics.values().toArray(new HotTopic[0]);
		Arrays.sort(hot_topics);
	}

	/*
	 * 实现大小比较
	 */
	@Override
	public int compareTo(StatiisticsRecord o) {
		return (int) (o.getImpuse().getImpuse_value() * 1000 - this.impuse
				.getImpuse_value() * 1000);
	}

	public long getRecord_id() {
		return record_id;
	}

	public void setRecord_id(long record_id) {
		this.record_id = record_id;
	}

}
