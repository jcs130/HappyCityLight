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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.app;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.thread.ChangingAskingStatusThread;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.thread.CrawlingNoResultsThreadManager;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.thread.CrawlingThreadManager;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.thread.MonitoringAskingThread;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.thread.ThreadsPool;

/**
 * Program starter
 */
public class starter {
	public static void main(String args[]) {
		starter st = new starter();
		// Asking thread visits asking table every 10 minutes due to API limits.
		// Queuing thread visits all the queued visits every 5 minutes
		// Sometimes the crawler get no results so start crawl again.
		// NoResultsTimeSlot starts every 10 minutes
		// If one queuing task starts then the asking task starts but all the
		// queuing tasks finished the asking task is considered finished. The
		// changing status thread works every 10 minutes
		st.startGetter(60000);
	}

	private void startGetter(int TimeSlot) {
		MonitoringAskingThread AskingThread = new MonitoringAskingThread(TimeSlot);
		ThreadsPool.addCrawlingThread(AskingThread);
		CrawlingThreadManager crawlingThreadManager = new CrawlingThreadManager(TimeSlot);
		ThreadsPool.addCrawlingThread(crawlingThreadManager);
		CrawlingNoResultsThreadManager NoResultsThread = new CrawlingNoResultsThreadManager(TimeSlot);
		ThreadsPool.addCrawlingThread(NoResultsThread);
		ChangingAskingStatusThread changingAskingThread = new ChangingAskingStatusThread(TimeSlot);
		ThreadsPool.addCrawlingThread(changingAskingThread);
	}
}