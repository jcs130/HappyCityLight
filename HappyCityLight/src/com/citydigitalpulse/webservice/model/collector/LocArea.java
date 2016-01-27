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
	private String locName;
	private int regID;
	private int locID;
	private LocPoint center;
	private int range;

	public LocArea(double north, double west, double south, double east,
			String locName) {
		this.north = north;
		this.south = south;
		this.west = west;
		this.east = east;
		this.locName = locName;
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

	/**
	 * 设置区域的中心和半径
	 * 
	 * @param center
	 * @param range
	 */
	public void setCenterAndRange(LocPoint center, int range) {
		this.center = center;
		this.range = range;
	}

	public String getLocName() {
		return locName;
	}

	public void setLocName(String locName) {
		this.locName = locName;
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

	public LocPoint getCenter() {
		return center;
	}

	public int getRange() {
		return range;
	}

	public int getRegID() {
		return regID;
	}

	public void setRegID(int regID) {
		this.regID = regID;
	}

}
