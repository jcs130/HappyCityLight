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
package com.citydigitalpulse.collector.TwitterGetter.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.citydigitalpulse.collector.TwitterGetter.model.StructuredFullMessage;
import com.citydigitalpulse.collector.TwitterGetter.tool.Tools;

/**
 * @author Zhongli Li
 *
 */
public class SplitDataSavingDAO {
	private MySQLHelper_Save saveDB;

	/**
	 * 
	 */
	public SplitDataSavingDAO() {
		this.saveDB = new MySQLHelper_Save();
	}

	/**
	 * 
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param table_name
	 */
	public void createNewSubTable(String table_name) {
		String sqlString = "CREATE TABLE if not exists `"
				+ table_name
				+ "` (\n  `num_id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n  `raw_id_str` varchar(45) DEFAULT NULL,\n  `user_name` varchar(100) DEFAULT NULL,\n  `creat_at` bigint(20) DEFAULT NULL,\n  `text` MEDIUMTEXT DEFAULT NULL,\n  `emotion_text` varchar(255) DEFAULT NULL,\n  `emotion_text_value` double DEFAULT NULL,\n  `media_types` varchar(255) DEFAULT NULL,\n  `media_urls` MEDIUMTEXT DEFAULT NULL,\n  `media_urls_local` MEDIUMTEXT DEFAULT NULL,\n  `emotion_medias` varchar(255) DEFAULT NULL,\n  `emotion_all` varchar(255) DEFAULT NULL,\n  `place_type` varchar(45) DEFAULT NULL,\n  `place_name` MEDIUMTEXT DEFAULT NULL,\n  `place_fullname` MEDIUMTEXT DEFAULT NULL,\n  `country` MEDIUMTEXT DEFAULT NULL,\n  `province` MEDIUMTEXT DEFAULT NULL,\n  `city` MEDIUMTEXT DEFAULT NULL,\n  `query_location_latitude` double DEFAULT NULL,\n  `query_location_langtitude` double DEFAULT NULL,\n  `is_real_location` varchar(10) DEFAULT \'0\',\n  `hashtags` MEDIUMTEXT DEFAULT NULL,\n  `replay_to` varchar(45) DEFAULT NULL,\n  `lang` varchar(45) DEFAULT NULL,\n  `message_from` varchar(45) DEFAULT NULL,\n  `time_zone` int(11) DEFAULT \'0\',\n  PRIMARY KEY (`num_id`),\n  UNIQUE KEY `raw_id_str_UNIQUE` (`raw_id_str`),\n  KEY `time_and_location` (`creat_at`,`query_location_latitude`,`query_location_langtitude`) USING BTREE,\n  KEY `location` (`query_location_latitude`,`query_location_langtitude`) USING BTREE,\n  KEY `time` (`creat_at`) USING BTREE,\n  FULLTEXT KEY `text` (`text`)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = saveDB.getConnection();
			ps = conn.prepareStatement(sqlString);
			ps.execute();
			System.out.println("table:" + table_name + " created.");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	public void insertMessage2Table(String table_name, StructuredFullMessage msg) {
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = saveDB.getConnection();
			String sqlString = "INSERT INTO "
					+ table_name
					+ " (raw_id_str, user_name, creat_at, text,emotion_text, emotion_text_value, media_types, media_urls, media_urls_local, place_type, place_name, place_fullname, country, province, city, query_location_latitude, query_location_langtitude, hashtags, replay_to, lang, message_from,is_real_location, time_zone) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			ps = conn.prepareStatement(sqlString,
					Statement.RETURN_GENERATED_KEYS);
			// ps.setLong(1, msg.getNum_id());
			ps.setString(1, msg.getRaw_id_str());
			ps.setString(2, msg.getUser_name());
			ps.setLong(3, msg.getCreat_at());
			ps.setString(4, msg.getText());
			ps.setString(5, msg.getEmotion_text());
			ps.setDouble(6, msg.getEmotion_text_value());
			ps.setString(7, Tools.buildStringFromList(msg.getMedia_types()));
			ps.setString(8, Tools.buildStringFromList(msg.getMedia_urls()));
			ps.setString(9,
					Tools.buildStringFromList(msg.getMedia_urls_local()));
			ps.setString(10, msg.getPlace_type());
			ps.setString(11, msg.getPlace_name());
			ps.setString(12, msg.getPlace_fullname());
			ps.setString(13, msg.getCountry());
			ps.setString(14, msg.getProvince());
			ps.setString(15, msg.getCity());
			ps.setDouble(16, msg.getQuery_location_latitude());
			ps.setDouble(17, msg.getQuery_location_langtitude());
			ps.setString(18, Tools.buildStringFromList(msg.getHashtags()));
			ps.setString(19, msg.getReplay_to());
			ps.setString(20, msg.getLang());
			ps.setString(21, msg.getMessage_from());
			ps.setString(22, Boolean.toString(msg.isReal_location()));
			ps.setInt(23, msg.getTime_zone());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
				// throw new RuntimeException(e);
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

	}
}
