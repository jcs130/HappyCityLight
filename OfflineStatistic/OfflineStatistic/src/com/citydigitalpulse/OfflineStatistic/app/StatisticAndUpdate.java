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
import java.util.TimeZone;

import com.citydigitalpulse.OfflineStatistic.dao.impl.StatisticDaoImpl;
import com.citydigitalpulse.OfflineStatistic.model.EmotionObj;
import com.citydigitalpulse.OfflineStatistic.model.RegInfo;
import com.citydigitalpulse.OfflineStatistic.model.StatiisticsRecord;
import com.citydigitalpulse.OfflineStatistic.model.StructuredFullMessage;
import com.citydigitalpulse.OfflineStatistic.tool.NLPModel;
import com.citydigitalpulse.OfflineStatistic.tool.Tools;

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

	private void init() {
		statisticDB = new StatisticDaoImpl();
		Tools.readStatisticSettingFile();
		this.sdf = new SimpleDateFormat(AppConfig.date_format);
		// this.date_start = sdf.format(AppConfig.START_DATE);
		// this.date_end = sdf.format(AppConfig.END_DATE);
	}

	public static void main(String[] args) {
		StatisticAndUpdate so = new StatisticAndUpdate();
		// 统计一个数据库中的信息并且更新或者插入记录
		so.work();
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void work() {
		init();
		// 获得监听地区的具体消息
		List<RegInfo> regList = statisticDB.getRegInfo();
		RegInfo tempReg = null;
		long start_id = 0;
		long dayTime = 3600000 * 24;
		String table_name = "";
		String date_string = "";
		// 用来地区统计记录的键值对(reg_ig,record)
		HashMap<Integer, StatiisticsRecord> oneDayRecord = new HashMap<Integer, StatiisticsRecord>();
		// 读取指定天的数据 数据库的格式为part_message_yyyy_MM_dd
		for (long i = AppConfig.START_DATE.getTime(); i <= AppConfig.END_DATE
				.getTime(); i += dayTime) {
			date_string = sdf.format(new Date(i));
			table_name = "part_message_" + date_string;
			start_id = 0;
			oneDayRecord.clear();
			// 循环读取一天的数据库，并统计，直到读取完所有数据
			while (true) {
				// 存储查询结果的列表
				ArrayList<StructuredFullMessage> queryResult = statisticDB
						.getFilteredOneDayMessages(table_name, start_id,
								AppConfig.LIMIT);
				// 若无结果则跳出循环
				if (queryResult.size() == 0) {
					break;
				}
				for (int j = 0; j < queryResult.size(); j++) {
					StructuredFullMessage temp = queryResult.get(j);
					if (j == 0) {
						// 更新语言标记
						if (temp.getLang().equals("en")) {
							EmotionObj emo = NLPModel.getTextEmotion_en(temp
									.getText());
							// 如果结果不同，则更新数据库中结果
							if (temp.getEmotion_text_value() != emo.getValue()
									|| !temp.getEmotion_text().equals(
											emo.getEmotion())) {
								temp.setEmotion_text(emo.getEmotion());
								temp.setEmotion_text_value(emo.getValue());
								statisticDB.insertMessage2Table(table_name,
										temp);
							}
						} else {
							// other language.
						}
					}
					ArrayList<RegInfo> cotainRegs = statisticDB
							.getRegInfoByLocation(regList,
									temp.getQuery_location_latitude(),
									temp.getQuery_location_langtitude());
					// 根据经纬度找到所在区城市并且获得区域信息
					for (int k = 0; k < cotainRegs.size(); k++) {
						tempReg = cotainRegs.get(k);
						// 检测是否有该地区ID,如果有，则直接统计记录
						if (oneDayRecord.containsKey(tempReg.getRegID())) {
							oneDayRecord.get(tempReg.getRegID()).addNewRecord(
									tempReg, temp);
						} else {
							// 添加该区域的记录
							StatiisticsRecord firstRecord = new StatiisticsRecord(
									date_string);
							firstRecord.addNewRecord(tempReg, temp);
							oneDayRecord.put(tempReg.getRegID(), firstRecord);
						}
					}
					start_id = temp.getNum_id();
				}
				if (queryResult.size() < AppConfig.LIMIT) {
					break;
				}
			}
			if (!oneDayRecord.isEmpty()) {
				// 存储或更新当天的数据
				statisticDB.saveRecord2Database(oneDayRecord);
			}
			AppConfig.START_DATE = new Date(i);
			System.out.println("Date: " + sdf.format(AppConfig.START_DATE)
					+ " Done.");
			// Tools.updateStatisticStartDate();;
		}
	}
}