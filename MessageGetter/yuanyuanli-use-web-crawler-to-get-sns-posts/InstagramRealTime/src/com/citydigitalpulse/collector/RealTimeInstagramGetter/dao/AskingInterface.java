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
package com.citydigitalpulse.collector.RealTimeInstagramGetter.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.citydigitalpulse.collector.RealTimeInstagramGetter.model.asking;

/**
 * Interface of DB
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
	 * Get the status from DB
	 * 
	 * @param type
	 *            0:user asking tweets for a time period 1: program start
	 *            crawling 2: user cancelled the request 3: crawl finished
	 * @return
	 */

	public List<asking> GetAskingInfo(int type);

	/**
	 * Change the status of DB
	 * 
	 * @param id
	 * @param type
	 */
	public void ChangeAskingStatus(ArrayList<asking> queList, int type);

}
