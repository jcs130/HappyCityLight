package com.zhongli.TwitterGetter.service;

import java.util.ArrayList;

import com.zhongli.TwitterGetter.service.twitter4j.TwitterStreamThread;

/**
 * 存储正在运行的线程的静态类
 * 
 * @author zhonglili
 *
 */
public class ThreadsPool {
	private static ArrayList<ServiceThread> serverThreads = new ArrayList<ServiceThread>();
	// jianshiStream区块的线程的数量
	private static int twitterStreamThreadNum = 0;

	public static void addTwitterStreamThread(TwitterStreamThread tst) {
		// 1.设置名称,避免名称中出现空洞
		String threadName = makeTwitterStreamThreadName(tst.getClass()
				.getSimpleName());
		tst.settName(threadName);
		// 2.添加到线程列表
		addThread(tst);
		// 3.修改数量
		twitterStreamThreadNum++;
		// 4.将线程名称存入数据库
		tst.saveThreadname();
		// 5.启动监视线程？
		// tst.start();
		tst.startListening();

	}

	private static String makeTwitterStreamThreadName(String className) {
		String name = "" + className;
		for (int i = 0; i < twitterStreamThreadNum; i++) {
			if (!hasSameName(className + i)) {
				// 如果发现空洞则使用这个数字命名
				name += i;
				return name;
			}
		}
		name += twitterStreamThreadNum;
		return name;
	}

	public static int getTwitterStreamThreadsNum() {
		return twitterStreamThreadNum;
	}

	/**
	 * 增加一个线程
	 * 
	 * @param tName
	 * @param t
	 */
	public static boolean addThread(ServiceThread t) {
		// 如果名字唯一则增加成功
		if (!hasSameName(t.gettName())) {
			t.start();
			serverThreads.add(t);
			System.out.println(t.gettName() + " 开始运行..");
			return true;
		} else {
			// 自动改名？
		}
		return false;
	}

	/**
	 * 检测是否有相同的名字
	 * 
	 * @param name
	 * @return
	 */
	private static boolean hasSameName(String name) {
		for (int i = 0; i < serverThreads.size(); i++) {
			if (serverThreads.get(i).gettName().toLowerCase()
					.equals(name.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 停止并删除一个线程
	 * 
	 * @param tName
	 * @return
	 */
	public static boolean stopThread(String tName) {
		for (int i = 0; i < serverThreads.size(); i++) {
			if (serverThreads.get(i).gettName().toLowerCase()
					.equals(tName.toLowerCase())) {
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
	 * 停止并删除一个线程
	 * 
	 * @param tName
	 * @return
	 */
	public static boolean removeThread(String tName) {
		for (int i = 0; i < serverThreads.size(); i++) {
			if (serverThreads.get(i).gettName().toLowerCase()
					.equals(tName.toLowerCase())) {
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
	 * 通过线程名字得到一个线程
	 * 
	 * @param tName
	 * @return
	 */
	public static ServiceThread getThread(String tName) {
		for (int i = 0; i < serverThreads.size(); i++) {
			if (serverThreads.get(i).gettName().toLowerCase()
					.equals(tName.toLowerCase())) {
				return serverThreads.get(i);
			}
		}
		return null;
	}

	/**
	 * 得到正在运行的线程的名字列表
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
