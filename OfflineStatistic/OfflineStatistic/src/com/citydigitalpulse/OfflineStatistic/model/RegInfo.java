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

/**
 * 
 * @author Zhongli Li
 *
 */
public class RegInfo {
	private int regID;
	private String regName;
	private String country;
	private int streamState;
	private double center_lat;
	private double center_lan;
	private ArrayList<LocArea> areas;
	private String box_points;
	private int time_zone;

	public RegInfo(String regName) {
		this();
		this.regName = regName;
		areas = new ArrayList<LocArea>();
	}

	/**
	 * 
	 */
	public RegInfo() {
		super();
		this.regID = 0;
		this.regName = "";
		this.country = "";
		this.streamState = 0;
		this.center_lat = 0;
		this.center_lan = 0;
		this.box_points = "";
		this.time_zone = 0;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getStreamState() {
		return streamState;
	}

	public void setStreamState(int streamState) {
		this.streamState = streamState;
	}

	public ArrayList<LocArea> getAreas() {
		return areas;
	}

	public String getBox_points() {
		return box_points;
	}

	public void setBox_points(String box_points) {
		this.box_points = box_points;
	}

	public int getTime_zone() {
		return time_zone;
	}

	public void setTime_zone(int time_zone) {
		this.time_zone = time_zone;
	}

	public double getCenter_lat() {
		return center_lat;
	}

	public void setCenter_lat(double center_lat) {
		this.center_lat = center_lat;
	}

	public double getCenter_lan() {
		return center_lan;
	}

	public void setCenter_lan(double center_lan) {
		this.center_lan = center_lan;
	}

	@Override
	public String toString() {
		return "RegInfo [regID=" + regID + ", regName=" + regName
				+ ", country=" + country + ", streamState=" + streamState
				+ ", center_lat=" + center_lat + ", center_lan=" + center_lan
				+ ", areas=" + areas + ", box_points=" + box_points
				+ ", time_zone=" + time_zone + "]";
	}

}
