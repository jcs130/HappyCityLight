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
public class regions {
	private int num_id;
	private String city_name;
	private double latitude;
	private double longitude;
	private double radius;
	private String start_date;
	private String end_date;
	private String in_what_lan;
	private String required_by;
	private String required_at;
	private String message_from;
	private int getting_status;

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

	public String get_start_date() {
		return start_date;
	}

	public void set_start_date(String start_date) {
		this.start_date = start_date;
	}

	public String get_end_date() {
		return end_date;
	}

	public void set_end_date(String end_date) {
		this.end_date = end_date;
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

	@Override
	public String toString() {
		return "asking [num_id= " + num_id + " citiy_name= " + city_name + " latitude= " + latitude + " longitude= "
				+ longitude + " radius= " + radius + " start_date= " + start_date + " end_date= " + end_date
				+ " in_what_lan= " + in_what_lan + " required_by= " + required_by + " required_at= " + required_at
				+ " message_from= " + message_from + " getting_status= " + getting_status + "]";
	}

}