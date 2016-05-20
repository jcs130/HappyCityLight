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

/**
 * Static class of running threads
 */
public class ThreadsPool {
	private static ArrayList<ServiceThread> serverThreads = new ArrayList<ServiceThread>();

	private static int ThreadNum = 0;

	public static void addCrawlingThread(ServiceThread serviceThread) {
		// set thread names
		String threadName = setThreadName(serviceThread.getClass().getSimpleName());
		serviceThread.settName(threadName);
		// add to thread list
		addThread(serviceThread);
		// change number of threads
		ThreadNum++;

	}

	// get the number of thread name
	private static String setThreadName(String className) {
		String name = "" + className;
		for (int i = 0; i < ThreadNum; i++) {
			if (!hasSameName(className + i)) {
				name += i;
				return name;
			}
		}
		name += ThreadNum;
		return name;
	}

	public static int getTwitterStreamThreadsNum() {
		return ThreadNum;
	}

	/**
	 * add a thread
	 * 
	 * @param tName
	 * @param t
	 */
	public static boolean addThread(ServiceThread serviceThread) {
		// name is unique then added successfully
		if (!hasSameName(serviceThread.gettName())) {
			serviceThread.start();
			serverThreads.add(serviceThread);
			//System.out.println(serviceThread.gettName() + " Start ");
			return true;
		} else {

		}
		return false;
	}

	/**
	 * Check if there is duplicated names
	 * 
	 * @param name
	 * @return
	 */
	private static boolean hasSameName(String name) {
		for (int i = 0; i < serverThreads.size(); i++) {
			if (serverThreads.get(i).gettName().toLowerCase().equals(name.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * stop one thread
	 * 
	 * @param tName
	 * @return
	 */
	public static boolean stopThread(String tName) {
		for (int i = 0; i < serverThreads.size(); i++) {
			if (serverThreads.get(i).gettName().toLowerCase().equals(tName.toLowerCase())) {
				// if (serverThreads.get(i).isAlive()) {
				serverThreads.get(i).stopMe();
				// }
				serverThreads.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * delete one thread
	 * 
	 * @param tName
	 * @return
	 */
	public static boolean removeThread(String tName) {
		for (int i = 0; i < serverThreads.size(); i++) {
			if (serverThreads.get(i).gettName().toLowerCase().equals(tName.toLowerCase())) {
				// if (serverThreads.get(i).isAlive()) {
				serverThreads.get(i).stopMe();
				// }
				serverThreads.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Get a thread throung name
	 * 
	 * @param tName
	 * @return
	 */
	public static ServiceThread getThread(String tName) {
		for (int i = 0; i < serverThreads.size(); i++) {
			if (serverThreads.get(i).gettName().toLowerCase().equals(tName.toLowerCase())) {
				return serverThreads.get(i);
			}
		}
		return null;
	}

	/**
	 * Get the list of running thread
	 * 
	 * @return
	 */
	public static ArrayList<String> getRunningThreadsList() {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < serverThreads.size(); i++) {
			result.add(serverThreads.get(i).gettName());
		}
		return result;
	}

}
