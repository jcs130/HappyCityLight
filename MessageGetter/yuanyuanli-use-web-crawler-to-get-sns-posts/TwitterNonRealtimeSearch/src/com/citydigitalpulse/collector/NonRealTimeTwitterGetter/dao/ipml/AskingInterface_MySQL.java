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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.AskingInterface;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.asking;

/**
 * Get and Change status of quests in asking table
 */
public class AskingInterface_MySQL implements AskingInterface {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public AskingInterface_MySQL() {
		dataSource = new AskingConnector();
	}

	@Override
	/**
	 * Get the asking information in asking table with type "type"
	 * 
	 * @param int
	 *            type
	 */
	public List<asking> GetAskingInfo(int type) {
		// Return the first of cities that required crawling with type 0
		String sqlString = "SELECT * FROM asking where getting_status =" + type + " LIMIT 1;";
		// System.out.println(sqlString);
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
				ask.set_radius(rs.getDouble("radius"));
				ask.set_start_date(rs.getString("start_date"));
				ask.set_end_date(rs.getString("end_date"));
				ask.set_message_from(rs.getString("message_from"));
				result.add(ask);
			}
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
		return result;

	}

	@Override
	/**
	 * Update getting status in asking table with type "type" where num_id is
	 * "num_id"
	 * 
	 * @param HashSet<Integer>
	 *            numIds
	 * @param int
	 *            type
	 */
	public void ChangeAskingStatus(HashSet<Integer> numIds, int type) {
		// Update the status of the asking DB

		String sqlString = "UPDATE asking SET getting_status=? WHERE num_id=?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			ids.addAll(numIds);
			for (int i = 0; i < ids.size(); i++) {
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ps.setInt(1, type);
				ps.setInt(2, ids.get(i));
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
		String sqlString = "UPDATE asking SET getting_status=0 WHERE num_id>0;";
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

	@Override
	/**
	 * Update getting status in asking table with type "type" where num_id is
	 * "num_id"
	 * 
	 * @param HashSet<Integer>
	 *            numIds
	 * @param int
	 *            type
	 */
	public void ChangeAskingStatus(int num_id, int type) {
		String sqlString = "UPDATE asking SET getting_status=? WHERE num_id=?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setInt(1, type);
			ps.setInt(2, num_id);
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
