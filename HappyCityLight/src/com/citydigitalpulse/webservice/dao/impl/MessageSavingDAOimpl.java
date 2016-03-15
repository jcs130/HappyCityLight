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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.citydigitalpulse.webservice.dao.MessageSavingDAO;
import com.citydigitalpulse.webservice.model.collector.LocArea;
import com.citydigitalpulse.webservice.model.collector.RegInfo;
import com.citydigitalpulse.webservice.model.message.HotTopic;
import com.citydigitalpulse.webservice.model.message.PulseValue;
import com.citydigitalpulse.webservice.model.message.RecordKey;
import com.citydigitalpulse.webservice.model.message.RegStatisticInfo;
import com.citydigitalpulse.webservice.model.message.StatiisticsRecord;
import com.citydigitalpulse.webservice.model.message.StructuredFullMessage;
import com.citydigitalpulse.webservice.tool.Tools;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageSavingDAOimpl implements MessageSavingDAO {
	private static final int CACHE_SIZE = 1000;
	private MySQLHelper_Save saveDB;
	private ObjectMapper mapper;
	private HashMap<RecordKey, StatiisticsRecord> statistic_history;
	private ArrayList<RecordKey> cached_key_list;
	private DateFormat key_sdf;

	public MessageSavingDAOimpl() {
		this.saveDB = new MySQLHelper_Save();
		this.mapper = new ObjectMapper();
		this.statistic_history = new HashMap<RecordKey, StatiisticsRecord>();
		this.cached_key_list = new ArrayList<RecordKey>();
		this.key_sdf = new SimpleDateFormat("yyyy_MM_dd");
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
		long dayTime = 3600000 * 24;
		String table_name = "";
		String date_string = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		ArrayList<StructuredFullMessage> res = new ArrayList<StructuredFullMessage>();
		for (long i = time_start; i <= time_end; i += dayTime) {
			date_string = sdf.format(new Date(i));
			table_name = "part_message_" + date_string;
			// and media_type !='[]'
			String queryOption = buildQueryOption(time_start, time_end,
					place_name, areas, lang, message_from, is_true_location,
					keywords);
			String sqlString = "SELECT * FROM " + table_name + " where "
					+ queryOption + ";";
			System.out.println(sqlString);
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
					msg.setEmotion_text_value(rs
							.getDouble("emotion_text_value"));
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
			} catch (SQLException e) {
				if (e.getErrorCode() == 1146) {
					System.out
							.println("Table:" + table_name + " is not exist.");
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
						Logger.printThrowable(e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return res;
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
		StatiisticsRecord rec = null;
		RecordKey key = new RecordKey(record_id);
		// 如果缓存中有数据，则直接读取缓存
		if (statistic_history.containsKey(key)) {
			rec = statistic_history.get(key);
		} else {
			// 根据type获取大区域的信息
			String sqlString = "SELECT * FROM statiistics_record where record_id ="
					+ record_id + ";";

			// 查询数据库，获取结果
			Connection conn = null;
			try {
				conn = saveDB.getConnection();
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					rec = new StatiisticsRecord();
					rec.setRecord_id(record_id);
					rec.setRecord_key(rs.getString("record_key"));
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
					key.setRecord_id(rec.getRecord_id());
					key.setRecord_key(rec.getRecord_key());

					if (statistic_history.containsKey(key)) {
						statistic_history.put(key, rec);
					} else {
						statistic_history.put(key, rec);
						if (cached_key_list.size() > CACHE_SIZE) {
							cached_key_list.remove(0);
						}
						cached_key_list.add(key);
					}
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
		}
		return rec;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param record_key
	 * @return
	 */
	public StatiisticsRecord getStatisticRecordByKey(String record_key) {
		StatiisticsRecord rec = null;
		RecordKey key = new RecordKey(record_key);
		System.out.println(record_key);
		// 如果缓存中有数据，则直接读取缓存
		if (statistic_history.containsKey(key)) {
			rec = statistic_history.get(key);
		} else {
			// 根据type获取大区域的信息
			String sqlString = "SELECT * FROM statiistics_record where record_key = ? ;";
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
					rec.setRecord_key(rs.getString("record_key"));
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
					key.setRecord_id(rec.getRecord_id());
					key.setRecord_key(rec.getRecord_key());

					if (statistic_history.containsKey(key)) {
						statistic_history.put(key, rec);
					} else {
						statistic_history.put(key, rec);
						if (cached_key_list.size() > CACHE_SIZE) {
							cached_key_list.remove(0);
						}
						cached_key_list.add(key);
					}
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
		}
		return rec;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param date_str
	 * @return
	 */
	public ArrayList<RegStatisticInfo> getStatisticRecordsByDate(String date_str) {
		String sqlString = "SELECT * FROM statiistics_record where local_date = ? ;";
		ArrayList<RegStatisticInfo> res = new ArrayList<RegStatisticInfo>();
		Connection conn = null;
		StatiisticsRecord rec;
		RecordKey key;
		try {
			conn = saveDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setString(1, date_str);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rec = new StatiisticsRecord();
				rec.setRecord_id(rs.getLong("record_id"));
				rec.setRecord_key(rs.getString("record_key"));
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

				key = new RecordKey(rec.getRecord_id());
				key.setRecord_key(rec.getRecord_key());

				if (statistic_history.containsKey(key)) {
					statistic_history.put(key, rec);
				} else {
					statistic_history.put(key, rec);
					if (cached_key_list.size() > CACHE_SIZE) {
						cached_key_list.remove(0);
					}
					cached_key_list.add(key);
				}

				res.add(new RegStatisticInfo(rec));
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
		return res;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param place_id
	 * @param date_start
	 * @param date_end
	 * @return
	 */
	public ArrayList<RegStatisticInfo> getPlaceHistoryInfos(int place_id,
			String date_start, String date_end) {
		// 解析开始日期和结束日期，如果缓存中存在该日期则不用查询数据库，如果没有改日期则直接使用==查询
		String sqlString = "SELECT * FROM statiistics_record where place_id = ? and (local_date between ? and ? ) order by local_date ASC;";
		ArrayList<RegStatisticInfo> res = new ArrayList<RegStatisticInfo>();
		Connection conn = null;
		StatiisticsRecord rec;
		try {
			conn = saveDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setInt(1, place_id);
			ps.setString(2, date_start);
			ps.setString(3, date_end);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rec = new StatiisticsRecord();
				rec.setRecord_id(rs.getLong("record_id"));
				rec.setRecord_key(rs.getString("record_key"));
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

				RecordKey key = new RecordKey(rec.getRecord_id());
				key.setRecord_key(rec.getRecord_key());
				if (statistic_history.containsKey(key)) {
					statistic_history.put(key, rec);
				} else {
					statistic_history.put(key, rec);
					if (cached_key_list.size() > CACHE_SIZE) {
						cached_key_list.remove(0);
					}
					cached_key_list.add(key);
				}
				res.add(new RegStatisticInfo(rec));
			}
			return res;
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
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param place_id
	 * @param start
	 * @param end
	 * @return
	 */
	public ArrayList<RegStatisticInfo> getPlaceHistoryInfos_fast(int place_id,
			Date start, Date end) {
		ArrayList<RegStatisticInfo> res = new ArrayList<RegStatisticInfo>();
		long dayTime = 3600000 * 24;
		long start_time = Math.min(start.getTime(), end.getTime());
		long end_time = Math.max(start.getTime(), end.getTime());
		// 通过日期算出具体要查那天的，再通过日期得到record_key
		String record_key;
		// 解析开始日期和结束日期，如果缓存中存在该日期则不用查询数据库，如果没有改日期则直接使用==查询
		for (long i = start_time; i <= end_time; i += dayTime) {
			record_key = "reg_" + place_id + "," + key_sdf.format(new Date(i))
					+ ",all,all";
			StatiisticsRecord rec = getStatisticRecordByKey(record_key);
			if (rec != null) {
				res.add(new RegStatisticInfo(rec));
			}
		}
		return res;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param place_id
	 * @param start
	 * @param end
	 * @return
	 */
	public ArrayList<StatiisticsRecord> getPlaceHistoryRecord_fast(
			int place_id, Date start, Date end) {
		HotTopic tempTopic;
		ArrayList<StatiisticsRecord> res = new ArrayList<StatiisticsRecord>();
		long dayTime = 3600000 * 24;
		long start_time = Math.min(start.getTime(), end.getTime());
		long end_time = Math.max(start.getTime(), end.getTime());
		// 通过日期算出具体要查那天的，再通过日期得到record_key
		String record_key;
		// 解析开始日期和结束日期，如果缓存中存在该日期则不用查询数据库，如果没有改日期则直接使用==查询
		for (long i = start_time; i <= end_time; i += dayTime) {
			record_key = "reg_" + place_id + "," + key_sdf.format(new Date(i))
					+ ",all,all";
			StatiisticsRecord rec = getStatisticRecordByKey(record_key);
			if (rec != null) {
				// 限制热门话题的数量和话题中的图片
				ArrayList<HotTopic> topics = new ArrayList<HotTopic>();

				for (int j = 0; j < rec.getHot_topics().length; j++) {
					tempTopic = rec.getHot_topics()[j];
					if (tempTopic.getImages().size() > 50) {
						tempTopic.setImages(tempTopic.getImages()
								.subList(0, 49));
					}
					topics.add(tempTopic);
					if (j > 50) {
						break;
					}
				}
				rec.setHot_topics(topics.toArray(new HotTopic[0]));
				res.add(rec);
			}
		}
		return res;
	}

}
