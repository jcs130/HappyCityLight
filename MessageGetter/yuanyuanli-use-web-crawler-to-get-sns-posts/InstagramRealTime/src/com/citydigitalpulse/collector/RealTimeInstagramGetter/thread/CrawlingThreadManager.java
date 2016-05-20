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
package com.citydigitalpulse.collector.RealTimeInstagramGetter.thread;

import java.text.ParseException;
import java.util.ArrayList;

import com.citydigitalpulse.collector.RealTimeInstagramGetter.dao.AskingInterface;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.dao.ipml.AskingInterface_MySQL;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.model.asking;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.tool.Tools;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.url.urlConnectionGetter;

/**
 * Scan queuing table and set status to 1. Get ready data start new thread to
 * crawl data
 */
public class CrawlingThreadManager extends ServiceThread {
	private int time;
	private boolean isRunning = false;
	private AskingInterface queueGetter;
	private ArrayList<asking> askingLists;
	private urlConnectionGetter urlGetter;

	public CrawlingThreadManager(int time) {
		this.time = time;
		this.settName(this.getClass().getSimpleName());
		this.queueGetter = new AskingInterface_MySQL();
		this.askingLists = new ArrayList<asking>();
	}

	@Override
	public void run() {
		super.run();
		isRunning = true;
		// ArrayList<asking> queuingPlaces;
		while (isRunning) {
			// 1.Scan queuing table, get first 10 requirements with status 0
			askingLists.addAll(queueGetter.GetAskingInfo(0));
			// Change the status in queuing table to 1
			queueGetter.ChangeAskingStatus(askingLists, 1);
			// 2. Use infor in queuing tablestart crawling
			try {
				urlGetter = new urlConnectionGetter();
				urlGetter.Getter(askingLists, Tools.getCurrentTimeStamp());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				sleep(time);
			} catch (InterruptedException e) {
				isRunning = false;
				// e.printStackTrace();
			}
		}
	}

	/*
	 * private void buildThread(ArrayList<asking> queuingPlaces) {
	 * CrawlingThread crawlingThread = new CrawlingThread(queuingPlaces);
	 * ThreadsPool.addCrawlingThread(crawlingThread); }
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