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

/**
 * Scan asking table and get requirements. Get place IDs based on latitude and
 * longtude.Store obtained information into queuing table and change the status
 * of asking table. Status in asking table: 0,User asked but not queued in
 * queuing table or started; 1, user requests accepted and queuing; 2, working
 * on this request; 3, no results found for this request; 4, finished; 5, failed
 * to get geometry information, wrong latitude and longitude pair.
 *
 */
public class CrawlingThreadManager extends ServiceThread {
	private int time;// waiting time
	private boolean isRunning = false;
	private QueuingInterface_MySQL QueuingInfor;

	public CrawlingThreadManager(int time) {
		this.time = time;
		this.settName(this.getClass().getSimpleName());
		this.QueuingInfor = new QueuingInterface_MySQL();

	}

	@Override
	public void run() {
		super.run();
		isRunning = true;
		ArrayList<queuing> queList;
		while (isRunning) {
			System.out.println("Scaning Queuing table where status = 0");
			queList = (ArrayList<queuing>) QueuingInfor.GetQueuingInfo(0);
			// Change status
			QueuingInfor.ChangeQueuingStatus(queList, 1);
			// Build thread
			buildCrawlThread(queList);
			try {
				sleep(time);
			} catch (InterruptedException e) {
				isRunning = false;
				// e.printStackTrace();
			}
		}
	}

	private void buildCrawlThread(ArrayList<queuing> queList) {
		CrawlingThread crawlThread = new CrawlingThread(queList);
		ThreadsPool.addCrawlingThread(crawlThread);
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