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

import java.util.HashSet;
import java.util.List;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.asking;

/**
 * Interface of asking table. Has three operations: reset, get and change
 * 
 * @author yuanyuan
 *
 */
public interface AskingInterface {

	/**
	 * Reset DB
	 */
	public void reSetStates();

	/**
	 * Get the request with status type from asking table
	 * 
	 * @param type
	 *            0,User asked but not queued in queuing table or started; 1,
	 *            user requests accepted and queuing; 2, working on this
	 *            request; 3, no results found for this request; 4, finished; 5,
	 *            failed to get geometry information, wrong latitude and
	 *            longitude pair.
	 * @return the first row that satisfy the selection sql
	 */

	public List<asking> GetAskingInfo(int type);

	/**
	 * Change the status of one row in asking table
	 * 
	 * @param num_id
	 * @param type
	 */
	public void ChangeAskingStatus(int num_id, int type);

	/**
	 * Change the status of multiple rows in asking table
	 * 
	 * @param num_id
	 * @param type
	 */
	public void ChangeAskingStatus(HashSet<Integer> numIds, int type);

}
