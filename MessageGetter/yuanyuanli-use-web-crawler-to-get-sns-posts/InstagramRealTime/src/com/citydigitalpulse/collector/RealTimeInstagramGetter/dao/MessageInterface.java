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

import java.util.List;

import com.citydigitalpulse.collector.RealTimeInstagramGetter.model.MessageModel;

/**
 * message data access object
 * 
 * @author yuanyuan
 *
 */
public interface MessageInterface {

	/**
	 * Insert one piece of data into the database
	 * 
	 * @param msg
	 */
	public long insert(MessageModel msg);

	/**
	 * Insert multiple pieces of data into databases
	 * 
	 * @param msgs
	 */

	public void insert(List<MessageModel> msgs);

}