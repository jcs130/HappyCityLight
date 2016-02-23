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
package com.citydigitalpulse.OfflineStatistic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.citydigitalpulse.OfflineStatistic.model.LocArea;
import com.citydigitalpulse.OfflineStatistic.model.LocPoint;
import com.citydigitalpulse.OfflineStatistic.model.RegInfo;
import com.citydigitalpulse.OfflineStatistic.model.StatiisticsRecord;
import com.citydigitalpulse.OfflineStatistic.model.StructuredFullMessage;
import com.citydigitalpulse.OfflineStatistic.tool.Tools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Zhongli Li
 *
 */
public class StatisticDaoImpl {
	private MySQLHelper_Save saveDB;
	private MySQLHelper_Controller collectorDB;

	/**
	 * 
	 */
	public StatisticDaoImpl() {
		super();
		this.saveDB = new MySQLHelper_Save();
		this.collectorDB = new MySQLHelper_Controller();
	}

	/**
	 * @param regList
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param query_location_latitude
	 * @param query_location_langtitude
	 * @return
	 */
	public ArrayList<RegInfo> getRegInfoByLocation(List<RegInfo> regList,
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

	public List<RegInfo> getRegInfo() {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM regnames ;";
		ArrayList<RegInfo> result = new ArrayList<RegInfo>();
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = collectorDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			RegInfo reg = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				reg = new RegInfo(rs.getString("regname"));
				reg.setRegID(rs.getInt("regid"));
				reg.setCountry(rs.getString("country"));
				reg.setBox_points(rs.getString("box_points"));
				reg.setCenter_lat(rs.getDouble("center_lat"));
				reg.setCenter_lan(rs.getDouble("center_lan"));
				reg.setTime_zone(rs.getInt("time_zone"));
				reg.setAreas(getAreasByReg(reg));
				// System.out.println(reg);
				if (reg.getCenter_lat() == 0 || reg.getCenter_lan() == 0) {
					LocPoint center = getCenterPoint(reg.getAreas());
					reg.setCenter_lat(center.getLat());
					reg.setCenter_lan(center.getLng());
					updateRegCenter(reg.getRegID(), center);
				}
				result.add(reg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		// System.out.println(result.size());
		return result;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param regID
	 * @param center
	 */
	private void updateRegCenter(int regID, LocPoint center) {
		String sqlString = "UPDATE regnames SET center_lat= ? , center_lan = ? WHERE regID=?";
		Connection conn = null;
		try {
			conn = collectorDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setDouble(1, center.getLat());
			ps.setDouble(2, center.getLng());
			ps.setInt(3, regID);
			ps.executeUpdate();

			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * 
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param arrayList
	 * @return
	 */
	private LocPoint getCenterPoint(ArrayList<LocArea> areaList) {
		LocPoint res = new LocPoint();
		double lan_sum = 0;
		double lat_sum = 0;
		for (int i = 0; i < areaList.size(); i++) {
			LocArea area = areaList.get(i);
			lat_sum += (area.getNorth() + area.getSouth()) / 2.0;
			lan_sum += (area.getWest() + area.getEast()) / 2.0;
		}
		res.setLat(lat_sum / (double) areaList.size());
		res.setLng(lan_sum / (double) areaList.size());
		return res;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param reg
	 */
	private ArrayList<LocArea> getAreasByReg(RegInfo reg) {
		ArrayList<LocArea> areas = new ArrayList<LocArea>();
		// 先查询regandarea表得到大区域下面的小区与编号
		ArrayList<Integer> areaIDs = new ArrayList<Integer>();
		String sqlString = "SELECT * FROM regandarea where regid="
				+ reg.getRegID() + ";";
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = collectorDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				areaIDs.add(rs.getInt("areaid"));
			}
			// 根据编号添加具体的对象
			for (int i = 0; i < areaIDs.size(); i++) {
				sqlString = "SELECT * FROM interestareas where areaid="
						+ areaIDs.get(i) + ";";
				ps = conn.prepareStatement(sqlString);
				rs = ps.executeQuery();
				while (rs.next()) {
					LocArea loc = new LocArea(areaIDs.get(i),
							rs.getDouble("north"), rs.getDouble("west"),
							rs.getDouble("south"), rs.getDouble("east"));
					// loc.setCenterAndRange(
					// new LocPoint(rs.getDouble("center_lat"), rs
					// .getDouble("center_lon")), rs
					// .getInt("range"));
					areas.add(loc);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return areas;

	}

	public ArrayList<StructuredFullMessage> getFilteredMessages(long start_id,
			int limit) {
		String sqlString = "SELECT * FROM full_message where num_id > "
				+ start_id + " LIMIT " + limit + ";";
		System.out.println(sqlString);
		Connection conn = null;
		try {
			conn = saveDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ArrayList<StructuredFullMessage> res = new ArrayList<StructuredFullMessage>();
			StructuredFullMessage msg;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				msg = new StructuredFullMessage();
				msg.setNum_id(rs.getLong("num_id"));
				msg.setRaw_id_str(rs.getString("raw_id_str"));
				msg.setUser_name(rs.getString("user_name"));
				msg.setText(rs.getString("text"));
				msg.setCreat_at(rs.getLong("creat_at"));
				msg.setEmotion_text(rs.getString("emotion_text"));
				msg.setMedia_types(Tools.buildListFromString(rs
						.getString("media_types")));
				msg.setMedia_urls(Tools.buildListFromString(rs
						.getString("media_urls")));
				msg.setMedia_urls_local(Tools.buildListFromString(rs
						.getString("media_urls_local")));
				msg.setEmotion_medias(Tools.buildListFromString(rs
						.getString("emotion_medias")));
				msg.setEmotion_all(rs.getString("emotion_all"));
				msg.setPlace_type(rs.getString("place_type"));
				msg.setPlace_name(rs.getString("place_name"));
				msg.setPlace_fullname(rs.getString("place_fullname"));
				msg.setCountry(rs.getString("country"));
				msg.setProvince(rs.getString("province"));
				msg.setCity(rs.getString("city"));
				msg.setQuery_location_latitude(rs
						.getDouble("query_location_latitude"));
				msg.setQuery_location_langtitude(rs
						.getDouble("query_location_langtitude"));
				msg.setReal_location(rs.getBoolean("is_real_location"));
				msg.setHashtags(Tools.buildListFromString(rs
						.getString("hashtags")));
				msg.setReplay_to(rs.getString("replay_to"));
				msg.setLang(rs.getString("lang"));
				msg.setMessage_from(rs.getString("message_from"));
				res.add(msg);
			}
			// System.out.println("Get new Datas");
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * 
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param table_name
	 */
	public void createNewSubTable(String table_name) {
		String sqlString = "CREATE TABLE if not exists `"
				+ table_name
				+ "` (\n  `num_id` int(11) unsigned NOT NULL,\n  `raw_id_str` varchar(45) DEFAULT NULL,\n  `user_name` varchar(100) DEFAULT NULL,\n  `creat_at` bigint(20) DEFAULT NULL,\n  `text` varchar(255) DEFAULT NULL,\n  `emotion_text` varchar(255) DEFAULT NULL,\n  `emotion_text_value` double DEFAULT NULL,\n  `media_types` varchar(255) DEFAULT NULL,\n  `media_urls` varchar(255) DEFAULT NULL,\n  `media_urls_local` varchar(255) DEFAULT NULL,\n  `emotion_medias` varchar(255) DEFAULT NULL,\n  `emotion_all` varchar(255) DEFAULT NULL,\n  `place_type` varchar(45) DEFAULT NULL,\n  `place_name` varchar(45) DEFAULT NULL,\n  `place_fullname` varchar(100) DEFAULT NULL,\n  `country` varchar(45) DEFAULT NULL,\n  `province` varchar(45) DEFAULT NULL,\n  `city` varchar(45) DEFAULT NULL,\n  `query_location_latitude` double DEFAULT NULL,\n  `query_location_langtitude` double DEFAULT NULL,\n  `is_real_location` varchar(10) DEFAULT \'0\',\n  `hashtags` varchar(100) DEFAULT NULL,\n  `replay_to` varchar(45) DEFAULT NULL,\n  `lang` varchar(45) DEFAULT NULL,\n  `message_from` varchar(45) DEFAULT NULL,\n  `time_zone` int(11) DEFAULT \'0\',\n  PRIMARY KEY (`num_id`),\n  UNIQUE KEY `raw_id_str_UNIQUE` (`raw_id_str`),\n  KEY `time_and_location` (`creat_at`,`query_location_latitude`,`query_location_langtitude`) USING BTREE,\n  KEY `location` (`query_location_latitude`,`query_location_langtitude`) USING BTREE,\n  KEY `time` (`creat_at`) USING BTREE,\n  FULLTEXT KEY `text` (`text`)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = saveDB.getConnection();
			ps = conn.prepareStatement(sqlString);
			ps.execute();
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
					+ " (num_id, raw_id_str, user_name, creat_at, text,emotion_text, emotion_text_value, media_types, media_urls, media_urls_local, place_type, place_name, place_fullname, country, province, city, query_location_latitude, query_location_langtitude, hashtags, replay_to, lang, message_from,is_real_location, time_zone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE emotion_text=?, emotion_text_value=?;";
			ps = conn.prepareStatement(sqlString,
					Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, msg.getNum_id());
			ps.setString(2, msg.getRaw_id_str());
			ps.setString(3, msg.getUser_name());
			ps.setLong(4, msg.getCreat_at());
			ps.setString(5, msg.getText());
			ps.setString(6, msg.getEmotion_text());
			ps.setDouble(7, msg.getEmotion_text_value());
			ps.setString(8, Tools.buildStringFromList(msg.getMedia_types()));
			ps.setString(9, Tools.buildStringFromList(msg.getMedia_urls()));
			ps.setString(10,
					Tools.buildStringFromList(msg.getMedia_urls_local()));
			ps.setString(11, msg.getPlace_type());
			ps.setString(12, msg.getPlace_name());
			ps.setString(13, msg.getPlace_fullname());
			ps.setString(14, msg.getCountry());
			ps.setString(15, msg.getProvince());
			ps.setString(16, msg.getCity());
			ps.setDouble(17, msg.getQuery_location_latitude());
			ps.setDouble(18, msg.getQuery_location_langtitude());
			ps.setString(19, Tools.buildStringFromList(msg.getHashtags()));
			ps.setString(20, msg.getReplay_to());
			ps.setString(21, msg.getLang());
			ps.setString(22, msg.getMessage_from());
			ps.setString(23, Boolean.toString(msg.isReal_location()));
			ps.setInt(24, msg.getTime_zone());
			ps.setString(25, msg.getEmotion_text());
			ps.setDouble(26, msg.getEmotion_text_value());
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

	/**
	 * 从指定的数据库中读取指定条数的数据
	 * 
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param table_name
	 * @param start_id
	 * @param lIMIT
	 * @return
	 */
	public ArrayList<StructuredFullMessage> getFilteredOneDayMessages(
			String table_name, long start_id, int limit) {
		String sqlString = "SELECT * FROM " + table_name + " where num_id > "
				+ start_id + " LIMIT " + limit + ";";
		System.out.println(sqlString);
		ArrayList<StructuredFullMessage> res = new ArrayList<StructuredFullMessage>();
		Connection conn = null;
		try {
			conn = saveDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			StructuredFullMessage msg;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				msg = new StructuredFullMessage();
				msg.setNum_id(rs.getLong("num_id"));
				msg.setRaw_id_str(rs.getString("raw_id_str"));
				msg.setUser_name(rs.getString("user_name"));
				msg.setText(rs.getString("text"));
				msg.setCreat_at(rs.getLong("creat_at"));
				msg.setEmotion_text(rs.getString("emotion_text"));
				msg.setEmotion_text_value(rs.getDouble("emotion_text_value"));
				msg.setMedia_types(Tools.buildListFromString(rs
						.getString("media_types")));
				msg.setMedia_urls(Tools.buildListFromString(rs
						.getString("media_urls")));
				msg.setMedia_urls_local(Tools.buildListFromString(rs
						.getString("media_urls_local")));
				msg.setEmotion_medias(Tools.buildListFromString(rs
						.getString("emotion_medias")));
				msg.setEmotion_all(rs.getString("emotion_all"));
				msg.setPlace_type(rs.getString("place_type"));
				msg.setPlace_name(rs.getString("place_name"));
				msg.setPlace_fullname(rs.getString("place_fullname"));
				msg.setCountry(rs.getString("country"));
				msg.setProvince(rs.getString("province"));
				msg.setCity(rs.getString("city"));
				msg.setQuery_location_latitude(rs
						.getDouble("query_location_latitude"));
				msg.setQuery_location_langtitude(rs
						.getDouble("query_location_langtitude"));
				msg.setReal_location(rs.getBoolean("is_real_location"));
				msg.setHashtags(Tools.buildListFromString(rs
						.getString("hashtags")));
				msg.setReplay_to(rs.getString("replay_to"));
				msg.setLang(rs.getString("lang"));
				msg.setMessage_from(rs.getString("message_from"));
				res.add(msg);
			}
			// System.out.println("Get new Datas");
			return res;
		} catch (SQLException e) {
			if (e.getErrorCode() == 1146) {
				System.out.println("Table:" + table_name + " is not exist.");
				return res;
			} else {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param statiisticsRecord
	 */
	public void saveRecord2Database(
			HashMap<Integer, StatiisticsRecord> oneDayResult) {
		// 将HashMap转化为列表，按照分数排序，再更新相应的Rank值，最后存入数据库中
		StatiisticsRecord[] resultArray = oneDayResult.values().toArray(
				new StatiisticsRecord[0]);
		Arrays.sort(resultArray);
		insertMutipalRecord(resultArray);
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param resultArray
	 */
	private void insertMutipalRecord(StatiisticsRecord[] resultArray) {
		Connection conn = null;
		Statement statement = null;
		StatiisticsRecord record;
		ObjectMapper mapper = new ObjectMapper();
		try {
			conn = saveDB.getConnection();
			// 指定在事物中提交
			conn.setAutoCommit(false);
			statement = conn.createStatement();
			// 循环添加新消息
			for (int i = 0; i < resultArray.length; i++) {
				record = resultArray[i];
				record.sortHotTopics();
				record.setRank(i + 1);
				String sqlString = "INSERT INTO statiistics_record (record_key, date_timestamp_ms, local_date, place_id, place_name, place_obj, pulse_value, pulse_obj, rank, hot_topics, message_from, language) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE pulse_value=?,pulse_obj=?,rank=?,hot_topics=?;";
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ps.setString(
						1,
						record.getRegInfo().getCountry().toLowerCase()
								+ ","
								+ record.getRegInfo().getRegName()
										.toLowerCase() + ","
								+ record.getLocal_date() + ","
								+ record.getLanguage() + ","
								+ record.getMessage_from());
				ps.setLong(2, record.getDate_timestamp_ms());
				ps.setString(3, record.getLocal_date());
				ps.setLong(4, record.getRegInfo().getRegID());
				ps.setString(5, record.getRegInfo().getCountry().toLowerCase()
						+ "," + record.getRegInfo().getRegName().toLowerCase());
				ps.setString(6, mapper.writeValueAsString(record.getRegInfo()));
				ps.setDouble(7, record.getPulse().getPulse_value());
				ps.setString(8, mapper.writeValueAsString(record.getPulse()));
				ps.setInt(9, record.getRank());
				ps.setString(10,
						mapper.writeValueAsString(record.getHot_topics()));
				ps.setString(11, record.getMessage_from());
				ps.setString(12, record.getLanguage());
				ps.setDouble(13, record.getPulse().getPulse_value());
				ps.setString(14, mapper.writeValueAsString(record.getPulse()));
				ps.setInt(15, record.getRank());
				ps.setString(16,
						mapper.writeValueAsString(record.getHot_topics()));
				ps.executeUpdate();
			}
			// 提交更改
			conn.commit();
		} catch (SQLException | JsonProcessingException e) {
			e.printStackTrace();
			// // 有错误发生回滚修改
			// try {
			// conn.rollback();
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// throw new RuntimeException(e1);
			// }
			// throw new RuntimeException(e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

}
