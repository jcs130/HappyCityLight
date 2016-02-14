package com.citydigitalpulse.webservice.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.citydigitalpulse.webservice.dao.MessageSavingDAO;
import com.citydigitalpulse.webservice.model.collector.LocArea;
import com.citydigitalpulse.webservice.model.message.StructuredFullMessage;
import com.citydigitalpulse.webservice.tool.Tools;

public class MessageSavingDAOimpl implements MessageSavingDAO {
	private MySQLHelper_Save saveDB;

	public MessageSavingDAOimpl() {
		this.saveDB = new MySQLHelper_Save();
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
			res += " ( ";
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
			res += " ( ";
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

}
