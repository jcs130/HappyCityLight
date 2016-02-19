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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.citydigitalpulse.webservice.api.MessageResource;
import com.citydigitalpulse.webservice.dao.CollectorControllerDAO;
import com.citydigitalpulse.webservice.model.collector.LocArea;
import com.citydigitalpulse.webservice.model.collector.LocPoint;
import com.citydigitalpulse.webservice.model.collector.OfflineTask;
import com.citydigitalpulse.webservice.model.collector.RegInfo;

public class CollectorControllerDAOimpl implements CollectorControllerDAO {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public CollectorControllerDAOimpl() {
		dataSource = new MySQLHelper_Controller();
	}

	@Override
	public List<RegInfo> getRegInfo(int type) {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM regnames where streamstate =" + type
				+ ";";
		ArrayList<RegInfo> result = new ArrayList<RegInfo>();
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			RegInfo reg = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				reg = new RegInfo(rs.getString("regname"));
				reg.setRegID(rs.getInt("regid"));
				reg.setBox_points(rs.getString("box_points"));
				getAreasByReg(reg);
				// System.out.println(reg);
				result.add(reg);
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
		// System.out.println(result.size());
		return result;
	}

	// 读取大小区域关系表添加大区域下的小区域
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
				conn = dataSource.getConnection();
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

	@Override
	public List<LocArea> getAreaByLoc(double north, double south, double west,
			double east) {
		// 数据库查询语句
		String sqlString = "SELECT * FROM interestareas where center_lat<="
				+ north + "&&center_lat>=" + south + "&&center_lon>=" + west
				+ "&&center_lon<=" + east + ";";
		ArrayList<LocArea> result = new ArrayList<LocArea>();
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				LocArea loc = new LocArea(rs.getInt("areaid"),
						rs.getDouble("north"), rs.getDouble("west"),
						rs.getDouble("south"), rs.getDouble("east"));
				// loc.setCenterAndRange(new
				// LocPoint(rs.getDouble("center_lat"),
				// rs.getDouble("center_lon")), rs.getInt("range"));
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
		return result;
	}

	@Override
	public ArrayList<String> getListenPlaces() {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM regnames where streamstate =1;";
		ArrayList<String> result = new ArrayList<String>();
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("regname").toLowerCase());
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
		MessageResource.setStreamPlaceNames(result);
		// System.out.println(result.size());
		return result;
	}

	@Override
	public RegInfo getRegInfoByName(String place_name) {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM regnames where regname =?;";
		RegInfo result = null;
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setString(1, place_name);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = new RegInfo(rs.getString("regname"));
				result.setRegID(rs.getInt("regid"));
				result.setBox_points(rs.getString("box_points"));
				getAreasByReg(result);
				// System.out.println(reg);
				return result;
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
		// System.out.println(result.size());
		return result;
	}

	@Override
	public void updateRegBoxInfo(RegInfo reg) {

		PreparedStatement ps = null;
		String sqlString = "UPDATE regnames SET box_points=? WHERE regid=?;";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sqlString);
			ps.setString(1, reg.getBox_points());
			ps.setInt(2, reg.getRegID());
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
	public int addNewOfflineTask(OfflineTask task) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public OfflineTask getOfflineTaskByTaskID(long task_id) {
		// TODO Auto-generated method stub
		return null;
	}
}
