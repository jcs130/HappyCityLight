
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
package com.citydigitalpulse.collector.NonRealTimeInstagramGetter.app;

import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.thread.CrawlingThreadManager;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.thread.MonitoringAskingThread;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.thread.ThreadsPool;

/**
 * program starter
 * 
 */
public class starter {
	/// Scan asking table every
	public static void main(String args[]) {
		starter st = new starter();
		st.startGetter(10000, 300);
	}

	private void startGetter(int monitorAskTimeSlot, int CrawlingTimeSlot) {
		MonitoringAskingThread monitorAskingThread = new MonitoringAskingThread(monitorAskTimeSlot);
		ThreadsPool.addCrawlingThread(monitorAskingThread);
		CrawlingThreadManager crawlingThreadManager = new CrawlingThreadManager(CrawlingTimeSlot);
		ThreadsPool.addCrawlingThread(crawlingThreadManager);

	}
}
