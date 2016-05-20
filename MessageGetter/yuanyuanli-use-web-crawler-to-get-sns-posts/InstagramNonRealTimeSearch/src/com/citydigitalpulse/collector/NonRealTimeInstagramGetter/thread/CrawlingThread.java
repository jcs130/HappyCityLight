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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;

import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.dao.ipml.AskingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.dao.ipml.QueuingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model.queuing;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.url.urlConnectionGetter;

/**
 * Crawling tweets thread Status: 0, queuing; 1, working; 2, finished; 3,
 * changed in asking table
 * 
 * @author yuanyuan
 *
 */
public class CrawlingThread extends ServiceThread {
	private ArrayList<queuing> queuingList;
	private boolean isRunning = true;

	public CrawlingThread(ArrayList<queuing> queuingPlaces) {
		this.queuingList = queuingPlaces;
	}

	@Override
	public void run() {
		super.run();
		QueuingInterface_MySQL queuingInforGetterChanger = new QueuingInterface_MySQL();
		AskingInterface_MySQL getRequirInfor = new AskingInterface_MySQL();

		queuingInforGetterChanger.ChangeQueuingStatus(queuingList, 1);
		// Change the status in asking table to 2
		getRequirInfor.ChangeAskingStatus(queuingList, 2);
		ArrayList<queuing> statusOne = new ArrayList<queuing>();
		ArrayList<queuing> statusZero = new ArrayList<queuing>();
		for (int i = 0; i < queuingList.size(); i++) {
			// Change the status in queuing table to 1
			urlConnectionGetter urlGetter = new urlConnectionGetter();
			int crawlStatus = 0;
			try {
				crawlStatus = urlGetter.Getter(queuingList.get(i));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (crawlStatus == 1) {
				statusOne.add(queuingList.get(i));
			}
			if (crawlStatus == 0) {
				statusZero.add(queuingList.get(i));
			}
		}
		queuingInforGetterChanger.ChangeQueuingStatus(statusOne, 2);
		queuingInforGetterChanger.ChangeQueuingStatus(statusZero, 4);
		HashSet<Integer> withResults = new HashSet<Integer>();
		HashSet<Integer> withoutResults = new HashSet<Integer>();
		for (int i = 0; i < statusOne.size(); i++) {
			withResults.add(statusOne.get(i).get_task_id());
		}
		getRequirInfor.ChangeAskingStatus(withResults, 4);
		for (int i = 0; i < statusZero.size(); i++) {
			withoutResults.add(statusOne.get(i).get_task_id());
		}
		getRequirInfor.ChangeAskingStatus(withResults, 3);

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
