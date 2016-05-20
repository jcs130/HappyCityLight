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
import java.util.HashSet;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml.AskingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml.QueuingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.queuing;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.url.urlConnectionGetter;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.url.urlGetterNear;

/**
 * Crawling tweets thread
 *
 */
public class CrawlingThread extends ServiceThread {
	private ArrayList<queuing> queuingList;
	private boolean isRunning = true;
	private urlConnectionGetter urlGetter;
	private urlGetterNear urlGetterNear;
	private AskingInterface_MySQL askingInterface_MySQL;
	private QueuingInterface_MySQL queuingInterface_MySQL;

	public CrawlingThread(ArrayList<queuing> queuingPlaces) {
		this.queuingList = queuingPlaces;
		this.urlGetter = new urlConnectionGetter();
		this.urlGetterNear = new urlGetterNear();
		this.askingInterface_MySQL = new AskingInterface_MySQL();
		this.queuingInterface_MySQL = new QueuingInterface_MySQL();
	}

	@Override
	public void run() {
		super.run();
		System.out.println("Start crawling");
		// Change the status in asking table to 2. Once there is a queing
		// task starts, we can say the asking request by a user has started
		HashSet<Integer> taskIds = new HashSet<Integer>();
		// Use hashset to avoid duplicates
		for (int m = 0; m < queuingList.size(); m++) {
			taskIds.add(queuingList.get(m).get_task_id());
		}
		askingInterface_MySQL.ChangeAskingStatus(taskIds, 2);
		ArrayList<queuing> statusOne = new ArrayList<queuing>();
		ArrayList<queuing> statusZero = new ArrayList<queuing>();
		for (int i = 0; i < queuingList.size(); i++) {
			urlGetter.Getter(queuingList.get(i));// using placeids
			urlGetterNear.Getter(queuingList.get(i));// using place names
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
		// Change the status in queuing table to 4
		queuingInterface_MySQL.ChangeQueuingStatus(statusZero, 4);
		this.stopMe();
		// System.out.println("Thread: " + this.gettName() + " Finished.");
		// }
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
