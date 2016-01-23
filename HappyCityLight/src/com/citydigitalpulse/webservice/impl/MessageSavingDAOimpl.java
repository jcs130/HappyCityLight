package com.citydigitalpulse.webservice.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.citydigitalpulse.webservice.dao.MessageSavingDAO;

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

}
