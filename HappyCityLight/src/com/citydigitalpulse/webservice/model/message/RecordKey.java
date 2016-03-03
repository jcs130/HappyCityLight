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
package com.citydigitalpulse.webservice.model.message;

/**
 * @author Zhongli Li
 *
 */
public class RecordKey {
	private String record_key;
	private long record_id;

	/**
	 * 
	 */
	public RecordKey(String record_key) {
		this.record_key = record_key;
	}

	public RecordKey(long record_id) {
		this.record_id = record_id;
	}

	public String getRecord_key() {
		return record_key;
	}

	public void setRecord_key(String record_key) {
		this.record_key = record_key;
	}

	public long getRecord_id() {
		return record_id;
	}

	public void setRecord_id(long record_id) {
		this.record_id = record_id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (record_id ^ (record_id >>> 32));
		result = prime * result
				+ ((record_key == null) ? 0 : record_key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordKey other = (RecordKey) obj;
		if (this.record_id == other.record_id
				|| this.record_key.equals(other.record_key)) {
			return true;
		} else {
			return false;
		}
	}

}
