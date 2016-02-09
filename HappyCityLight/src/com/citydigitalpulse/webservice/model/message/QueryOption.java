package com.citydigitalpulse.webservice.model.message;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 用来记录查询条件的对象
 * 
 * @author zhonglili
 *
 */
public class QueryOption {
	private long time_start, time_end;
	private String place_name;
	private String location_area_json, langs, message_sources;
	private boolean is_true_location;

	public long getTime_start() {
		return time_start;
	}

	public void setTime_start(long time_start) {
		this.time_start = time_start;
	}

	public long getTime_end() {
		return time_end;
	}

	public void setTime_end(long time_end) {
		this.time_end = time_end;
	}

	public String getPlace_name() {
		return place_name;
	}

	public void setPlace_name(String place_name) {
		this.place_name = place_name;
	}

	public String getLocation_area_json() {
		return location_area_json;
	}

	public void setLocation_area_json(String location_area_json) {
		this.location_area_json = location_area_json;
	}

	public String getLangs() {
		return langs;
	}

	public void setLangs(String langs) {
		this.langs = langs;
	}

	public String getMessage_sources() {
		return message_sources;
	}

	public void setMessage_sources(String message_sources) {
		this.message_sources = message_sources;
	}

	public boolean isIs_true_location() {
		return is_true_location;
	}

	public void setIs_true_location(boolean is_true_location) {
		this.is_true_location = is_true_location;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof QueryOption))
			return false;
		if (obj == this)
			return true;

		QueryOption rhs = (QueryOption) obj;
		if (rhs.is_true_location == this.is_true_location
				&& rhs.getLangs().equals(this.getLangs())
				&& rhs.getLocation_area_json().equals(
						this.getLocation_area_json())
				&& rhs.getTime_start() == this.getTime_start()
				&& rhs.getTime_end() == this.getTime_end()
				&& rhs.getPlace_name().equals(this.getPlace_name())
				&& rhs.getMessage_sources().equals(this.getMessage_sources())
				&& rhs.is_true_location == this.is_true_location) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
				. // two randomly chosen prime numbers
					// if deriving: appendSuper(super.hashCode()).
				append(time_start).append(time_end).append(location_area_json)
				.append(message_sources).append(place_name).append(langs)
				.append(is_true_location).toHashCode();
	}

}
