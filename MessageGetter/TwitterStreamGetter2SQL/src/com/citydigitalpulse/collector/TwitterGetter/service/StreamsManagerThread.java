/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Zhongli Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.collector.TwitterGetter.service;

import java.util.ArrayList;

import com.citydigitalpulse.collector.TwitterGetter.dao.InfoGetterDAO;
import com.citydigitalpulse.collector.TwitterGetter.dao.impl.InfoGetterDAO_MySQL;
import com.citydigitalpulse.collector.TwitterGetter.model.EarthSqure;
import com.citydigitalpulse.collector.TwitterGetter.service.twitter4j.TwitterStreamThread;
import com.citydigitalpulse.collector.TwitterGetter.service.twitter4j.TwitterTools;

/**
 * 管理Stream监视线程的管理线程，查询区块表，获取所有使用次数大于0且运行状态为0（停止）的区块，新建相应的Stream监听线程
 * 
 * @author zhonglili
 *
 */
public class StreamsManagerThread {
	private TwitterTools tt;

	public StreamsManagerThread() {
		this.tt = new TwitterTools();
	}

	public void startBuildThreads() {
		InfoGetterDAO db = new InfoGetterDAO_MySQL();
		ArrayList<EarthSqure> ess;
		// 需要修改县城侧罗防止由于死所带来的数据丢失问题
		// 从数据库中获取使用次数>0但是没有被监视的Stream区块
		ess = (ArrayList<EarthSqure>) db.getReadySqure();
		System.out.println("Squares: " + ess.size());
		// 新建监视线程，每个监视线程监视N个区块
		buildStreamthreads(20, ess);
	}

	// 将多个区块合并为个线程
	private void buildStreamthreads(int size, ArrayList<EarthSqure> ess) {
		ArrayList<EarthSqure> smallList = new ArrayList<EarthSqure>();
		for (int i = 0; i < ess.size(); i++) {
			// 如果有线程的监视数量小于size，则将这个区块分配给该线程
			if (!insert2Thread(ess.get(i))) {
				if (smallList.size() < size) {
					smallList.add(ess.get(i));
				} else {
					TwitterStreamThread tst = new TwitterStreamThread(
							smallList, tt);
					ThreadsPool.addTwitterStreamThread(tst);
					smallList.clear();
					smallList.add(ess.get(i));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
		if (smallList.size() > 0) {
			TwitterStreamThread tst = new TwitterStreamThread(smallList, tt);
			ThreadsPool.addTwitterStreamThread(tst);
		}

	}

	// 插入方法暂未实现
	private boolean insert2Thread(EarthSqure earthSqure) {
		// for (int j = 0; j < ThreadsPool.getTwitterStreamThreadsNum(); j++) {
		//
		// }
		return false;
	}
}
