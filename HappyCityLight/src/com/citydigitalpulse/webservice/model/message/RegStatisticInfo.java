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

import com.citydigitalpulse.webservice.model.collector.RegInfo;

/**
 * @author Zhongli Li
 *
 */
public class RegStatisticInfo implements Comparable<RegStatisticInfo> {
	private int regID;
	private String date;
	private String regName;
	private int rank;
	private String record_key;
	private long record_id;
	private PulseValue pulse;

	/**
	 * 
	 */
	public RegStatisticInfo() {
		super();
	}

	public RegStatisticInfo(StatiisticsRecord record) {
		this();
		this.regID = record.getRegInfo().getRegID();
		this.regName = record.getRegInfo().getRegName();
		this.rank = record.getRank();
		this.record_id = record.getRecord_id();
		this.record_key = record.getRegInfo().getCountry().toLowerCase() + ","
				+ record.getRegInfo().getRegName().toLowerCase() + ","
				+ record.getLocal_date() + "," + record.getLanguage() + ","
				+ record.getMessage_from();
		this.pulse = record.getPulse();
		this.date = record.getLocal_date();
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getRecord_key() {
		return record_key;
	}

	public void setRecord_key(String record_key) {
		this.record_key = record_key;
	}

	public long getRecord_id() {
		return record_id;
	}

	public void setRecord_id(long record_id) {
		this.record_id = record_id;
	}

	@Override
	public int compareTo(RegStatisticInfo o) {
		return (this.getRank() - o.getRank());
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public PulseValue getPulse() {
		return pulse;
	}

	public void setPulse(PulseValue pulse) {
		this.pulse = pulse;
	}

	public int getRegID() {
		return regID;
	}

	public void setRegID(int regID) {
		this.regID = regID;
	}

	public String getRegName() {
		return regName;
	}

	public void setRegName(String regName) {
		this.regName = regName;
	}

}
