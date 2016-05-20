
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

/**
 * The class on city status Status 0: asking for data 1: the asking infor has
 * been put into the queuing table 2: start getting data 3: No result found 4:
 * crawl finished
 * 
 * @author yuanyuan
 *
 */
public class asking {
	private int num_id = 0;
	private String city_name = "";
	private double latitude = 0.0;
	private double longitude = 0.0;
	private double radius = 0.0;
	private String in_what_lan = "";
	private String required_by = "";
	private String required_at = "";
	private String message_from = "";
	private int getting_status = 0;

	public int get_num_id() {
		return num_id;
	}

	public void set_num_id(int num_id) {
		this.num_id = num_id;
	}

	public String get_city_name() {
		return city_name;
	}

	public void set_city_name(String city_name) {
		this.city_name = city_name;
	}

	public double get_latitude() {
		return latitude;
	}

	public void set_latitude(double latitude) {
		this.latitude = latitude;
	}

	public double get_longitude() {
		return longitude;
	}

	public void set_longitude(double longitude) {
		this.longitude = longitude;
	}

	public double get_radius() {
		return radius;
	}

	public void set_radius(double radius) {
		this.radius = radius;
	}

	public String get_in_what_lan() {
		return in_what_lan;
	}

	public void set_in_what_lan(String in_what_lan) {
		this.in_what_lan = in_what_lan;
	}

	public String get_required_by() {
		return required_by;
	}

	public void set_required_by(String required_by) {
		this.required_by = required_by;
	}

	public String get_required_at() {
		return required_at;
	}

	public void set_required_at(String required_at) {
		this.required_at = required_at;
	}

	public String get_message_from() {
		return message_from;
	}

	public void set_message_from(String message_from) {
		this.message_from = message_from;
	}

	public int get_getting_status() {
		return getting_status;
	}

	public void set_getting_status(int getting_status) {
		this.getting_status = getting_status;
	}


}