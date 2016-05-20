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
package com.citydigitalpulse.collector.NonRealTimeInstagramGetter.thread;

import java.util.ArrayList;

import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.dao.QueuingInterface;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.dao.ipml.QueuingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model.queuing;

/**
 * Scan queuing table and set status to 1. Get ready data start new thread to
 * crawl data
 * 
 * @author yuanyuan
 *
 */
public class CrawlingThreadManager extends ServiceThread {
	private int time;
	private boolean isRunning = false;
	private QueuingInterface queueGetter;

	public CrawlingThreadManager(int time) {
		this.time = time;
		this.settName(this.getClass().getSimpleName());
		this.queueGetter = new QueuingInterface_MySQL();
	}

	@Override
	public void run() {
		super.run();
		isRunning = true;
		while (isRunning) {
			// 1.Scan queuing table, get all the requirement with status 0
			ArrayList<queuing> queuingPlaces = (ArrayList<queuing>) queueGetter.GetQueuingInfo(0);
			// System.out.println(queuingPlaces);
			// 2. Use infor in queuing table
			// Build new thread, start crawling
			buildThread(queuingPlaces);
			try {
				sleep(time);
			} catch (InterruptedException e) {
				isRunning = false;
				System.out.println("Thread: " + this.gettName() + " Finished.");
				// e.printStackTrace();
			}
		}
	}

	private void buildThread(ArrayList<queuing> queuingPlaces) {
		CrawlingThread crawlingThread = new CrawlingThread(queuingPlaces);
		ThreadsPool.addCrawlingThread(crawlingThread);
	}

	/**
	 * 终止线程的方法
	 */
	@Override
	public void stopMe() {
		isRunning = false;
		if (this.isAlive()) {
			super.interrupt();
		}
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public boolean isRunning() {
		return isRunning;
	}

}