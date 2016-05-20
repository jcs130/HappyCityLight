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

import java.util.HashMap;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.placeID;

/**
 * Interface of place Ids to reduce the use of API
 *
 */
public interface PlaceIdInterface {

	/**
	 * Get the status from queuing DB
	 * 
	 * @param type
	 *            0: waiting to be crawled 1: crawling 2: finished 3: status
	 *            changed in asking table 4: no results
	 * @return
	 */

	public HashMap<String, double[]> GetPlaceID();

	/**
	 * Insert one piece of data into the database
	 * 
	 * @param queuing
	 */
	public void insert(placeID placeid);

}
