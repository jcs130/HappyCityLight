package com.citydigitalpulse.messagegetter.TwitterGetter.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.citydigitalpulse.messagegetter.TwitterGetter.dao.TwitterSaveDAO;
import com.citydigitalpulse.messagegetter.TwitterGetter.model.StructuredFullMessage;
import com.citydigitalpulse.messagegetter.TwitterGetter.tool.Tools;

public class TwitterSavingDAOimpl implements TwitterSaveDAO {
	MessageSavingDBHelper savingDB;

	public TwitterSavingDAOimpl() {
		savingDB = new MessageSavingDBHelper();
	}

	@Override
	public long insert(StructuredFullMessage msg) {
		PreparedStatement ps = null;
		Connection conn = null;
		long key = 0;
		try {
			conn = savingDB.getConnection();
			String sqlString = "INSERT INTO full_message (raw_id_str, user_name, creat_at, text, media_types, media_urls, media_urls_local, place_type, place_name, place_fullname, country, province, city, query_location_latitude, query_location_langtitude, hashtags, replay_to, lang, message_from,is_real_location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			ps = conn.prepareStatement(sqlString,
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, msg.getRaw_id_str());
			ps.setString(2, msg.getUser_name());
			ps.setLong(3, msg.getCreat_at());
			ps.setString(4, msg.getText());
			ps.setString(5, Tools.buildStringFromList(msg.getMedia_type()));
			ps.setString(6, Tools.buildStringFromList(msg.getMedia_urls()));
			ps.setString(7,
					Tools.buildStringFromList(msg.getMedia_urls_local()));
			ps.setString(8, msg.getPlace_type());
			ps.setString(9, msg.getPlace_name());
			ps.setString(10, msg.getPlace_fullname());
			ps.setString(11, msg.getCountry());
			ps.setString(12, msg.getProvince());
			ps.setString(13, msg.getCity());
			ps.setDouble(14, msg.getQuery_location_latitude());
			ps.setDouble(15, msg.getQuery_location_langtitude());
			ps.setString(16, Tools.buildStringFromList(msg.getHashtags()));
			ps.setString(17, msg.getReplay_to());
			ps.setString(18, msg.getLang());
			ps.setString(19, msg.getMessage_from());
			ps.setString(20, Boolean.toString(msg.isReal_location()));
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				key = rs.getLong(1);
				System.out.println("key:" + key);
			} else {
				// 插入失败
			}
			// 获得插入数据库的编号
			return key;
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

	@Override
	public void insert(List<StructuredFullMessage> msgs) {
		Connection conn = null;
		Statement statement = null;
		StructuredFullMessage msg;
		try {
			conn = savingDB.getConnection();
			// 指定在事物中提交
			conn.setAutoCommit(false);
			statement = conn.createStatement();
			// 循环添加新消息
			for (int i = 0; i < msgs.size(); i++) {
				msg = msgs.get(i);
				String sqlString = "INSERT INTO full_message (raw_id_str, user_name, creat_at, text, media_types, media_urls, media_urls_local, place_type, place_name, place_fullname, country, province, city, query_location_latitude, query_location_langtitude, hashtags, replay_to, lang, message_from,is_real_location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ps.setString(1, msg.getRaw_id_str());
				ps.setString(2, msg.getUser_name());
				ps.setLong(3, msg.getCreat_at());
				ps.setString(4, msg.getText());
				ps.setString(5, Tools.buildStringFromList(msg.getMedia_type()));
				ps.setString(6, Tools.buildStringFromList(msg.getMedia_urls()));
				ps.setString(7,
						Tools.buildStringFromList(msg.getMedia_urls_local()));
				ps.setString(8, msg.getPlace_type());
				ps.setString(9, msg.getPlace_name());
				ps.setString(10, msg.getPlace_fullname());
				ps.setString(11, msg.getCountry());
				ps.setString(12, msg.getProvince());
				ps.setString(13, msg.getCity());
				ps.setDouble(14, msg.getQuery_location_latitude());
				ps.setDouble(15, msg.getQuery_location_langtitude());
				ps.setString(16, Tools.buildStringFromList(msg.getHashtags()));
				ps.setString(17, msg.getReplay_to());
				ps.setString(18, msg.getLang());
				ps.setString(19, msg.getMessage_from());
				ps.setString(20, Boolean.toString(msg.isReal_location()));
				ps.executeUpdate();
			}
			// 提交更改
			conn.commit();
		} catch (SQLException e) {
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
	// public void insert(List<StructuredFullMessage> msgs) {
	// //使用MySQL特有的语法完成
	// PreparedStatement ps = null;
	// Connection conn = null;
	// try {
	// conn = savingDB.getConnection();
	// String sqlString =
	// "INSERT INTO full_message (raw_id_str, user_name, creat_at, text, media_types, media_urls, media_urls_local, place_type, place_name, place_fullname, country, province, city, query_location_latitude, query_location_langtitude, hashtags, replay_to, lang, message_from) VALUES";
	// StructuredFullMessage msg;
	// for (int i = 0; i < msgs.size(); i++) {
	// msg = msgs.get(i);
	// sqlString += "('" + msg.getRaw_id_str() + "', '"
	// + msg.getUser_name() + "', " + msg.getCreat_at()
	// + ", '" + msg.getText() + "', '"
	// + Tools.buildStringFromList(msg.getMedia_type())
	// + "', '"
	// + Tools.buildStringFromList(msg.getMedia_urls())
	// + "', '"
	// + Tools.buildStringFromList(msg.getMedia_urls_local())
	// + "', '" + msg.getPlace_type() + "', '"
	// + msg.getPlace_name() + "', '"
	// + msg.getPlace_fullname() + "', '" + msg.getCountry()
	// + "', '" + msg.getProvince() + "', '" + msg.getCity()
	// + "', " + msg.getQuery_location_latitude() + ", "
	// + msg.getQuery_location_langtitude() + ", '"
	// + Tools.buildStringFromList(msg.getHashtags()) + "', '"
	// + msg.getReplay_to() + "', '" + msg.getLang() + "', '"
	// + msg.getMessage_from() + "')";
	// if (i < msgs.size() - 1) {
	// sqlString += ",";
	// } else {
	// sqlString += ";";
	// }
	// }
	// ps = conn.prepareStatement(sqlString);
	// ps.execute();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// throw new RuntimeException(e);
	// } finally {
	// try {
	// ps.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// throw new RuntimeException(e);
	// }
	// if (conn != null) {
	// try {
	// conn.close();
	// } catch (SQLException e) {
	// }
	// }
	// }
	//
	// }

}
