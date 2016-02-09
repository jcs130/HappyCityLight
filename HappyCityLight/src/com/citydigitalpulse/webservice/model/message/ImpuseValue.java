package com.citydigitalpulse.webservice.model.message;

import com.citydigitalpulse.webservice.model.collector.LocArea;

/**
 * 直接返回的城市脉搏值
 * 
 * @author zhonglili
 *
 */
public class ImpuseValue {
	private long timestamp;
	private int positive_num;
	private int negative_num;
	private int neutral_num;
	private int sun_num;
	private double impuse_value;
	private LocArea area;
	private double center_location_lat;
	private double center_location_lan;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getPositive_num() {
		return positive_num;
	}

	public void setPositive_num(int positive_num) {
		this.positive_num = positive_num;
	}

	public int getNegative_num() {
		return negative_num;
	}

	public void setNegative_num(int negative_num) {
		this.negative_num = negative_num;
	}

	public int getNeutral_num() {
		return neutral_num;
	}

	public void setNeutral_num(int neutral_num) {
		this.neutral_num = neutral_num;
	}

	public int getSun_num() {
		return sun_num;
	}

	public void setSun_num(int sun_num) {
		this.sun_num = sun_num;
	}

	public double getImpuse_value() {
		return impuse_value;
	}

	public void setImpuse_value(double impuse_value) {
		this.impuse_value = impuse_value;
	}

	public LocArea getArea() {
		return area;
	}

	public void setArea(LocArea area) {
		this.area = area;
	}

	public double getCenter_location_lat() {
		return center_location_lat;
	}

	public void setCenter_location_lat(double center_location_lat) {
		this.center_location_lat = center_location_lat;
	}

	public double getCenter_location_lan() {
		return center_location_lan;
	}

	public void setCenter_location_lan(double center_location_lan) {
		this.center_location_lan = center_location_lan;
	}

}
