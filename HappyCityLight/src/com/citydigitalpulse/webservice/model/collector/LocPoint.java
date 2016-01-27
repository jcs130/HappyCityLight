package com.citydigitalpulse.webservice.model.collector;

/**
 * 地图上的点
 * 
 * @author John
 *
 */
public class LocPoint {
	private double lat;
	private double lan;

	public LocPoint(double lat, double lan) {
		this.lat = lat;
		this.lan = lan;
	}

	public double getLat() {
		return lat;
	}

	public double getLan() {
		return lan;
	}

}
