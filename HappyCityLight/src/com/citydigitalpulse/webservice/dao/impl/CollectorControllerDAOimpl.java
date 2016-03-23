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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.citydigitalpulse.webservice.api.MessageResource;
import com.citydigitalpulse.webservice.dao.CollectorControllerDAO;
import com.citydigitalpulse.webservice.model.collector.LocArea;
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
	public List<RegInfo> getPublicRegInfo(int type) {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM regnames where streamstate =" + type
				+ " and private=0;";
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
				reg.setCountry(rs.getString("country"));
				reg.setBox_points(rs.getString("box_points"));
				reg.setCenter_lat(rs.getDouble("center_lat"));
				reg.setCenter_lan(rs.getDouble("center_lan"));
				reg.setTime_zone(rs.getInt("time_zone"));
				reg.setPrivate(rs.getBoolean("private"));
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
				result.add(loc);
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
		String sqlString = "SELECT regname FROM regnames where streamstate =1 and private=0;";
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
		RegInfo reg = null;
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setString(1, place_name);
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
				return reg;
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
		return reg;
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
	
	@Override
	public RegInfo getRegInfoByID(long regID) {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM regnames where regid =?;";
		RegInfo reg = null;
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setLong(1, regID);
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
				return reg;
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
		return reg;
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param userID
	 * @param reg_name
	 * @param country
	 * @param center_lat
	 * @param center_lan
	 * @param time_zone
	 * @param location_areas
	 * @return reg_id
	 */
	public int addNewOnlineTask(long userID, String reg_name, String country,
			double center_lat, double center_lan, int time_zone,
			ArrayList<LocArea> location_areas) {
		ArrayList<Integer> area_ids = new ArrayList<Integer>();
		// 添加新的区域信息并且返回ID
		for (int i = 0; i < location_areas.size(); i++) {
			area_ids.add(addNewArea(location_areas.get(i)));
		}
		// 添加新的大区域
		int reg_id = addNewRegion(reg_name, country, center_lat, center_lan,
				time_zone);
		// 建立联系并且初始化状态
		addRel(reg_id, area_ids);
		// 更改区域状态
		changeRegStates(reg_id,0);
		return reg_id;
	}

	/**
	 * @Author  Zhongli Li Email: lzl19920403@gmail.com
	 * @param reg_id
	 */
	private void changeRegStates(int reg_id,int status) {
		PreparedStatement ps = null;
		String sqlString = "UPDATE regnames SET streamstate="+status+" WHERE regid="+reg_id+";";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sqlString);
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

	private void addRel(int reg_id, ArrayList<Integer> area_ids) {
		// 添加大区域和小区域的对应关系
		Connection conn = null;
		Statement statement = null;
		try {
			conn = dataSource.getConnection();
			// 指定在事物中提交
			conn.setAutoCommit(false);
			statement = conn.createStatement();
			// 循环添加新消息
			for (int i = 0; i < area_ids.size(); i++) {
				String sqlString = "INSERT INTO regandarea (regid,areaid) VALUES (?, ?);";
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ps.setInt(1, reg_id);
				ps.setInt(2, area_ids.get(i));
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

	private int addNewRegion(String reg_name, String country,
			double center_lat, double center_lan, int time_zone) {
		PreparedStatement ps = null;
		Connection conn = null;
		int key = 0;
		try {
			conn = dataSource.getConnection();
			String sqlString = "INSERT INTO regnames (regname, country, streamstate, private, center_lat, center_lan, time_zone) VALUES (?, ?, ?, ?, ?, ?, ?);";
			ps = conn.prepareStatement(sqlString,
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, reg_name);
			ps.setString(2, country);
			ps.setInt(3, 1);
			ps.setInt(4, 1);
			ps.setDouble(5, center_lat);
			ps.setDouble(6, center_lan);
			ps.setInt(7, time_zone);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				key = rs.getInt(1);
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

	private int addNewArea(LocArea area) {
		PreparedStatement ps = null;
		Connection conn = null;
		int key = 0;
		try {
			conn = dataSource.getConnection();
			String sqlString = "INSERT INTO interestareas (north, east, south, west) VALUES (?, ?, ?, ?);";
			ps = conn.prepareStatement(sqlString,
					Statement.RETURN_GENERATED_KEYS);
			ps.setDouble(1, area.getNorth());
			ps.setDouble(2, area.getEast());
			ps.setDouble(3, area.getSouth());
			ps.setDouble(4, area.getWest());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				key = rs.getInt(1);
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

}
