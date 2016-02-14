package com.citydigitalpulse.collector.TwitterGetter.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.citydigitalpulse.collector.TwitterGetter.dao.InfoGetterDAO;
import com.citydigitalpulse.collector.TwitterGetter.model.EarthSqure;
import com.citydigitalpulse.collector.TwitterGetter.model.LocArea;
import com.citydigitalpulse.collector.TwitterGetter.model.LocPoint;
import com.citydigitalpulse.collector.TwitterGetter.model.RegInfo;

public class InfoGetterDAO_MySQL implements InfoGetterDAO {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public InfoGetterDAO_MySQL() {
		dataSource = new InfoDBHelper();
	}

	@Override
	public List<EarthSqure> getSqureInfo(int type) {
		// 数据库查询语句
		String sqlString = "SELECT * FROM earthsqure where streamstate ="
				+ type + ";";
		ArrayList<EarthSqure> result = new ArrayList<EarthSqure>();
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			EarthSqure squre = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				squre = new EarthSqure(rs.getDouble("south"),
						rs.getDouble("north"), rs.getDouble("west"),
						rs.getDouble("east"), rs.getInt("row"),
						rs.getInt("col"), rs.getDouble("degreepersqure"));
				squre.setSqureID(rs.getInt("squreid"));
				squre.setStreamState(rs.getInt("streamstate"));
				squre.setUseTimes(rs.getInt("usetimes"));
				squre.setThreadName(rs.getString("threadname"));
				result.add(squre);
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
						loc.setCenterAndRange(
								new LocPoint(rs.getDouble("center_lat"), rs
										.getDouble("center_lon")), rs
										.getInt("range"));
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
	public void saveEarthSqure(double south, double north, double west,
			double east, int row, int col, double degreepersqure) {
		// 先判断数据库中存不存在相同的区块，如果不存在新建一个数据
		if (!haveSqure(row, col)) {

			Connection conn = null;
			String sqlString = "INSERT INTO earthsqure (south, north, west, east, row, col,degreepersqure) VALUES ("
					+ south
					+ ", "
					+ north
					+ ", "
					+ west
					+ ", "
					+ east
					+ ", "
					+ row + ", " + col + ", " + degreepersqure + ");";
			try {
				conn = dataSource.getConnection();
				PreparedStatement ps = conn.prepareStatement(sqlString);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}
			}

		} else {
			// 如果存在了这个区块则什么也不做
		}
	}

