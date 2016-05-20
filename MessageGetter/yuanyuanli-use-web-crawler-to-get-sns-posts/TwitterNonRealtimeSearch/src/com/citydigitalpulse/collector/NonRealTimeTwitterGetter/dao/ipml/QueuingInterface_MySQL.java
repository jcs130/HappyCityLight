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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.QueuingInterface;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.queuing;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.taskStatus;

/**
 * SQLs on operations of queuing table
 */
public class QueuingInterface_MySQL implements QueuingInterface {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public QueuingInterface_MySQL() {
		dataSource = new QueuingConnector();
	}

	@Override
	public List<queuing> GetQueuingInfo(int type) {
		String sqlString = "";
		if (type >= 5) {
			// Return list of queuing with status between 4 and 8
			sqlString = "SELECT * FROM queuing where (status  between 4 and 8);";
		} else // Return list of cities that required crawling with type 0
			sqlString = "SELECT * FROM queuing where status =" + type + " LIMIT 10;";
		ArrayList<queuing> result = new ArrayList<queuing>();
		// Query the DB and return the result
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			queuing que = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				que = new queuing();
				que.set_num_id(rs.getInt("num_id"));
				que.set_task_id(rs.getInt("task_id"));
				que.set_place_name(rs.getString("name"));
				que.set_streetAddress(rs.getString("streetAddress"));
				que.set_country(rs.getString("country"));
				que.set_placeType(rs.getString("placeType"));
				que.set_place_fullName(rs.getString("fullName"));
				que.set_boundingBoxType(rs.getString("boundingBoxType"));
				que.set_boundingBoxCoordinatesLatitude(rs.getDouble("boundingBoxCoordinatesLatitude"));
				que.set_boundingBoxCoordinatesLongitude(rs.getDouble("boundingBoxCoordinatesLongitude"));
				que.set_place_id(rs.getString("place_id"));
				que.set_start_date(rs.getString("start_date"));
				que.set_end_date(rs.getString("end_date"));
				que.set_in_what_lan(rs.getString("in_what_lan"));
				que.set_message_from(rs.getString("message_from"));
				que.set_status(rs.getInt("status"));
				// System.out.println(ask);
				result.add(que);
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
	public ArrayList<taskStatus> GetQueuingTaskStatus(int taskid) {
		// Return num_id, task_id and status with no duplicates
		String sqlString = "SELECT DISTINCT task_id, status from queuing where task_id = "+taskid+";";
		// String sqlString = "SELECT DISTINCT task_id, status FROM queuing;";
		ArrayList<taskStatus> result = new ArrayList<taskStatus>();
		// Query the DB and return the result
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			taskStatus task = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				task = new taskStatus();
				task.setTask_id(rs.getInt("task_id"));
				task.setStatus(rs.getInt("status"));
				result.add(task);
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
	public ArrayList<Integer> GetQueuingTaskIDs() {
		// Return num_id, task_id and status with no duplicates
		String sqlString = "SELECT DISTINCT task_id from queuing;";
		// String sqlString = "SELECT DISTINCT task_id, status FROM queuing;";
		ArrayList<Integer> result = new ArrayList<Integer>();
		// Query the DB and return the result
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("task_id"));
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
	public void ChangeQueuingStatus(ArrayList<queuing> queList, int type) {
		// Update the status of the asking DB
		String sqlString = "UPDATE queuing SET status=? WHERE num_id=?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			for (int i = 0; i < queList.size(); i++) {
				PreparedStatement ps = conn.prepareStatement(sqlString);
				if (type >= 5) {
					ps.setInt(1, queList.get(i).get_status() + 1);
				} else
					ps.setInt(1, type);
				ps.setInt(2, queList.get(i).get_num_id());
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
	public void ChangeQueuingStatus(int num_id, int type) {
		// Update the status of the asking DB

		String sqlString = "UPDATE queuing SET status=? WHERE num_id=?";
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

	@Override
	public void reSetStates() {
		// Reset the DB
		// Set all the status of cities to 0 which means not crawling
		String sqlString = "UPDATE queuing SET status=0 WHERE num_id>0;";
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
	public void insert(queuing que) {
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			String sqlString = "INSERT INTO queuing (task_id, name, streetAddress, country, placeType, fullName, boundingBoxType, boundingBoxCoordinatesLatitude, boundingBoxCoordinatesLongitude, place_id, start_date, end_date, in_what_lan, message_from, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			ps = conn.prepareStatement(sqlString);
			ps.setInt(1, que.get_task_id());
			ps.setString(2, que.get_place_name());
			ps.setString(3, que.get_streetAddress());
			ps.setString(4, que.get_country());
			ps.setString(5, que.get_placeType());
			ps.setString(6, que.get_place_fullName());
			ps.setString(7, que.get_boundingBoxType());
			ps.setDouble(8, que.get_boundingBoxCoordinatesLatitude());
			ps.setDouble(9, que.get_boundingBoxCoordinatesLongitude());
			ps.setString(10, que.get_place_id());
			ps.setString(11, que.get_start_date());
			ps.setString(12, que.get_end_date());
			ps.setString(13, que.get_in_what_lan());
			ps.setString(14, que.get_message_from());
			ps.setInt(15, que.get_status());
			ps.execute();
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
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void insert(List<queuing> ques) {

		Connection conn = null;
		Statement statement = null;
		queuing que;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			statement = conn.createStatement();
			for (int i = 0; i < ques.size(); i++) {
				que = ques.get(i);
				String sqlString = "INSERT INTO queuing (task_id, name, streetAddress, country, placeType, fullName, boundingBoxType, boundingBoxCoordinatesLatitude, boundingBoxCoordinatesLongitude, place_id, start_date, end_date, in_what_lan, message_from, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ps = conn.prepareStatement(sqlString);
				ps.setInt(1, que.get_task_id());
				ps.setString(2, que.get_place_name());
				ps.setString(3, que.get_streetAddress());
				ps.setString(4, que.get_country());
				ps.setString(5, que.get_placeType());
				ps.setString(6, que.get_place_fullName());
				ps.setString(7, que.get_boundingBoxType());
				ps.setDouble(8, que.get_boundingBoxCoordinatesLatitude());
				ps.setDouble(9, que.get_boundingBoxCoordinatesLongitude());
				ps.setString(10, que.get_place_id());
				ps.setString(11, que.get_start_date());
				ps.setString(12, que.get_end_date());
				ps.setString(13, que.get_in_what_lan());
				ps.setString(14, que.get_message_from());
				ps.setInt(15, que.get_status());
				ps.execute();
				ps.close();
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
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
