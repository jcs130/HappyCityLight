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
package com.citydigitalpulse.webservice.model.collector;

/**
 * 区域对象
 * 
 * @author John
 *
 */
public class LocArea {
	private double north;
	private double south;
	private double west;
	private double east;
	private int locID;

	public LocArea() {
		super();
	}

	public LocArea(double north, double west, double south, double east) {
		this.north = north;
		this.south = south;
		this.west = west;
		this.east = east;
	}

	public LocArea(int locID, double north, double west, double south,
			double east) {
		this.locID = locID;
		this.north = north;
		this.south = south;
		this.west = west;
		this.east = east;
	}

	public double getNorth() {
		return north;
	}

	public double getSouth() {
		return south;
	}

	public double getWest() {
		return west;
	}

	public double getEast() {
		return east;
	}

	public int getLocID() {
		return locID;
	}

}
