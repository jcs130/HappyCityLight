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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.thread;

import java.util.ArrayList;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.QueuingInterface;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml.QueuingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.queuing;

/**
 * Scan queuing table get the task with status bigger than 4
 */
public class CrawlingNoResultsThreadManager extends ServiceThread {
	private int time;
	private boolean isRunning = false;
	private QueuingInterface queueGetter;

	public CrawlingNoResultsThreadManager(int time) {
		this.time = time;
		this.settName(this.getClass().getSimpleName());
		this.queueGetter = new QueuingInterface_MySQL();
	}

	@Override
	public void run() {
		super.run();
		isRunning = true;
		ArrayList<queuing> queuingNoResults;
		while (isRunning) {
			System.out.println("Scaning Asking table where status bigger than 3");
			// 1.Scan queuing table, get first requirements with status between
			// 4 and 8
			queuingNoResults = (ArrayList<queuing>) queueGetter.GetQueuingInfo(9);
			// Build new thread, start crawling
			buildThread(queuingNoResults);
			try {
				sleep(time);
			} catch (InterruptedException e) {
				isRunning = false;
				// e.printStackTrace();
			}
		}
	}

	private void buildThread(ArrayList<queuing> queuingPlaces) {
		CrawlingNoResultsThread crawlingNoResultsThread = new CrawlingNoResultsThread(queuingPlaces);
		ThreadsPool.addCrawlingThread(crawlingNoResultsThread);
	}

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