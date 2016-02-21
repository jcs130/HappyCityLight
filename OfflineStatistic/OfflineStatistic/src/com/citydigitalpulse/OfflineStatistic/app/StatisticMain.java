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
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import weka.filters.unsupervised.attribute.Center;

import com.citydigitalpulse.OfflineStatistic.dao.impl.MySQLHelper_Controller;
import com.citydigitalpulse.OfflineStatistic.dao.impl.MySQLHelper_Save;
import com.citydigitalpulse.OfflineStatistic.model.LocArea;
import com.citydigitalpulse.OfflineStatistic.model.LocPoint;
import com.citydigitalpulse.OfflineStatistic.model.RegInfo;
import com.citydigitalpulse.OfflineStatistic.model.StatiisticsRecord;
import com.citydigitalpulse.OfflineStatistic.model.StructuredFullMessage;
import com.citydigitalpulse.OfflineStatistic.tool.NLPModel;
import com.citydigitalpulse.OfflineStatistic.tool.Tools;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Zhongli Li
 *
 */
public class StatisticMain {
	private MySQLHelper_Save saveDB;
	private MySQLHelper_Controller collectorDB;
	private ObjectMapper mapper;

	public static void main(String[] args) {
		StatisticMain sm = new StatisticMain();
		sm.work();
	}

