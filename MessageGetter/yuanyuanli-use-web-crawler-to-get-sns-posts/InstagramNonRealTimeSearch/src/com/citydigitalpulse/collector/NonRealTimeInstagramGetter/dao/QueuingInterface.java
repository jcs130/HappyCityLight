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
package com.citydigitalpulse.collector.NonRealTimeInstagramGetter.dao;

import java.util.ArrayList;
import java.util.List;

import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model.queuing;

/**
 * Twitter message data access object
 * 
 * @author yuanyuan
 *
 */
public interface QueuingInterface {
	/**
	 * Reset DB
	 */
	public void reSetStates();

	/**
	 * Get the status from DB
	 * 
	 * @param type
	 *            0: queuing; 1: start crawling; 2: finished; 3: Saved to asking
	 * @return
	 */

	public List<queuing> GetQueuingInfo(int type);

	/**
	 * 
	 * Change the status in queing table
	 */
	public void ChangeQueuingStatus(ArrayList<queuing> queuingList, int type);

	/**
	 * Insert one piece of data into the database
	 * 
	 * @param msg
	 */
	public void insert(queuing que);

	/**
	 * Insert multiple pieces of data into databases
	 * 
	 * @param msgs
	 */
	public void insert(List<queuing> ques);
}