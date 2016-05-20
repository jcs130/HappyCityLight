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

import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.dao.AskingInterface;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.dao.ipml.AskingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.dao.ipml.QueuingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model.asking;
import com.citydigitalpulse.collector.NonRealTimeInstagramGetter.model.queuing;

/**
 * Scan DB and get requirements. Store infor into queuing DB and change the
 * status of asking table
 * 
 * @author yuanyuan
 *
 */
public class MonitoringAskingThread extends ServiceThread {
	private int time;
	private boolean isRunning = false;
	private AskingInterface inforGetter;
	private QueuingInterface_MySQL saveQueuingMsg = new QueuingInterface_MySQL();

	public MonitoringAskingThread(int time) {
		this.time = time;
		this.settName(this.getClass().getSimpleName());
		this.inforGetter = new AskingInterface_MySQL();
	}

	@Override
	public void run() {
		super.run();
		isRunning = true;
		// Scan every time second
		while (isRunning) {
			System.out.println("Scann asking table");
			// 1.Scan asking table, get all the requirement with status 0
			ArrayList<asking> askingPlaces = (ArrayList<asking>) inforGetter.GetAskingInfo(0);
			ArrayList<queuing> queList = new ArrayList<queuing>();
			// System.out.println(askingPlaces);
			// 2. Use the lat and long get place ids
			for (int i = 0; i < askingPlaces.size(); i++) {

				queuing que = new queuing();
				// Set the task id in queuing table as the num_id in asking
				// table
				que.set_task_id(askingPlaces.get(i).get_num_id());
				que.set_place_name(askingPlaces.get(i).get_city_name());
				que.set_boundingBoxCoordinatesLatitude(askingPlaces.get(i).get_latitude());
				que.set_boundingBoxCoordinatesLongitude(askingPlaces.get(i).get_longitude());
				que.set_start_date(askingPlaces.get(i).get_start_date());
				que.set_end_date(askingPlaces.get(i).get_end_date());
				que.set_in_what_lan(askingPlaces.get(i).get_in_what_lan());
				que.set_message_from(askingPlaces.get(i).get_message_from());
				que.set_status(0);// ready to start
				queList.add(que);
			}
			if (queList != null && queList.size() != 0) {
				System.out.println("Insert into queuing table");
				saveQueuingMsg.insert(queList);// Save task to queuing table
				// change status to ready (1) in asking table
				inforGetter.ChangeAskingStatus(queList, 1);
			}
			try {
				sleep(time);
			} catch (InterruptedException e) {
				isRunning = false;
				// System.out.println("Thread: " + this.gettName() + "
				// Finished.");
				// e.printStackTrace();
			}
		}
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