package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool;

import java.util.Comparator;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.taskStatus;

/**
 * Comparator for ArrayList<queuing> by task_id in increasing order
 */
public class CustomComparator implements Comparator<taskStatus> {

	@Override
	public int compare(taskStatus o1, taskStatus o2) {
		return o1.getTask_id() - o2.getTask_id();
	}

}
