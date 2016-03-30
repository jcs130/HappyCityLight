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
import com.citydigitalpulse.OfflineStatistic.tool.senitool.SentimentClassifier;
import com.citydigitalpulse.OfflineStatistic.tool.senitool.Translater;
import com.citydigitalpulse.OfflineStatistic.tool.senitool.ZLSentiment_en;

/**
 * @author Zhongli Li
 *
 */
public class StatisticAndUpdateThread extends Thread {
	private StatisticDaoImpl statisticDB;
	private SimpleDateFormat sdf;
	private SentimentClassifier ZLSentiment_en;
	private long date;
	private List<RegInfo> regList;
	private boolean isDone;
	private Translater translater;

	/**
	 * 
	 */
	public StatisticAndUpdateThread(StatisticDaoImpl statisticDB,
			List<RegInfo> regList, SimpleDateFormat sdf, long date,
			Translater translater) {
		super();
		this.setDone(false);
		this.sdf = sdf;
		this.regList = regList;
		this.statisticDB = statisticDB;
		this.ZLSentiment_en = new ZLSentiment_en("models/",
				"training_data_normal_before_remove_useless_part.model");
		this.date = date;
		this.translater = translater;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		RegInfo tempReg = null;
		long start_id = 0;
		String date_string = sdf.format(new Date(date));
		String table_name = "part_message_" + date_string;
		start_id = 0;
		List<StructuredFullMessage> change_list = new ArrayList<StructuredFullMessage>();
		// 用来地区统计记录的键值对(reg_ig,record)
		HashMap<Integer, StatiisticsRecord> oneDayRecord = new HashMap<Integer, StatiisticsRecord>();
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
				// 更新语言标记
				if (temp.getLang().equals("en")) {
					// EmotionObj emo =
					// sentiStrength_en.getTextSentiment(temp
					// .getText());
					EmotionObj emo = ZLSentiment_en.getTextSentiment(temp
							.getText());
					// System.out.println(temp.getText() + emo);
					// 如果结果不同，则更新数据库中结果
					if (temp.getEmotion_text() == null
							|| temp.getEmotion_text_value() != emo.getValue()
							|| !temp.getEmotion_text().equals(emo.getEmotion())) {
						temp.setEmotion_text(emo.getEmotion());
						temp.setEmotion_text_value(emo.getValue());
						// 一次更新多条记录，新建一个列表
						change_list.add(temp);
						// statisticDB.insertMessage2Table(table_name,
						// temp);
					}
				}
				// else if (temp.getLang().equals("fr")) {
				// String translated_text = Tools.getEnglish("fr",
				// temp.getText());
				// // 法语，使用GoogleTranslate
				// EmotionObj emo = ZLSentiment_en
				// .getTextSentiment(translated_text);
				// // System.out.println(temp.getText() + emo);
				// // 如果结果不同，则更新数据库中结果
				// if (temp.getEmotion_text() == null
				// || temp.getEmotion_text_value() != emo.getValue()
				// || !temp.getEmotion_text().equals(emo.getEmotion())) {
				// temp.setEmotion_text(emo.getEmotion());
				// temp.setEmotion_text_value(emo.getValue());
				// // 一次更新多条记录，新建一个列表
				// change_list.add(temp);
				// // statisticDB.insertMessage2Table(table_name,
				// // temp);
				// }
				//
				// }
				else {
					// String translated_text;
					// if (temp.getLang().equals("zh")) {
					// translated_text = translater.getEnglish("zh-cn",
					// temp.getText());
					// translated_text += translater.getEnglish("zh-tw",
					// temp.getText());
					// } else {
					// translated_text = translater.getEnglish(temp.getLang(),
					// temp.getText());
					// }
					// if (translated_text.trim().equals("")) {
					// translated_text = "nomal";
					// }
					// EmotionObj emo = ZLSentiment_en
					// .getTextSentiment(translated_text);
					// // System.out.println(temp.getText() + emo);
					// // 如果结果不同，则更新数据库中结果
					// if (temp.getEmotion_text() == null
					// || temp.getEmotion_text_value() != emo.getValue()
					// || !temp.getEmotion_text().equals(emo.getEmotion())) {
					// temp.setEmotion_text(emo.getEmotion());
					// temp.setEmotion_text_value(emo.getValue());
					// // 一次更新多条记录，新建一个列表
					// change_list.add(temp);
					// // statisticDB.insertMessage2Table(table_name,
					// // temp);
					// }
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
						if (tempReg.isPrivate()) {
							firstRecord.setPrivate(true);
						} else {
							firstRecord.setPrivate(false);
						}
						firstRecord.addNewRecord(tempReg, temp);
						oneDayRecord.put(tempReg.getRegID(), firstRecord);
					}
				}
				start_id = temp.getNum_id();
			}
			// 一次性更新更改的数据
			statisticDB.updateMutipalRecords(table_name, change_list);
			System.out.println("Changes:" + change_list.size());
			change_list.clear();
			if (queryResult.size() < AppConfig.LIMIT) {
				break;
			}
		}
		if (!oneDayRecord.isEmpty()) {
			// 存储或更新当天的数据
			statisticDB.saveRecord2Database(oneDayRecord);
		}
		System.out.println("Date: " + date_string + " Done.");
		isDone = true;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
}
