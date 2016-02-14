package com.citydigitalpulse.collector.TwitterGetter.dao;

import java.util.List;

import com.citydigitalpulse.collector.TwitterGetter.model.StructuredFullMessage;

/**
 * Twitter message data access object
 * 
 * @author John
 *
 */
public interface TwitterSaveDAO {

	/**
	 * Insert one data into the database
	 * 
	 * @param msg
	 * @return
	 */
	public long insert(StructuredFullMessage msg);

	/**
	 * Insert many data into databases
	 * 
	 * @param msgs
	 */
	public void insert(List<StructuredFullMessage> msgs);

}
