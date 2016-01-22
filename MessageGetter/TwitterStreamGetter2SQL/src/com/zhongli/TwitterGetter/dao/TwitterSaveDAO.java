package com.zhongli.TwitterGetter.dao;

import java.util.List;

import com.zhongli.TwitterGetter.model.StructuredFullMessage;

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
	 */
	public void insert(StructuredFullMessage msg);

	/**
	 * Insert many data into databases
	 * 
	 * @param msgs
	 */
	public void insert(List<StructuredFullMessage> msgs);

}
