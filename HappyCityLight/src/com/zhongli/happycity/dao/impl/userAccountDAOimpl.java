package com.zhongli.happycity.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.zhongli.happycity.dao.UserAccountDAO;
import com.zhongli.happycity.model.message.MarkMessageObj;
import com.zhongli.happycity.model.message.MediaObject;
import com.zhongli.happycity.model.user.Privilege;
import com.zhongli.happycity.model.user.Role;
import com.zhongli.happycity.model.user.UserAccount;
import com.zhongli.happycity.model.user.UserDetail;

public class userAccountDAOimpl implements UserAccountDAO {
	private MySQLHelper_User userDB;
	private HashMap<Integer, Role> role_map;

	public userAccountDAOimpl() {
		this.userDB = new MySQLHelper_User();
	}

	@Override
	public boolean createUser(String email, String password) {
		if (getUserIDbyEmail(email) != -1) {
			// 用户名重复
			return false;
		}
		UserAccount user = new UserAccount(email, password);
		// insert into mark_records
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = userDB.getConnection();

			String sqlString = "INSERT INTO user_account "
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

			String sqlString = "SELECT user_id FROM user_account WHERE email = ?;";
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
	public UserAccount getUserAccountByUserID(long userID) {
		UserAccount res = new UserAccount();
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = userDB.getConnection();

			String sqlString = "SELECT * FROM user_account WHERE user_id = ?;";
			ps = conn.prepareStatement(sqlString);
			ps.setLong(1, userID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				res.setUserId(rs.getLong("user_id"));
				res.setEmail(rs.getString("email"));
				res.setCreated_on(new Date(rs.getLong("create_on")));
				res.setPassword(rs.getString("password"));
				res.setEnabled(rs.getBoolean("enable"));
				res.setTokenExpired(rs.getBoolean("token_expired"));
				ArrayList<Role> roles = getUserRolesByUserId(userID);
				res.setRoles(roles);
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
	public ArrayList<Role> getUserRolesByUserId(long userID) {
		// 1. 在用户-角色表里面找到用户对应的角色编号
		PreparedStatement ps = null;
		Connection conn = null;
		ArrayList<Role> res = new ArrayList<Role>();
		try {
			conn = userDB.getConnection();

			String sqlString = "SELECT role_id FROM user_role WHERE user_id = ?;";
			ps = conn.prepareStatement(sqlString);
			ps.setLong(1, userID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Role r = new Role();
				r.setId(rs.getInt("role_id"));
				res.add(r);
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
		// 通过角色编号在角色表中找到角色的名称和对应的权限
		return updateRoles(res);
	}

	private ArrayList<Role> updateRoles(ArrayList<Role> raw) {
		ArrayList<Role> res = new ArrayList<Role>();
		for (int i = 0; i < raw.size(); i++) {
			// 根据角色ID查询角色名称以及权限
			res.add(getRoleByRoleId(raw.get(i).getId()));
		}
		return res;
	}

	/**
	 * 通过角色ID更新角色的详细信息
	 * 
	 * @param role_id
	 * @return
	 */
	private Role getRoleByRoleId(int role_id) {
		// 如果角色在缓存中则直接调用缓存中数据，若不在缓存中则重新从数据库中读取
		if (role_map.containsKey(role_id)) {
			return role_map.get(role_id);
		} else {
			Role res = new Role();
			res.setId(role_id);
			PreparedStatement ps1 = null;
			PreparedStatement ps2 = null;
			PreparedStatement ps3 = null;
			Connection conn = null;
			try {
				conn = userDB.getConnection();
				// 1. 通过角色ID得到角色名称
				String sqlString1 = "SELECT role_name FROM role WHERE role_id = ?;";
				ps1 = conn.prepareStatement(sqlString1);
				ps1.setLong(1, role_id);
				ResultSet rs1 = ps1.executeQuery();
				while (rs1.next()) {
					res.setName(rs1.getString("role_name"));
				}
				// 2. 通过角色ID获得角色权限ID
				ArrayList<Privilege> privileges = new ArrayList<Privilege>();
				String sqlString2 = "SELECT privilege_id FROM role_privilege WHERE role_id = ?;";
				ps2 = conn.prepareStatement(sqlString2);
				ps2.setLong(1, role_id);
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					Privilege pri = new Privilege();
					pri.setId(rs2.getInt("privilege_id"));
					privileges.add(pri);
				}
				for (int i = 0; i < privileges.size(); i++) {
					// 3.通过权限ID获得权限名称并补充权限对象
					String sqlString3 = "SELECT privilege_name FROM privilege WHERE privilege_id = ?;";
					ps3 = conn.prepareStatement(sqlString3);
					ps3.setLong(1, privileges.get(i).getId());
					ResultSet rs3 = ps3.executeQuery();
					while (rs3.next()) {
						privileges.get(i).setName(
								rs3.getString("privilege_name"));
						privileges.get(i).setDescription(
								rs3.getString("privilege_description"));
					}
				}
				res.setPrivileges(privileges);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					ps1.close();
					ps2.close();
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
			role_map.put(role_id, res);
			return res;
		}

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
