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
import java.util.TimeZone;

import com.citydigitalpulse.OfflineStatistic.dao.impl.StatisticDaoImpl;
import com.citydigitalpulse.OfflineStatistic.model.EmotionObj;
import com.citydigitalpulse.OfflineStatistic.model.RegInfo;
import com.citydigitalpulse.OfflineStatistic.model.StructuredFullMessage;
import com.citydigitalpulse.OfflineStatistic.tool.NLPModel;
import com.citydigitalpulse.OfflineStatistic.tool.Tools;

/**
 * 分库与感情识别
 * 
 * @author Zhongli Li
 *
 */
public class SplitDBandAnalysis {
	private StatisticDaoImpl statisticDB;

	public static void main(String[] args) {
		SplitDBandAnalysis sm = new SplitDBandAnalysis();
		sm.work();
	}

	private void init() {
		Tools.readSplitSettingFile();
		statisticDB = new StatisticDaoImpl();
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void work() {
		init();
		// 获得监听地区的具体消息
		List<RegInfo> regList = statisticDB.getRegInfo();
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy_MM_dd");
		// 读取数据库 根据时间戳和城市的时区判断具体的日期，并且加入到指定的数据库，数据库的格式为part_message_yyyy_MM_dd
		// 根据ID从小到大筛选过滤
		// int limit = 5000;
		// long start_id = 0;
		RegInfo tempReg = null;
		String date_string = "";
		// 用来记录删除的ID的列表
		ArrayList<Long> del_list = new ArrayList<Long>();
		// 用来记录内存中记录统计数据的列表
		ArrayList<String> inMemeryRecordKey = new ArrayList<String>();
		// 用来地区统计记录的键值对(date_string,(reg_ig,record))
		// HashMap<String, HashMap<Integer, StatiisticsRecord>> record = new
		// HashMap<String, HashMap<Integer, StatiisticsRecord>>();
		while (true) {
			// 存储查询结果的列表
			ArrayList<StructuredFullMessage> queryResult = statisticDB
					.getFilteredMessages(AppConfig.START_ID, AppConfig.LIMIT);
			// 循环列表，更新情感标记，根据不同时间将数据分到不同数据库
			for (int i = 0; i < queryResult.size(); i++) {
				StructuredFullMessage temp = queryResult.get(i);
				// 更新语言标记
				if (temp.getLang().equals("en")) {
					EmotionObj emo = NLPModel.getTextEmotion_en(temp.getText());
					temp.setEmotion_text(emo.getEmotion());
					temp.setEmotion_text_value(emo.getValue());
				} else {
					// other language.
				}
				ArrayList<RegInfo> cotainRegs = statisticDB
						.getRegInfoByLocation(regList,
								temp.getQuery_location_latitude(),
								temp.getQuery_location_langtitude());
				// 根据经纬度找到所在区城市并且获得区域信息
				for (int j = 0; j < cotainRegs.size(); j++) {
					tempReg = cotainRegs.get(j);
					temp.setTime_zone(tempReg.getTime_zone());
					isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"
							+ tempReg.getTime_zone()));
					Date time = new Date(temp.getCreat_at());
					date_string = isoFormat.format(time);
					String table_name = "part_message_" + date_string;
					// System.out.println(table_name);
					// 将数据插入到指定数据库，如果目标数据库不存在则创建
					if (inMemeryRecordKey.contains(date_string)) {
						if (j == 0) {
							statisticDB.insertMessage2Table(table_name, temp);
							del_list.add(temp.getNum_id());
						}

					} else {
						statisticDB.createNewSubTable(table_name);
						if (j == 0) {
							statisticDB.insertMessage2Table(table_name, temp);
							del_list.add(temp.getNum_id());
						}
						/************************/
						// record.put(date_string,
						// new HashMap<Integer, StatiisticsRecord>());
						// inMemeryRecordKey.add(date_string);
						// 如果内存中的天数超过2天，则将最早一天的统计数据存入数据库
						// if (inMemeryRecordKey.size() > 2) {
						// saveRecord2Database(record.get(inMemeryRecordKey
						// .get(0)));
						// record.remove(inMemeryRecordKey.get(0));
						// inMemeryRecordKey.remove(0);
						// }
						/********************/
					}
					/*********************/
					// 检测是否有该地区ID,如果有，则直接统计记录
					// if
					// (record.get(date_string).containsKey(tempReg.getRegID()))
					// {
					// record.get(date_string).get(tempReg.getRegID())
					// .addNewRecord(tempReg, temp);
					// } else {
					// // 添加该区域的记录
					// StatiisticsRecord firstRecord = new StatiisticsRecord(
					// date_string);
					// firstRecord.addNewRecord(tempReg, temp);
					// record.get(date_string).put(tempReg.getRegID(),
					// firstRecord);
					// }
					/****************************/
				}
				AppConfig.START_ID = temp.getNum_id();
			}
			System.out.println("last_id: " + AppConfig.START_ID);
			Tools.updateSplitStartID();
			if (queryResult.size() < AppConfig.LIMIT) {
				break;
			}
		}
		/***************************/
		// 将剩下的统计数据存入数据库(最后一天的不全)
		// for (int i = 0; i < inMemeryRecordKey.size(); i++) {
		// saveRecord2Database(record.get(inMemeryRecordKey.get(i)));
		// record.remove(inMemeryRecordKey.get(i));
		// }
		/**************************/
	}

}
