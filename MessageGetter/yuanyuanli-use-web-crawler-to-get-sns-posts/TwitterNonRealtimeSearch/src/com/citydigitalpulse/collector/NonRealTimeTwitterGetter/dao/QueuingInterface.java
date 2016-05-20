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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.queuing;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.taskStatus;

/**
 * Interface of queuing table includs reset, change, get and insert
 *
 */
public interface QueuingInterface {

	/**
	 * Reset queuing table
	 */
	public void reSetStates();

	public ArrayList<taskStatus> GetQueuingTaskStatus(int taskid);

	/**
	 * Get the request with status type from queuing DB
	 * 
	 * @param type
	 *            0: waiting to be crawled 1: crawling 2: finished 3: status
	 *            changed in asking table 4: no results
	 * @return List<queuing>
	 */

	public List<queuing> GetQueuingInfo(int type);

	/**
	 * Change one status in queing table using num_id
	 * 
	 * @param queList
	 * @param int
	 *            type
	 */
	public void ChangeQueuingStatus(ArrayList<queuing> queList, int type);

	/**
	 * Change mutiple status in queing table using num_id
	 * 
	 * @param queList
	 * @param int
	 *            type
	 */
	public void ChangeQueuingStatus(int num_id, int type);

	/**
	 * Insert one piece of data into the database
	 * 
	 * @param queuing
	 */
	public void insert(queuing que);

	/**
	 * Insert multiple pieces of data into databases
	 * 
	 * @param msgs
	 */
	public void insert(List<queuing> ques);

	public ArrayList<Integer> GetQueuingTaskIDs();
}
