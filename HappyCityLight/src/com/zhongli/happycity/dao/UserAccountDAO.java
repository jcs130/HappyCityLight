package com.zhongli.happycity.dao;

import java.util.ArrayList;

import com.zhongli.happycity.model.user.Role;
import com.zhongli.happycity.model.user.UserAccount;
import com.zhongli.happycity.model.user.UserDetail;

/**
 * 用户数据操作类
 * 
 * @author zhonglili
 *
 */
public interface UserAccountDAO {
	/**
	 * 创建新用户
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	public boolean createUser(String email, String password);

	/**
	 * 设置用户角色
	 * 
	 * @param userID
	 * @param roles
	 * @return
	 */
	public boolean setUserRoles(long userID, ArrayList<Integer> roles);

	/**
	 * 根据邮件找到用户编号
	 * 
	 * @param email
	 * @return
	 */
	public long getUserIDbyEmail(String email);

	/**
	 * 根据用户编号获得用户账户信息
	 * 
	 * @param userID
	 * @return
	 */
	public UserAccount getUserAccountByUserID(long userID);

	/**
	 * 根据用户编号获取用户角色
	 * 
	 * @param userID
	 * @return
	 */
	public ArrayList<Role> getUserRolesByUserId(long userID);

	/**
	 * 更改用户密码
	 * 
	 * @param userID
	 * @param password
	 * @return
	 */
	public boolean changePassword(long userID, String password);

	/**
	 * 检查密码是否相同
	 * 
	 * @param userID
	 * @param password
	 * @return
	 */
	public boolean isPasswordMatch(long userID, String password);

	/**
	 * 查看用户是否通过验证
	 * 
	 * @param userID
	 * @return
	 */
	public boolean isUserAvailable(long userID);

	/**
	 * 启用用户
	 * 
	 * @param userID
	 */
	public void setUserAvailable(long userID);
}
