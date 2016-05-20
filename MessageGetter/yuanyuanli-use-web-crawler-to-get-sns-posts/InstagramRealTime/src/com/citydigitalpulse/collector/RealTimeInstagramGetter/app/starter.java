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
package com.citydigitalpulse.collector.RealTimeInstagramGetter.app;

import com.citydigitalpulse.collector.RealTimeInstagramGetter.thread.CrawlingThreadManager;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.thread.ThreadsPool;

/**
 * program starter
 * 
 * @author yuanyuan
 */
public class starter {
	public static void main(String args[]) {
		starter st = new starter();
		st.startGetter(300000);
	}

	private void startGetter(int CrawlingTimeSlot) {
		System.out.println("program started");
		CrawlingThreadManager crawlingThreadManager = new CrawlingThreadManager(CrawlingTimeSlot);
		ThreadsPool.addCrawlingThread(crawlingThreadManager);

	}
}
