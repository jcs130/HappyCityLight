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
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml.AskingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml.QueuingInterface_MySQL;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.taskStatus;

/**
 * Change the status of asking table
 */
public class ChangingAskingStatusThread extends ServiceThread {
	private int time;
	private boolean isRunning = false;
	private QueuingInterface queueGetter;
	private AskingInterface_MySQL inforGetter;

	public ChangingAskingStatusThread(int time) {
		this.time = time;
		this.settName(this.getClass().getSimpleName());
		this.queueGetter = new QueuingInterface_MySQL();
		this.inforGetter = new AskingInterface_MySQL();
	}

	@Override
	public void run() {
		super.run();
		isRunning = true;
		ArrayList<taskStatus> taskList;
		ArrayList<Integer> taskIDs;
		while (isRunning) {
			// 1.Scan queuing table and get task_id and status of the
			// queuing tasks with no duplicates
			// taskList = queueGetter.GetQueuingTaskStatus();
			taskIDs = queueGetter.GetQueuingTaskIDs();

			for (int i = 0; i < taskIDs.size(); i++) {
				// equal with next one
				ArrayList<taskStatus> equal = new ArrayList<taskStatus>();
				equal = queueGetter.GetQueuingTaskStatus(taskIDs.get(i));
				boolean flag1 = false;
				boolean flag2 = false;
				boolean flag9 = true;
				int size = equal.size();
				// No 0,1,4,5,6,7,8
				for (int m = 0; m < size; m++) {
					if (equal.get(m).getStatus() == 0 || equal.get(m).getStatus() == 1 || equal.get(m).getStatus() == 4
							|| equal.get(m).getStatus() == 5 || equal.get(m).getStatus() == 6
							|| equal.get(m).getStatus() == 7 || equal.get(m).getStatus() == 8) {
						flag1 = true;
						break;
					}
				}
				// has status 2 which means finished with results
				for (int m = 0; m < size; m++) {
					if (equal.get(m).getStatus() == 2) {
						flag2 = true;
						break;
					}
				}

				// only has status 9
				for (int m = 0; m < size; m++) {
					if (equal.get(m).getStatus() != 9) {
						flag9 = false;
						break;
					}
				}

				// No 0 or 1 or 4-8 but has 2. Finished
				if (flag2 == true && flag1 == false) {
					inforGetter.ChangeAskingStatus(equal.get(0).getTask_id(), 3);
				}
				// No 0,1,2 but only 9, no results
				if (flag9) {
					inforGetter.ChangeAskingStatus(equal.get(0).getTask_id(), 4);
				}
			}
		}
		try {
			sleep(time);
		} catch (InterruptedException e) {
			isRunning = false;
			// e.printStackTrace();
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