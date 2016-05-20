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
package com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model;

/**
 * The class on waiting list 0: queuing 1:getting 2:finished 3: the result has
 * been put into the asking table
 * 
 * @author yuanyuan
 *
 */
public class queuing {
	private int num_id = 0;
	private int task_id = 0;
	private String name = "";
	private String streetAddress = "";
	private String country = "";
	private String placeType = "";
	private String fullName = "";
	private String boundingBoxType = "";
	private double boundingBoxCoordinatesLatitude = 0.0;
	private double boundingBoxCoordinatesLongitude = 0.0;
	private String place_id = "";
	private String start_date = "";
	private String end_date = "";
	private String in_what_lan = "";
	private String message_from = "";
	private int status = 0;

	public int get_num_id() {
		return num_id;
	}

	public void set_num_id(int num_id) {
		this.num_id = num_id;
	}

	public int get_task_id() {
		return task_id;
	}

	public void set_task_id(int task_id) {
		this.task_id = task_id;
	}

	public String get_place_name() {
		return name;
	}

	public void set_place_name(String name) {
		this.name = name;
	}

	public String get_streetAddress() {
		return streetAddress;
	}

	public void set_streetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String get_country() {
		return country;
	}

	public void set_country(String country) {
		this.country = country;
	}

	public String get_placeType() {
		return placeType;
	}

	public void set_placeType(String placeType) {
		this.placeType = placeType;
	}

	public String get_place_fullName() {
		return fullName;
	}

	public void set_place_fullName(String fullName) {
		this.fullName = fullName;
	}

	public String get_boundingBoxType() {
		return boundingBoxType;
	}

	public void set_boundingBoxType(String boundingBoxType) {
		this.boundingBoxType = boundingBoxType;
	}

	public double get_boundingBoxCoordinatesLatitude() {
		return boundingBoxCoordinatesLatitude;
	}

	public void set_boundingBoxCoordinatesLatitude(double latTempDouble) {
		this.boundingBoxCoordinatesLatitude = latTempDouble;
	}

	public double get_boundingBoxCoordinatesLongitude() {
		return boundingBoxCoordinatesLongitude;
	}

	public void set_boundingBoxCoordinatesLongitude(double boundingBoxCoordinatesLongitude) {
		this.boundingBoxCoordinatesLongitude = boundingBoxCoordinatesLongitude;
	}

	public String get_place_id() {
		return place_id;
	}

	public void set_place_id(String place_id) {
		this.place_id = place_id;
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

	public String get_message_from() {
		return message_from;
	}

	public void set_message_from(String message_from) {
		this.message_from = message_from;
	}

	public int get_status() {
		return status;
	}

	public void set_status(int getting_status) {
		this.status = getting_status;
	};

}