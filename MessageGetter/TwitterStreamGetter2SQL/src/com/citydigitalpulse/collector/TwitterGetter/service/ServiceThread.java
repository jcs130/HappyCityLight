/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Zhongli Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.collector.TwitterGetter.service;

/**
 * 为了方便管理，增加线程的几个通用方法
 * 
 * @author zhonglili
 *
 */
public abstract class ServiceThread extends Thread {
	private String tName;

	public String gettName() {
		return tName;
	}

	public void settName(String tName) {
		this.tName = tName;
	}
	
	public abstract void stopMe();

}