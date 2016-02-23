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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.citydigitalpulse.OfflineStatistic.dao.impl.MySQLHelper_Save;
import com.citydigitalpulse.OfflineStatistic.model.HotTopic;
import com.citydigitalpulse.OfflineStatistic.model.PulseValue;
import com.citydigitalpulse.OfflineStatistic.model.RegInfo;
import com.citydigitalpulse.OfflineStatistic.model.StatiisticsRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Zhongli Li
 *
 */
public class dbTest {
	private MySQLHelper_Save saveDB;
	private ObjectMapper mapper;

	public static void main(String[] args) {
		dbTest dt = new dbTest();
		dt.init();
		System.out.println(dt.getStatisticRecordByID(1172));
	}

	private void init() {
		this.saveDB = new MySQLHelper_Save();
		mapper = new ObjectMapper();
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param record_id
	 * @return
	 */
	public StatiisticsRecord getStatisticRecordByID(long record_id) {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM statiistics_record where record_id ="
				+ record_id + ";";
		StatiisticsRecord rec = null;
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = saveDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rec = new StatiisticsRecord();
				rec.setRecord_id(record_id);
				rec.setDate_timestamp_ms(rs.getLong("date_timestamp_ms"));
				rec.setLocal_date(rs.getString("local_date"));
				rec.setLanguage(rs.getString("language"));
				rec.setMessage_from(rs.getString("message_from"));
				rec.setRank(rs.getInt("rank"));
				rec.setPulse(mapper.readValue(rs.getString("pulse_obj"),
						PulseValue.class));
				rec.setRegInfo(mapper.readValue(rs.getString("place_obj"),
						RegInfo.class));
				rec.setHot_topics((HotTopic[]) mapper.readValue(
						rs.getString("hot_topics"),
						new TypeReference<HotTopic[]>() {
						}));
				return rec;
			}
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return rec;
	}
}
