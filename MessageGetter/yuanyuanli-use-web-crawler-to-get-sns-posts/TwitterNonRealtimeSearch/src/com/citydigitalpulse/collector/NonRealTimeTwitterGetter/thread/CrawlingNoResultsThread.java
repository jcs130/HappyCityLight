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

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml.QueuingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.queuing;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.url.urlConnectionGetter;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.url.urlGetterNear;

/**
 * Crawling tweets thread
 *
 */
public class CrawlingNoResultsThread extends ServiceThread {
	private ArrayList<queuing> queuingList;
	private boolean isRunning = false;
	private urlConnectionGetter urlGetter;
	private urlGetterNear urlGetterNear;
	private QueuingInterface_MySQL queuingInterface_MySQL;

	public CrawlingNoResultsThread(ArrayList<queuing> queuingPlaces) {
		this.queuingList = queuingPlaces;
		this.urlGetter = new urlConnectionGetter();
		this.urlGetterNear = new urlGetterNear();
		this.queuingInterface_MySQL = new QueuingInterface_MySQL();
	}

	@Override
	public void run() {
		super.run();
		ArrayList<queuing> statusOne = new ArrayList<queuing>();
		ArrayList<queuing> statusZero = new ArrayList<queuing>();
		for (int i = 0; i < queuingList.size(); i++) {
			urlGetter.Getter(queuingList.get(i));
			urlGetterNear.Getter(queuingList.get(i));
			int crawlStatusUrlGetter = urlGetter.get_CrawlStatus();
			int crawlStatusUrlGetterNear = urlGetterNear.get_CrawlStatus();
			// One of the getting method finished with results
			if (crawlStatusUrlGetter == 1 || crawlStatusUrlGetterNear == 1) {
				statusOne.add(queuingList.get(i));
			}

			// Both finished with no results
			if (crawlStatusUrlGetter == 0 && crawlStatusUrlGetterNear == 0) {
				statusZero.add(queuingList.get(i));
			}
		}
		// Change the status in queuing table to 2
		queuingInterface_MySQL.ChangeQueuingStatus(statusOne, 2);
		// Change the status in queuing table to status+1
		queuingInterface_MySQL.ChangeQueuingStatus(statusZero, 9);
		this.stopMe();
	}

	@Override
	public void stopMe() {
		isRunning = false;
		if (this.isAlive()) {
			super.interrupt();
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

}
