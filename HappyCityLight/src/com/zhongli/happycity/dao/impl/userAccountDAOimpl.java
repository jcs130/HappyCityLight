package com.zhongli.happycity.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.zhongli.happycity.dao.UserAccountDAO;
import com.zhongli.happycity.model.message.MarkMessageObj;
import com.zhongli.happycity.model.message.MediaObject;
import com.zhongli.happycity.model.user.User;
import com.zhongli.happycity.model.user.UserDetail;

public class userAccountDAOimpl implements UserAccountDAO {
	private MySQLHelper_User userDB;

	public userAccountDAOimpl() {
		this.userDB = new MySQLHelper_User();
	}

	@Override
	public boolean createUser(String email, String password) {
		if (getUserIDbyEmail(email) != -1) {
			// 用户名重复
			return false;
		}
		User user = new User(email, password);
		// insert into mark_records
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = userDB.getConnection();

			String sqlString = "INSERT INTO user_accounts "
					+ "(email, password, enable,create_at) VALUES"
					+ "(?, ?, ?, ?);";
			ps = conn.prepareStatement(sqlString);
			ps.setString(1, user.getEmail());
			ps.setString(2, user.getPassword());
			ps.setBoolean(3, user.isEnabled());
			ps.setLong(4, new Date().getTime());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		// 设置初始的角色
		ArrayList<Integer> roles = new ArrayList<Integer>();
		roles.add(3);
		if (setUserRoles(getUserIDbyEmail(email), roles)) {
			return true;
		} else {
			// 回滚？
			return false;
		}
	}

	@Override
	public long getUserIDbyEmail(String email) {
		PreparedStatement ps = null;
		Connection conn = null;
		long res = -1;
		try {
			conn = userDB.getConnection();

			String sqlString = "SELECT user_id FROM user_accounts WHERE email = ?;";
			ps = conn.prepareStatement(sqlString);

			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				res = rs.getLong("user_id");
				return res;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return res;
	}

	@Override
	public User getUserAccountByEmail(String email) {
		User res = new User();
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = userDB.getConnection();

			String sqlString = "SELECT * FROM user_accounts WHERE email = ?;";
			ps = conn.prepareStatement(sqlString);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				res.setUserId(rs.getLong("user_id"));
				res.setEmail(rs.getString("email"));
				res.setCreated_on(new Date(rs.getLong("create_on")));
				res.setPassword(rs.getString("password"));
				res.setEnabled(rs.getBoolean("enable"));
				res.setTokenExpired(rs.getBoolean("token_expired"));
				return res;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return res;

	}

	@Override
	public User getUserAccountByUserID(long userID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changePassword(long userID, String password) {
		PreparedStatement ps = null;
		String sqlString = "UPDATE user_accounts SET password = ? WHERE user_id = ?;";
		Connection conn = null;
		try {
			conn = userDB.getConnection();
			ps = conn.prepareStatement(sqlString);
			ps.setString(1, password);
			ps.setLong(2, userID);
			ps.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
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
	public boolean isUserAvailable(long userID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUserAvailable(long userID) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPasswordMatch(long userID, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setUserRoles(long userID, ArrayList<Integer> roles) {
		// TODO Auto-generated method stub
		return false;
	}

}
