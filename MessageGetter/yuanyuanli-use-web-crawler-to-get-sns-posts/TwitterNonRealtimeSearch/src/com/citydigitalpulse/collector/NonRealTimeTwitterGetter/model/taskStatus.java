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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model;

import java.util.Comparator;

/**
 * the class of task_id and status
 */
public class taskStatus implements Comparator<taskStatus> {
	private int task_id;
	private int status;

	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int compare(taskStatus arg0, taskStatus arg1) {
		int compare = arg0.getTask_id() - arg1.getTask_id();
		return compare;
	}

	@Override
	public int hashCode() {
		return this.task_id * 359 + this.status * 163;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!taskStatus.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final taskStatus other = (taskStatus) obj;
		if (this.task_id == other.task_id && this.status == other.status) {
			return true;
		}
		return false;
	}
}
