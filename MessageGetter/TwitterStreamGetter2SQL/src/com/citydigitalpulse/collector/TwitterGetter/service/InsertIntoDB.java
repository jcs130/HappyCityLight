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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.citydigitalpulse.collector.TwitterGetter.dao.InfoGetterDAO;
import com.citydigitalpulse.collector.TwitterGetter.dao.impl.InfoGetterDAO_MySQL;
import com.citydigitalpulse.collector.TwitterGetter.dao.impl.SplitDataSavingDAO;
import com.citydigitalpulse.collector.TwitterGetter.dao.impl.TwitterSavingDAOimpl;
import com.citydigitalpulse.collector.TwitterGetter.model.RegInfo;
import com.citydigitalpulse.collector.TwitterGetter.model.StructuredFullMessage;
import com.citydigitalpulse.collector.TwitterGetter.tool.Tools;

/**
 * @author Zhongli Li
 *
 */
public class InsertIntoDB extends Thread {
	private SimpleDateFormat isoFormat;
	private List<StructuredFullMessage> writelist;
	private InfoGetterDAO regDB;
	private SplitDataSavingDAO statisticDB;
	private List<String> inMemeryRecordKey;

	/**
	 * 
	 */
	public InsertIntoDB() {
		isoFormat = new SimpleDateFormat("yyyy_MM_dd");
		this.statisticDB = new SplitDataSavingDAO();
		this.regDB = new InfoGetterDAO_MySQL();
		this.writelist = new ArrayList<StructuredFullMessage>();
		this.inMemeryRecordKey = new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		super.run();
		System.out.println("Insert thread running....");
		List<RegInfo> regList = regDB.getRegInfo();
		// ArrayList<String> inMemeryRecordKey = new ArrayList<String>();
		String date_string = "";
		while (true) {
			System.out.println(Tools.cacheUpdateMessages.size());
			if (Tools.cacheUpdateMessages.size() > 200) {
				System.out.println("Write into database...");
				writelist.addAll(Tools.cacheUpdateMessages.values());
				Tools.cacheUpdateMessages.clear();
				for (int i = 0; i < writelist.size(); i++) {
					// 判断时区以及时间，实时的加入到分库中
					StructuredFullMessage temp = writelist.get(i);
					ArrayList<RegInfo> cotainRegs = getRegInfoByLocation(
							regList, temp.getQuery_location_latitude(),
							temp.getQuery_location_langtitude());
					// 根据经纬度找到所在区城市并且获得区域信息
					if (cotainRegs.size() > 0) {
						temp.setTime_zone(cotainRegs.get(0).getTime_zone());
						isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"
								+ cotainRegs.get(0).getTime_zone()));
						long timestamp = temp.getCreat_at();
						if (timestamp < Long.parseLong("10000000000")) {
							timestamp = timestamp * 1000;
						}
						Date time = new Date(timestamp);
						date_string = isoFormat.format(time);
						String table_name = "part_message_" + date_string;
						// System.out.println(table_name);
						// 将数据插入到指定数据库，如果目标数据库不存在则创建
						if (inMemeryRecordKey.contains(date_string)) {
							statisticDB.insertMessage2Table(table_name, temp);
						} else {
							if (inMemeryRecordKey.size() > 5000) {
								inMemeryRecordKey.clear();
							}
							statisticDB.createNewSubTable(table_name);
							inMemeryRecordKey.add(date_string);
							statisticDB.insertMessage2Table(table_name, temp);
						}

					}
				}
				writelist.clear();
			} else {
				try {
					sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			System.out.println("\n\ninsert into db success.\n\n");
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param regList
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param query_location_latitude
	 * @param query_location_langtitude
	 * @return
	 */
	private ArrayList<RegInfo> getRegInfoByLocation(List<RegInfo> regList,
			double query_location_latitude, double query_location_langtitude) {
		ArrayList<RegInfo> res = new ArrayList<RegInfo>();
		for (int i = 0; i < regList.size(); i++) {
			// System.out.println(regList.get(i).getAreas().size());
			for (int j = 0; j < regList.get(i).getAreas().size(); j++) {
				if (query_location_latitude < regList.get(i).getAreas().get(j)
						.getNorth()
						&& query_location_latitude > regList.get(i).getAreas()
								.get(j).getSouth()
						&& query_location_langtitude < regList.get(i)
								.getAreas().get(j).getEast()
						&& query_location_langtitude > regList.get(i)
								.getAreas().get(j).getWest()) {
					res.add(regList.get(i));
				}
			}
		}
		return res;
	}

}
