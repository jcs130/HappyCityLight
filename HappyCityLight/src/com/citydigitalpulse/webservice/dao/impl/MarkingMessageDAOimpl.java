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
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.Redirect;
import org.glassfish.hk2.utilities.reflection.Logger;

import redis.clients.jedis.Jedis;

import com.citydigitalpulse.webservice.dao.MarkingMessageDAO;
import com.citydigitalpulse.webservice.model.collector.LocArea;
import com.citydigitalpulse.webservice.model.message.MarkMessageObj;
import com.citydigitalpulse.webservice.model.message.MarkMsg2Web;
import com.citydigitalpulse.webservice.model.message.MarkRecordObj;
import com.citydigitalpulse.webservice.model.message.MediaObject;
import com.citydigitalpulse.webservice.tool.Tools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeParser;

public class MarkingMessageDAOimpl implements MarkingMessageDAO {
	private MySQLHelper_Mark markDB;
	// private SimpleDateFormat sdf;
	// 缓存区
	private static String CACHE_KEY = "UnmarkedMessageTemp";
	// private static ArrayList<MarkMessageObj> cacheMessage = new
	// ArrayList<MarkMessageObj>();
	private int cacheSize;
	private int markMaxTime;

	public MarkingMessageDAOimpl(int cacheSize, int markMaxTime) {
		markDB = new MySQLHelper_Mark();
		this.cacheSize = cacheSize;
		this.markMaxTime = markMaxTime;

		// sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
		// sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public ArrayList<MarkMessageObj> getNewMarkingMsg(int limit,
			String queryOption) {
		// and media_type !='[]'
		String sqlString = "SELECT * FROM mark_messages where " + queryOption
				+ " ORDER BY rand() LIMIT " + limit + ";";
		System.out.println(sqlString);
		Connection conn = null;

		try {
			conn = markDB.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ArrayList<MarkMessageObj> res = new ArrayList<MarkMessageObj>();
			MarkMessageObj message;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				message = new MarkMessageObj();
				message.setMsg_id(rs.getLong("msg_id"));
				message.setFull_msg_id(rs.getLong("full_msg_id"));
				message.setText(rs.getString("text"));
				message.setMedia_types(Tools.buildListFromString(rs
						.getString("media_types").replaceAll("[", "")
						.replaceAll("]", "")));
				message.setMedia_urls(Tools.buildListFromString(rs
						.getString("media_urls").replaceAll("[", "")
						.replaceAll("]", "")));
				message.setMedia_urls_local(Tools.buildListFromString(rs
						.getString("media_urls_local").replaceAll("[", "")
						.replaceAll("]", "")));
				message.setLang(rs.getString("lang"));
				System.out.println(message);
				res.add(message);
			}
			System.out.println("Get new Datas");
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

	/**
	 * 检测消息是否还存在
	 * 
	 * @param testURL
	 * @return
	 */
	private boolean isURLAvailable(URL testURL) {
		InputStream in;
		try {
			in = testURL.openStream();
			System.out.println(testURL + " 可以打开");
			in.close();
		} catch (Exception e1) {
			System.out.println(testURL + " 连接打不开!");
			return false;
		}

		return true;
	}

	// /**
	// * 将ArrayList的toString()之后生成的字符串转化为ArrayList
	// *
	// * @param string
	// * @return
	// */
	// private List<String> getListFromString(String listString) {
	// // 去掉首尾的中括号
	// String[] temp = listString.substring(1, listString.length() - 1).split(
	// ", ");
	// ArrayList<String> res = new ArrayList<String>();
	// for (int i = 0; i < temp.length; i++) {
	// res.add(temp[i]);
	// }
	//
	// return res;
	// }

	@Override
	public MarkMsg2Web getOneNewMsg() throws IOException {
		String queryOption = "mark_times <" + markMaxTime;
		// 读取缓存中的缓存数据
		Jedis cacheDB = RedisUtil.getJedis();
		String jsonObj = "";
		MarkMessageObj markobj = null;
		ObjectMapper mapper = new ObjectMapper();
		if (cacheDB.exists(CACHE_KEY)) {
			jsonObj = cacheDB.lpop(CACHE_KEY);
			if ("nil".equals(jsonObj)) {
				ArrayList<MarkMessageObj> cacheMessage = getNewMarkingMsg(
						cacheSize, queryOption);
				// 将列表存入缓存
				for (int i = 0; i < cacheMessage.size(); i++) {
					if (i == 0) {
						markobj = cacheMessage.get(i);
					} else {
						cacheDB.rpush(CACHE_KEY,
								mapper.writeValueAsString(cacheMessage.get(i)));
					}
				}
			} else {
				// 将json转换为区域数组
				markobj = mapper.readValue(jsonObj, MarkMessageObj.class);
			}
		} else {
			ArrayList<MarkMessageObj> cacheMessage = getNewMarkingMsg(
					cacheSize, queryOption);
			// 将列表存入缓存
			for (int i = 0; i < cacheMessage.size(); i++) {
				if (i == 0) {
					markobj = cacheMessage.get(i);
				} else {
					cacheDB.rpush(CACHE_KEY,
							mapper.writeValueAsString(cacheMessage.get(i)));
				}
			}
		}
		MarkMsg2Web res = new MarkMsg2Web(markobj);
		RedisUtil.returnResource(cacheDB);
		return res;
	}

	@Override
	public void recordForMessage(long user_id, long message_id,
			String text_emotion, ArrayList<String> media_emotion) {
		MarkMessageObj message = new MarkMessageObj();
		message = getMessageInfo(message_id);
		// insert into mark_records
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = markDB.getConnection();

			String sqlString = "INSERT INTO mark_records "
					+ "(msg_id, user_id, mark_at, text, emotion_text, media_types, media_urls, media_urls_local, emotion_medias) VALUES"
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?);";
			ps = conn.prepareStatement(sqlString);
			ps.setLong(1, message_id);
			ps.setLong(2, user_id);
			ps.setLong(3, new Date().getTime());
			ps.setString(4, message.getText());
			ps.setString(5, text_emotion);

			ps.setString(6, Tools.buildStringFromList(message.getMedia_types()));
			ps.setString(7, Tools.buildStringFromList(message.getMedia_urls()));
			ps.setString(8,
					Tools.buildStringFromList(message.getMedia_urls_local()));
			ps.setString(9, Tools.buildStringFromList(media_emotion));

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
				}
			}
		}

		// update mark_messages
		updateMarkMessage(message_id);
		// update mark_detail, message_id, updatetime
		updateUserMarkDetail(message_id, user_id);
	}

	@Override
	public void insertNewMessage(long msg_id, long full_msg_id, String text,
			String media_types, String media_urls, String media_urls_local,
			int mark_times, String lang, String message_from) {
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = markDB.getConnection();

			String sqlString = "INSERT INTO mark_messages (msg_id, full_msg_id, text, media_types, media_urls, media_urls_local, mark_times, lang, message_from) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
			ps = conn.prepareStatement(sqlString);
			ps.setLong(1, msg_id);
			ps.setLong(2, full_msg_id);
			ps.setString(3, text);
			ps.setString(4, media_types);
			ps.setString(5, media_urls);
			ps.setString(6, media_urls_local);
			ps.setInt(7, mark_times);
			ps.setString(8, lang);
			ps.setString(9, message_from);
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
				}
			}
		}
	}

	@Override
	public void updateMarkMessage(long message_id) {
		PreparedStatement ps = null;
		String sqlString = "UPDATE mark_messages SET mark_times = mark_times + 1 WHERE msg_id = ?;";
		Connection conn = null;
		try {
			conn = markDB.getConnection();
			ps = conn.prepareStatement(sqlString);
			ps.setLong(1, message_id);
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
	public void createUserMarkDetail(long user_id) {
		PreparedStatement ps = null;
		String sqlString = "INSERT mark_detail (user_id, updated_on) VALUES (?, ?);";
		Connection conn = null;
		try {
			conn = markDB.getConnection();
			ps = conn.prepareStatement(sqlString);
			ps.setLong(1, user_id);
			ps.setLong(2, new Date().getTime());
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
				}
			}
		}
	}

	@Override
	public void updateUserMarkDetail(long message_id, long user_id) {
		PreparedStatement ps = null;
		String sqlString = "UPDATE mark_detail SET last_recorded_message_id = ?,count=count+1 , updated_on = ?"
				+ " WHERE user_id = ?;";
		Connection conn = null;
		try {
			conn = markDB.getConnection();
			ps = conn.prepareStatement(sqlString);
			ps.setLong(1, message_id);
			ps.setLong(2, new Date().getTime());
			ps.setLong(3, user_id);
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
					e.printStackTrace();
					Logger.printThrowable(e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public MarkMessageObj getMessageInfo(long message_id) {
		MarkMessageObj message = new MarkMessageObj();
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = markDB.getConnection();

			String sqlString = "SELECT * FROM mark_messages WHERE msg_id = ?;";
			ps = conn.prepareStatement(sqlString);

			ps.setLong(1, message_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ArrayList<String> types = new ArrayList<String>(
						Arrays.asList(rs.getString("media_types")));
				ArrayList<String> urls = new ArrayList<String>(Arrays.asList(rs
						.getString("media_urls")));
				ArrayList<String> urls_local = new ArrayList<String>(
						Arrays.asList(rs.getString("media_urls_local")));
				ArrayList<MediaObject> medias = new ArrayList<MediaObject>();
				for (int i = 0; i < types.size(); i++) {
					medias.add(new MediaObject(types.get(i), urls.get(i),
							urls_local.get(i)));
				}
				message.setMsg_id(rs.getLong("msg_id"));
				message.setText(rs.getString("text"));
				message.setMedia_urls_local(Tools.buildListFromString(rs
						.getString("media_urls_local").replaceAll("[", "")
						.replaceAll("]", "")));
				message.setMedia_types(Tools.buildListFromString(rs
						.getString("media_types").replaceAll("[", "")
						.replaceAll("]", "")));
				message.setMedia_urls(Tools.buildListFromString(rs
						.getString("media_urls").replaceAll("[", "")
						.replaceAll("]", "")));
			}

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
					e.printStackTrace();
					Logger.printThrowable(e);
					throw new RuntimeException(e);
				}
			}
		}
		return message;
	}

	@Override
	public ArrayList<MarkRecordObj> getRecentRecords(int count, long user_id) {
		MarkRecordObj record;
		ArrayList<MarkRecordObj> records = new ArrayList<MarkRecordObj>();
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = markDB.getConnection();
			String sqlString = "SELECT * FROM mark_records WHERE user_id = ? ORDER BY mark_at DESC";
			if (count == 0) {
				sqlString += ";";
				ps = conn.prepareStatement(sqlString);
				ps.setLong(1, user_id);
			} else {
				sqlString += " LIMIT ?;";
				ps = conn.prepareStatement(sqlString);
				ps.setLong(1, user_id);
				ps.setInt(2, count);
			}
			ResultSet rs = ps.executeQuery();
			// SimpleDateFormat sdf = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while (rs.next()) {
				record = new MarkRecordObj();
				record.setEmotion_medias(Tools.buildListFromString(rs
						.getString("emotion_medias")));
				record.setEmotion_text(rs.getString("emotion_text"));
				// record.setMark_at(sdf.parse(rs.getString("mark_at")));
				record.setMark_at(new Date(rs.getLong("mark_at")));
				record.setMedia_types(Tools.buildListFromString(rs
						.getString("media_types").replaceAll("[", "")
						.replaceAll("]", "")));
				record.setMedia_urls(Tools.buildListFromString(rs
						.getString("media_urls").replaceAll("[", "")
						.replaceAll("]", "")));
				record.setMedia_urls_local(Tools.buildListFromString(rs
						.getString("media_urls_local").replaceAll("[", "")
						.replaceAll("]", "")));
				record.setMsg_id(rs.getLong("msg_id"));
				record.setRecord_id(rs.getInt("record_id"));
				record.setText(rs.getString("text"));
				record.setUser_id(rs.getLong("user_id"));
				records.add(record);
			}
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
					e.printStackTrace();
					Logger.printThrowable(e);
					throw new RuntimeException(e);
				}
			}
		}
		return records;
	}

	private void updateRecordCount(long user_id, int count) {
		PreparedStatement ps = null;
		String sqlString = "UPDATE mark_detail SET count=" + count
				+ " WHERE user_id = ?;";
		Connection conn = null;
		try {
			conn = markDB.getConnection();
			ps = conn.prepareStatement(sqlString);
			ps.setLong(1, user_id);
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
					e.printStackTrace();
					Logger.printThrowable(e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	public int getRecordCount(long user_id) {
		PreparedStatement ps = null;
		int count = 0;
		Connection conn = null;
		try {
			conn = markDB.getConnection();
			String sqlString = "SELECT count(*) FROM mark_records WHERE user_id = ?";
			// String sqlString = "SELECT count FROM mark_detail WHERE user_id
			// = ?";
			ps = conn.prepareStatement(sqlString);
			ps.setLong(1, user_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
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
					e.printStackTrace();
					Logger.printThrowable(e);
					throw new RuntimeException(e);
				}
			}
		}
		updateRecordCount(user_id, count);
		return count;
	}
}
