/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Yuanyuan Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.collector.RealTimeInstagramGetter.dao.ipml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.citydigitalpulse.collector.RealTimeInstagramGetter.dao.AskingInterface;
import com.citydigitalpulse.collector.RealTimeInstagramGetter.model.asking;

public class AskingInterface_MySQL implements AskingInterface {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public AskingInterface_MySQL() {
		dataSource = new AskingConnector();
	}

	@Override
	public List<asking> GetAskingInfo(int type) {
		// Return list of cities that required crawling with type 0
		String sqlString = "SELECT * FROM asking_ig_realtime where getting_status =" + type
				+ " and message_from='instagram' LIMIT 50" + ";";
		ArrayList<asking> result = new ArrayList<asking>();
		// Query the DB and return the result
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			asking ask = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ask = new asking();
				ask.set_num_id(rs.getInt("num_id"));
				ask.set_city_name(rs.getString("city_name"));
				ask.set_latitude(rs.getDouble("latitude"));
				ask.set_longitude(rs.getDouble("longitude"));
				ask.set_message_from(rs.getString("message_from"));
				// System.out.println(ask);
				result.add(ask);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		// System.out.println(result.size());
		return result;

	}

	@Override
	public void ChangeAskingStatus(ArrayList<asking> queuingList, int type) {
		// Update the status of the asking DB

		String sqlString = "UPDATE asking_ig_realtime SET getting_status=? WHERE num_id=?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			for (int i = 0; i < queuingList.size(); i++) {
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ps.setInt(1, type);
				ps.setInt(2, queuingList.get(i).get_num_id());
				ps.executeUpdate();
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void reSetStates() {
		// Reset the DB
		// Set all the status of cities to 0 which means not crawling
		String sqlString = "UPDATE asking_ig_realtime SET status=4 WHERE num_id>0;";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setString(1, "none");
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
					e.printStackTrace();
				}
			}
		}
	}
}
