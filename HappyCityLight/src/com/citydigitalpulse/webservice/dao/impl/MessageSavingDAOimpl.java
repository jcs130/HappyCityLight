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
package com.citydigitalpulse.webservice.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.citydigitalpulse.webservice.dao.MessageSavingDAO;
import com.citydigitalpulse.webservice.model.collector.LocArea;
import com.citydigitalpulse.webservice.model.collector.RegInfo;
import com.citydigitalpulse.webservice.model.message.HotTopic;
import com.citydigitalpulse.webservice.model.message.ImpuseValue;
import com.citydigitalpulse.webservice.model.message.StatiisticsRecord;
import com.citydigitalpulse.webservice.model.message.StructuredFullMessage;
import com.citydigitalpulse.webservice.tool.Tools;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageSavingDAOimpl implements MessageSavingDAO {
	private MySQLHelper_Save saveDB;
	private ObjectMapper mapper;

	public MessageSavingDAOimpl() {
		this.saveDB = new MySQLHelper_Save();
		mapper = new ObjectMapper();
	}

	@Override
	public void updateTextEmotion(long num_id, String emotion) {
		PreparedStatement ps = null;
		String sqlString = "UPDATE full_message SET emotion_text=? WHERE num_id=?;";
		Connection conn = null;
		try {
			conn = saveDB.getConnection();
			ps = conn.prepareStatement(sqlString);
			ps.setString(1, emotion);
			ps.setLong(2, num_id);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			throw new RuntimeException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
				Logger.printThrowable(e);
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

	@Override
	public ArrayList<StructuredFullMessage> getFilteredMessages(
			long time_start, long time_end, String place_name,
			List<LocArea> areas, List<String> lang, List<String> message_from,
			boolean is_true_location, List<String> keywords) {
		// and media_type !='[]'
		String queryOption = buildQueryOption(time_start, time_end, place_name,
				areas, lang, message_from, is_true_location, keywords);
		String sqlString = "SELECT * FROM full_message where " + queryOption
				+ ";";
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
			Logger.printThrowable(e);
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					Logger.printThrowable(e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	private String buildQueryOption(long time_start, long time_end,
			String place_name, List<LocArea> areas, List<String> lang,
			List<String> message_from, boolean is_true_location,
			List<String> keywords) {
		String res = "";
		// 真实坐标筛选
		if (is_true_location) {
			res += "is_real_location = \"true\" and ";
		}
		// 时间范围过滤
		if (time_start != 0 && time_end != 0) {
			res += "creat_at > " + time_start + " and creat_at < " + time_end
					+ " and ";
		}
		// 地点条件
		if (areas.size() != 0) {
			if (!"".equals(place_name)) {
				res += " (place_name = \"" + place_name + "\" or ";
			} else {
				res += " ( ";
			}
			// 循环area增加条件
			for (int i = 0; i < areas.size(); i++) {
				LocArea temp = areas.get(i);
				res += "( query_location_latitude > " + temp.getSouth()
						+ " and query_location_latitude <" + temp.getNorth()
						+ " and query_location_langtitude < " + temp.getEast()
						+ " and query_location_langtitude >" + temp.getWest()
						+ ")";
				if (i != areas.size() - 1) {
					res += " or ";
				} else {
					res += " ) ";
				}
			}
		}
		// 语言条件
		if (lang.size() != 0) {
			System.out.println("lang:" + lang);
			res += "and ( ";
			for (int i = 0; i < lang.size(); i++) {
				res += " lang = \"" + lang.get(i) + "\"";
				if (i != lang.size() - 1) {
					res += " or ";
				} else {
					res += " ) ";
				}
			}
		}
		// 消息来源条件
		if (message_from.size() != 0) {
			System.out.println("message from:" + message_from);
			res += "and ( ";
			for (int i = 0; i < message_from.size(); i++) {
				res += " message_from = \"" + message_from.get(i) + "\"";
				if (i != message_from.size() - 1) {
					res += " or ";
				} else {
					res += " ) ";
				}
			}
		}
		// 是否有关键字（模糊搜索）
		if (keywords.size() != 0) {
			System.out.println("keywords:" + keywords);
			res += " and ( MATCH(text) AGAINST (\"";
			for (int i = 0; i < keywords.size(); i++) {
				res += "+" + keywords.get(i);
				if (i != keywords.size() - 1) {
					res += " ";
				} else {
					res += "\" IN NATURAL LANGUAGE MODE))";
				}
			}
		}
		return res;
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
				rec.setImpuse(mapper.readValue(rs.getString("impuse_obj"),
						ImpuseValue.class));
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

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param record_key
	 * @return
	 */
	public StatiisticsRecord getStatisticRecordByKey(String record_key) {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM statiistics_record where record_key = ? ;";
		StatiisticsRecord rec = null;
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = saveDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setString(1, record_key);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rec = new StatiisticsRecord();
				rec.setRecord_id(rs.getLong("record_id"));
				rec.setDate_timestamp_ms(rs.getLong("date_timestamp_ms"));
				rec.setLocal_date(rs.getString("local_date"));
				rec.setLanguage(rs.getString("language"));
				rec.setMessage_from(rs.getString("message_from"));
				rec.setRank(rs.getInt("rank"));
				rec.setImpuse(mapper.readValue(rs.getString("impuse_obj"),
						ImpuseValue.class));
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