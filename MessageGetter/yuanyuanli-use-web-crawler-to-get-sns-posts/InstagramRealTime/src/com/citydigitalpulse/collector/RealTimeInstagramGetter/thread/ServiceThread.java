package com.citydigitalpulse.collector.RealTimeInstagramGetter.thread;

/**
 * 
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