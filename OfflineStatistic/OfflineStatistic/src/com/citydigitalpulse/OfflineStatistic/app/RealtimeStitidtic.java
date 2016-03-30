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
package com.citydigitalpulse.OfflineStatistic.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.citydigitalpulse.OfflineStatistic.dao.impl.StatisticDaoImpl;
import com.citydigitalpulse.OfflineStatistic.model.RegInfo;
import com.citydigitalpulse.OfflineStatistic.tool.Tools;
import com.citydigitalpulse.OfflineStatistic.tool.senitool.Translater;

/**
 * @author Zhongli Li
 *
 */
public class RealtimeStitidtic {
	private StatisticDaoImpl statisticDB;
	// private String date_start, date_end;
	private SimpleDateFormat sdf;
	private Translater translater;

	// private SentimentClassifier ZLSentiment_en;

	private void init() {
		statisticDB = new StatisticDaoImpl();
		Tools.readStatisticSettingFile();
		this.sdf = new SimpleDateFormat(AppConfig.date_format);
		// sentiStrength_en = new SentiStrengthNLP_en("SentStrength_Data/");
		// ZLSentiment_en = new ZLSentiment_en("models/",
		// "libSVM_(Saima Aman Data Set).model");
		translater = new Translater();

	}

	public static void main(String[] args) {
		RealtimeStitidtic so = new RealtimeStitidtic();
		so.init();
		// 统计一个数据库中的信息并且更新或者插入记录
		// so.work();
		so.work_muti_thread();
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void work_muti_thread() {
		// boolean isDone = false;
		// 获得监听地区的具体消息
		List<RegInfo> regList = statisticDB.getRegInfo();
		long dayTime = 3600000 * 24;
		List<StatisticAndUpdateThread> threads = new ArrayList<StatisticAndUpdateThread>();
		// 读取指定天的数据 数据库的格式为part_message_yyyy_MM_dd
		// 更新前一天的数据
		long yesterday = new Date().getTime() - dayTime;
		int work_num = 0;
		StatisticAndUpdateThread t = new StatisticAndUpdateThread(statisticDB,
				regList, sdf, yesterday, translater);
		threads.add(t);
		t.start();
		t = new StatisticAndUpdateThread(statisticDB, regList, sdf, yesterday
				- dayTime, translater);
		threads.add(t);
		t.start();
		while (true) {
			work_num = 0;
			// 判断同时进行的任务数是否超过5个，如果超过，则等待
			for (int j = 0; j < threads.size(); j++) {
				if (!threads.get(j).isDone()) {
					work_num += 1;
				}
			}
			if (work_num < 6) {
				break;
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Waiting.....");
		// 循环等待知道所有进程结束
		while (true) {
			int num = 0;
			for (int i = 0; i < threads.size(); i++) {
				if (threads.get(i).isDone()) {
					num += 1;
				}
			}
			System.out.println(threads.size() + "<>" + num);
			if (num == threads.size()) {
				break;
			} else {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
