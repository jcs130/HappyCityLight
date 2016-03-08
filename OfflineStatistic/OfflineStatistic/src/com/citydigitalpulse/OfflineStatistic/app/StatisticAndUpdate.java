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
import java.util.HashMap;
import java.util.List;

import com.citydigitalpulse.OfflineStatistic.dao.impl.StatisticDaoImpl;
import com.citydigitalpulse.OfflineStatistic.model.EmotionObj;
import com.citydigitalpulse.OfflineStatistic.model.RegInfo;
import com.citydigitalpulse.OfflineStatistic.model.StatiisticsRecord;
import com.citydigitalpulse.OfflineStatistic.model.StructuredFullMessage;
import com.citydigitalpulse.OfflineStatistic.tool.Tools;
import com.citydigitalpulse.OfflineStatistic.tool.senitool.SentimentClassifier;
import com.citydigitalpulse.OfflineStatistic.tool.senitool.ZLSentiment_en;

/**
 * 用于统计数据和更新信息
 * 
 * @author Zhongli Li
 *
 */
public class StatisticAndUpdate {
	private StatisticDaoImpl statisticDB;
	// private String date_start, date_end;
	private SimpleDateFormat sdf;

	// private SentimentClassifier ZLSentiment_en;

	private void init() {
		statisticDB = new StatisticDaoImpl();
		Tools.readStatisticSettingFile();
		this.sdf = new SimpleDateFormat(AppConfig.date_format);
		// sentiStrength_en = new SentiStrengthNLP_en("SentStrength_Data/");
		// ZLSentiment_en = new ZLSentiment_en("models/",
		// "libSVM_(Saima Aman Data Set).model");

	}

	public static void main(String[] args) {
		StatisticAndUpdate so = new StatisticAndUpdate();
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
		for (long i = AppConfig.START_DATE.getTime(); i <= AppConfig.END_DATE
				.getTime(); i += dayTime) {
			// int work_num = 0;
			System.out.println(i);
			StatisticAndUpdateThread t = new StatisticAndUpdateThread(
					statisticDB, regList, sdf, i);
			threads.add(t);
			t.start();
			// while (true) {
			// work_num = 0;
			// // 判断同时进行的任务数是否超过5个，如果超过，则等待
			// for (int j = 0; j < threads.size(); j++) {
			// if (!threads.get(j).isDone()) {
			// work_num += 1;
			// }
			// }
			// if (work_num <2) {
			// break;
			// } else {
			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			// }
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
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	// private void work() {
	//
	// // 获得监听地区的具体消息
	// List<RegInfo> regList = statisticDB.getRegInfo();
	// RegInfo tempReg = null;
	// long start_id = 0;
	// long dayTime = 3600000 * 24;
	// String table_name = "";
	// String date_string = "";
	// List<StructuredFullMessage> change_list = new
	// ArrayList<StructuredFullMessage>();
	// // 用来地区统计记录的键值对(reg_ig,record)
	// HashMap<Integer, StatiisticsRecord> oneDayRecord = new HashMap<Integer,
	// StatiisticsRecord>();
	// // 读取指定天的数据 数据库的格式为part_message_yyyy_MM_dd
	// for (long i = AppConfig.START_DATE.getTime(); i <= AppConfig.END_DATE
	// .getTime(); i += dayTime) {
	// date_string = sdf.format(new Date(i));
	// table_name = "part_message_" + date_string;
	// start_id = 0;
	// oneDayRecord.clear();
	// // 循环读取一天的数据库，并统计，直到读取完所有数据
	// while (true) {
	// // 存储查询结果的列表
	// ArrayList<StructuredFullMessage> queryResult = statisticDB
	// .getFilteredOneDayMessages(table_name, start_id,
	// AppConfig.LIMIT);
	// // 若无结果则跳出循环
	// if (queryResult.size() == 0) {
	// break;
	// }
	// for (int j = 0; j < queryResult.size(); j++) {
	// StructuredFullMessage temp = queryResult.get(j);
	// // 更新语言标记
	// if (temp.getLang().equals("en")) {
	// // EmotionObj emo =
	// // sentiStrength_en.getTextSentiment(temp
	// // .getText());
	// EmotionObj emo = ZLSentiment_en.getTextSentiment(temp
	// .getText());
	// // System.out.println(temp.getText() + emo);
	// // 如果结果不同，则更新数据库中结果
	// if (temp.getEmotion_text_value() != emo.getValue()
	// || !temp.getEmotion_text().equals(
	// emo.getEmotion())) {
	// temp.setEmotion_text(emo.getEmotion());
	// temp.setEmotion_text_value(emo.getValue());
	// // 一次更新多条记录，新建一个列表
	// change_list.add(temp);
	// // statisticDB.insertMessage2Table(table_name,
	// // temp);
	// }
	// } else {
	// // other language.
	// }
	// ArrayList<RegInfo> cotainRegs = statisticDB
	// .getRegInfoByLocation(regList,
	// temp.getQuery_location_latitude(),
	// temp.getQuery_location_langtitude());
	// // 根据经纬度找到所在区城市并且获得区域信息
	// for (int k = 0; k < cotainRegs.size(); k++) {
	// tempReg = cotainRegs.get(k);
	// // 检测是否有该地区ID,如果有，则直接统计记录
	// if (oneDayRecord.containsKey(tempReg.getRegID())) {
	// oneDayRecord.get(tempReg.getRegID()).addNewRecord(
	// tempReg, temp);
	// } else {
	// // 添加该区域的记录
	// StatiisticsRecord firstRecord = new StatiisticsRecord(
	// date_string);
	// firstRecord.addNewRecord(tempReg, temp);
	// oneDayRecord.put(tempReg.getRegID(), firstRecord);
	// }
	// }
	// start_id = temp.getNum_id();
	// }
	// if (queryResult.size() < AppConfig.LIMIT) {
	// // 一次性更新更改的数据
	// statisticDB.updateMutipalRecords(table_name, change_list);
	// System.out.println("Changes:" + change_list.size());
	// change_list.clear();
	// break;
	// }
	// }
	// if (!oneDayRecord.isEmpty()) {
	// // 存储或更新当天的数据
	// statisticDB.saveRecord2Database(oneDayRecord);
	// }
	// AppConfig.START_DATE = new Date(i);
	// System.out.println("Date: " + sdf.format(AppConfig.START_DATE)
	// + " Done.");
	// // Tools.updateStatisticStartDate();
	// }
	// }
}
