package com.citydigitalpulse.webservice.model.collector;

import java.util.ArrayList;

/**
 * 监听区域的类
 * 
 * @author John
 *
 */
public class RegInfo {
	private int regID;
	private String regName;
	private String country;
	private int streamState;
	private ArrayList<LocArea> areas;
	private String box_points;

	public RegInfo(String regName) {
		this.regName = regName;
		areas = new ArrayList<LocArea>();
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

	@Override
	public String toString() {
		return "RegInfo [regID=" + regID + ", regName=" + regName
				+ ", country=" + country + ", streamState=" + streamState
				+ ", areas=" + areas + ", box_points=" + box_points + "]";
	}

}