	/**
	 * 查询数据库中是否有指定的区块
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean haveSqure(int row, int col) {
		EarthSqure es = getSqureInfo(row, col);
		if (es == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void saveEarthSqure(EarthSqure es) {
		saveEarthSqure(es.getSouth(), es.getNorth(), es.getWest(),
				es.getEast(), es.getRow(), es.getCol(),
				es.getDegreePerSqure_lon());

	}

	@Override
	public EarthSqure getSqureInfo(int row, int col) {
		// 根据type获取大区域的信息
		String sqlString = "SELECT * FROM earthsqure where row =" + row
				+ "&& col =" + col + ";";
		// 查询数据库，获取结果
		Connection conn = null;
		EarthSqure squre = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				squre = new EarthSqure(rs.getDouble("south"),
						rs.getDouble("north"), rs.getDouble("west"),
						rs.getDouble("east"), rs.getInt("row"),
						rs.getInt("col"), rs.getDouble("degreepersqure"));
				squre.setSqureID(rs.getInt("squreid"));
				squre.setStreamState(rs.getInt("streamstate"));
				squre.setUseTimes(rs.getInt("usetimes"));
				squre.setThreadName(rs.getString("threadname"));
			}
		} catch (SQLException e) {
			// throw new RuntimeException(e);
			e.printStackTrace();

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return squre;
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
				loc.setCenterAndRange(new LocPoint(rs.getDouble("center_lat"),
						rs.getDouble("center_lon")), rs.getInt("range"));
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

	// @Override
	// public List<EarthSqure> getSquresByLoc(double north, double south,
	// double west, double east) {
	// // 获取四个顶点的所在的区块，并把之间的区块都添加
	// return null;
	// }

	@Override
	public List<EarthSqure> getStreamSqures(RegInfo reg) {
		ArrayList<LocArea> areas = reg.getAreas();
		ArrayList<EarthSqure> result = new ArrayList<EarthSqure>();
		// System.out.println();
		// System.out.println(reg.getRegName());
		// 循环得到Stream区块
		for (int i = 0; i < areas.size(); i++) {
			// System.out.println("area:" + (i + 1));
			// 每个区块四个顶点分别计算
			// NW
			EarthSqure e1 = new EarthSqure(areas.get(i).getNorth(), areas
					.get(i).getWest());
			addToArray(result, e1);
			// NE
			EarthSqure e2 = new EarthSqure(areas.get(i).getNorth(), areas
					.get(i).getEast());
			addToArray(result, e2);
			// SW
			EarthSqure e3 = new EarthSqure(areas.get(i).getSouth(), areas
					.get(i).getWest());
			addToArray(result, e3);
			// SE
			EarthSqure e4 = new EarthSqure(areas.get(i).getSouth(), areas
					.get(i).getEast());
			addToArray(result, e4);
			// System.out.println("fill:");
			// 如果四个顶点所在的区域之间存在空隙则增加空隙区域的区块
			// 首先补全上下两边界的区域
			for (int j = e1.getCol(); j < e2.getCol(); j++) {
				EarthSqure e = new EarthSqure(e1.getRow(), j);
				addToArray(result, e);
			}
			for (int j = e3.getCol(); j < e4.getCol(); j++) {
				EarthSqure e = new EarthSqure(e3.getRow(), j);
				addToArray(result, e);
			}

			// 开始补全中间的行
			for (double j = e3.getNorth(); j < e1.getSouth(); j += 0.15) {
				// 这一行从西到东补全
				EarthSqure ew = new EarthSqure(j, areas.get(i).getWest());
				EarthSqure ee = new EarthSqure(j, areas.get(i).getEast());
				addToArray(result, ew);
				addToArray(result, ee);
				for (int k = ew.getCol(); k < ee.getCol(); k++) {
					EarthSqure e = new EarthSqure(ew.getRow(), k);
					addToArray(result, e);
				}
			}

		}
		return result;
	}

	private boolean addToArray(ArrayList<EarthSqure> list, EarthSqure e) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getRow() == e.getRow()
					&& list.get(i).getCol() == e.getCol()) {
				return false;
			}
		}
		// System.out.println(e);
		list.add(e);
		return true;

	}

	@Override
	public void changeRegState(int id, int type) {
		String sqlString = "UPDATE regnames SET streamstate=? WHERE regID=?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setInt(1, type);
			ps.setInt(2, id);
			ps.executeUpdate();

			ps.close();
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

	@Override
	public void changeSqureState(int id, int type, String threadname) {
		String sqlString = "UPDATE earthsqure SET streamstate=?, threadname=? WHERE squreid=?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setInt(1, type);
			ps.setString(2, threadname);
			ps.setInt(3, id);
			ps.executeUpdate();

			ps.close();
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

	@Override
	public void squreAddUseTime(int row, int col) {
		String sqlString = "UPDATE earthsqure SET usetimes=usetimes+1 WHERE row=? && col=?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setInt(1, row);
			ps.setInt(2, col);
			ps.executeUpdate();

			ps.close();
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

	@Override
	public void squreDelUseTime(int row, int col) {
		String sqlString = "UPDATE earthsqure SET usetimes=usetimes-1 WHERE row=? && col=?";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setInt(1, row);
			ps.setInt(2, col);
			ps.executeUpdate();

			ps.close();
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

	@Override
	public List<EarthSqure> getReadySqure() {
		// 数据库查询语句
		String sqlString = "SELECT * FROM earthsqure where streamstate =0&&usetimes>0;";
		ArrayList<EarthSqure> result = new ArrayList<EarthSqure>();
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			EarthSqure squre = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				squre = new EarthSqure(rs.getDouble("south"),
						rs.getDouble("north"), rs.getDouble("west"),
						rs.getDouble("east"), rs.getInt("row"),
						rs.getInt("col"), rs.getDouble("degreepersqure"));
				squre.setSqureID(rs.getInt("squreid"));
				squre.setStreamState(rs.getInt("streamstate"));
				squre.setUseTimes(rs.getInt("usetimes"));
				squre.setThreadName(rs.getString("threadname"));
				result.add(squre);
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
	public List<EarthSqure> getShouldStopSqure() {
		// 数据库查询语句
		String sqlString = "SELECT * FROM earthsqure where streamstate =1&&usetimes<=0;";
		ArrayList<EarthSqure> result = new ArrayList<EarthSqure>();
		// 查询数据库，获取结果
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			EarthSqure squre = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				squre = new EarthSqure(rs.getDouble("south"),
						rs.getDouble("north"), rs.getDouble("west"),
						rs.getDouble("east"), rs.getInt("row"),
						rs.getInt("col"), rs.getDouble("degreepersqure"));
				squre.setSqureID(rs.getInt("squreid"));
				squre.setStreamState(rs.getInt("streamstate"));
				squre.setUseTimes(rs.getInt("usetimes"));
				squre.setThreadName(rs.getString("threadname"));
				result.add(squre);
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
	public void reSetStates() {
		String sqlString = "UPDATE earthsqure SET streamstate=0, usetimes=0, threadname=? WHERE squreID>0;";
		String sqlString2 = "UPDATE regnames SET streamstate=0 WHERE regid>0;";
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			ps.setString(1, "none");
			ps.executeUpdate();
			ps.close();
			ps = conn.prepareStatement(sqlString2);
			ps.executeUpdate();
			ps.close();
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