	private void init() {
		this.saveDB = new MySQLHelper_Save();
		this.collectorDB = new MySQLHelper_Controller();
		mapper = new ObjectMapper();
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void work() {
		init();
		// 获得监听地区的具体消息
		List<RegInfo> regList = getRegInfo(1);
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy_MM_dd");
		// 读取数据库 根据时间戳和城市的时区判断具体的日期，并且加入到指定的数据库，数据库的格式为msgsaving-yyyy-mm-dd
		// 根据ID从小到大筛选过滤
		int limit = 5000;
		long start_id = 0;
		RegInfo tempReg = null;
		String date_string = "";
		// 用来记录删除的ID的列表
		ArrayList<Long> del_list = new ArrayList<Long>();
		// 用来记录内存中记录统计数据的列表
		ArrayList<String> inMemeryRecordKey = new ArrayList<String>();
		// 用来地区统计记录的键值对
		HashMap<String, HashMap<Integer, StatiisticsRecord>> record = new HashMap<String, HashMap<Integer, StatiisticsRecord>>();
		while (true) {
			// 存储查询结果的列表
			ArrayList<StructuredFullMessage> queryResult = getFilteredMessages(
					start_id, limit);
			// 循环列表，更新情感标记，根据不同时间将数据分到不同数据库
			for (int i = 0; i < queryResult.size(); i++) {
				StructuredFullMessage temp = queryResult.get(i);
				// 更新语言标记
				if (temp.getLang().equals("en")) {
					temp.setEmotion_text(NLPModel.getTextEmotion(temp.getText()));
				}

				ArrayList<RegInfo> cotainRegs = getRegInfoByLocation(regList,
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
						this.insertMessage2Table(table_name, temp);
						del_list.add(temp.getNum_id());
					} else {
						this.createNewSubTable(table_name);
						this.insertMessage2Table(table_name, temp);
						del_list.add(temp.getNum_id());
						record.put(date_string,
								new HashMap<Integer, StatiisticsRecord>());
						inMemeryRecordKey.add(date_string);
						// 如果内存中的天数超过2天，则将最早一天的统计数据存入数据库
						if (inMemeryRecordKey.size() > 2) {
							saveRecord2Database(record.get(inMemeryRecordKey
									.get(0)));
							record.remove(inMemeryRecordKey.get(0));
							inMemeryRecordKey.remove(0);
						}
					}
					// 检测是否有该地区ID,如果有，则直接统计记录
					if (record.get(date_string).containsKey(tempReg.getRegID())) {
						record.get(date_string).get(tempReg.getRegID())
								.addNewRecord(tempReg, temp);
					} else {
						// 添加该区域的记录
						StatiisticsRecord firstRecord = new StatiisticsRecord(date_string);
						firstRecord.addNewRecord(tempReg, temp);
						record.get(date_string).put(tempReg.getRegID(),
								firstRecord);
					}
				}
				start_id = temp.getNum_id();
			}
			System.out.println("last_id: " + start_id);
			if (queryResult.size() < limit) {
				break;
			}
		}

	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param statiisticsRecord
	 */
	private void saveRecord2Database(
			HashMap<Integer, StatiisticsRecord> oneDayResult) {
		// 将HashMap转化为列表，按照分数排序，再更新相应的Rank值，最后存入数据库中

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
			for (int j = 0; j < regList.get(i).getAreas().size(); j++) {
				if (query_location_latitude < regList.get(i).getAreas().get(j)
						.getNorth()
						&& query_location_langtitude > regList.get(i)
								.getAreas().get(j).getSouth()
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

	public List<RegInfo> getRegInfo(int type) {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM regnames where streamstate =" + type
				+ ";";
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
				getAreasByReg(reg);
				// System.out.println(reg);
				if (reg.getCenter_lat() == 0 || reg.getCenter_lan() == 0) {
					List<LocPoint> box_points = mapper.readValue(
							reg.getBox_points(),
							new TypeReference<List<LocPoint>>() {
							});
					LocPoint center = getCenterPoint(box_points);
					reg.setCenter_lat(center.getLat());
					reg.setCenter_lan(center.getLng());
					updateRegCenter(reg.getRegID(), center);
				}
				result.add(reg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
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
		String sqlString = "UPDATE regnames SET center_lat= ? , center_lon = ? WHERE regID=?";
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
	 * @param box_points
	 * @return
	 */
	private LocPoint getCenterPoint(List<LocPoint> box_points) {
		LocPoint res = new LocPoint();
		double lan_sum = 0;
		double lat_sum = 0;
		for (int i = 0; i < box_points.size(); i++) {
			lat_sum += box_points.get(i).getLat();
			lan_sum += box_points.get(i).getLng();
		}
		res.setLat(lat_sum / (double) box_points.size());
		res.setLng(lan_sum / (double) box_points.size());
		return res;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param reg
	 */
	private void getAreasByReg(RegInfo reg) {
		// 如果列表不为空则清空列表
		if (reg.getAreas().size() != 0) {
			reg.getAreas().clear();
		} else {

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
						reg.getAreas().add(loc);
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

		}
	}

	private ArrayList<StructuredFullMessage> getFilteredMessages(long start_id,
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
	private void createNewSubTable(String table_name) {
		String sqlString = "CREATE TABLE if not exists `"
				+ table_name
				+ "` (\n  `num_id` int(11) unsigned NOT NULL,\n  `raw_id_str` varchar(45) DEFAULT NULL,\n  `user_name` varchar(100) DEFAULT NULL,\n  `creat_at` bigint(20) DEFAULT NULL,\n  `text` varchar(255) DEFAULT NULL,\n  `emotion_text` varchar(255) DEFAULT NULL,\n  `media_types` varchar(255) DEFAULT NULL,\n  `media_urls` varchar(255) DEFAULT NULL,\n  `media_urls_local` varchar(255) DEFAULT NULL,\n  `emotion_medias` varchar(255) DEFAULT NULL,\n  `emotion_all` varchar(255) DEFAULT NULL,\n  `place_type` varchar(45) DEFAULT NULL,\n  `place_name` varchar(45) DEFAULT NULL,\n  `place_fullname` varchar(100) DEFAULT NULL,\n  `country` varchar(45) DEFAULT NULL,\n  `province` varchar(45) DEFAULT NULL,\n  `city` varchar(45) DEFAULT NULL,\n  `query_location_latitude` double DEFAULT NULL,\n  `query_location_langtitude` double DEFAULT NULL,\n  `is_real_location` varchar(10) DEFAULT \'0\',\n  `hashtags` varchar(100) DEFAULT NULL,\n  `replay_to` varchar(45) DEFAULT NULL,\n  `lang` varchar(45) DEFAULT NULL,\n  `message_from` varchar(45) DEFAULT NULL,\n  `time_zone` int(11) DEFAULT \'0\',\n  PRIMARY KEY (`num_id`),\n  UNIQUE KEY `raw_id_str_UNIQUE` (`raw_id_str`),\n  KEY `time_and_location` (`creat_at`,`query_location_latitude`,`query_location_langtitude`) USING BTREE,\n  KEY `location` (`query_location_latitude`,`query_location_langtitude`) USING BTREE,\n  KEY `time` (`creat_at`) USING BTREE,\n  FULLTEXT KEY `text` (`text`)\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n";
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
	private void insertMessage2Table(String table_name,
			StructuredFullMessage msg) {
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = saveDB.getConnection();
			String sqlString = "INSERT INTO "
					+ table_name
					+ " (num_id, raw_id_str, user_name, creat_at, text,emotion_text, media_types, media_urls, media_urls_local, place_type, place_name, place_fullname, country, province, city, query_location_latitude, query_location_langtitude, hashtags, replay_to, lang, message_from,is_real_location, time_zone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			ps = conn.prepareStatement(sqlString,
					Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, msg.getNum_id());
			ps.setString(2, msg.getRaw_id_str());
			ps.setString(3, msg.getUser_name());
			ps.setLong(4, msg.getCreat_at());
			ps.setString(5, msg.getText());
			ps.setString(6, msg.getEmotion_text());
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
				}
			}
		}

	}

}
