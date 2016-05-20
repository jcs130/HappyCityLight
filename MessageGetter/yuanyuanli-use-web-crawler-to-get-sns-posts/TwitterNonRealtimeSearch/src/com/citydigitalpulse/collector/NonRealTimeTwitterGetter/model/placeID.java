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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model;

/**
 * The class on asking table *
 */
public class placeID {
	private String placeid = "";
	private double latitude = 0.0;
	private double longitude = 0.0;

	public String get_Placeid() {
		return placeid;
	}

	public void set_Placeid(String placeid) {
		this.placeid = placeid;
	}

	public double get_Latitude() {
		return latitude;
	}

	public void set_Latitude(double latitude) {
		this.latitude = latitude;
	}

	public double get_Longitude() {
		return longitude;
	}

	public void set_Longitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "asking [latitude= " + latitude + " longitude= " + longitude + "]";
	}

}