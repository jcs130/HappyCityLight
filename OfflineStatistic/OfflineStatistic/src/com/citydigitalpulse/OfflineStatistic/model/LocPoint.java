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

/**
 * 
 * @author Zhongli Li
 *
 */
public class LocPoint {
	private double lat;
	private double lng;

	/**
	 * 
	 */
	public LocPoint() {
		super();
		this.lat = 0;
		this.lng = 0;
	}

	public LocPoint(double lat, double lng) {
		this();
		this.setLat(lat);
		this.setLng(lng);
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

}
