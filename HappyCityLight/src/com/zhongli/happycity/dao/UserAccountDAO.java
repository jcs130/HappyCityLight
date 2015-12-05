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
	 * 重设用户角色
	 * 
	 * @param userID
	 * @param roles
	 * @return
	 */
	public boolean setUserRoles(long userID, ArrayList<Role> roles);

	/**
	 * 新增角色
	 * 
	 * @param userID
	 * @param role
	 * @return
	 */
	public boolean addUserRole(long userID, Role role);

	/**
	 * 删除角色
	 * 
	 * @param userID
	 * @param role
	 * @return
	 */
	public boolean delUserRole(long userID, Role role);

	/**
	 * 根据角色名称找到角色ID
	 * 
	 * @param roleName
	 * @return
	 */
	public int getRoleIdByRoleName(String roleName);

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
	 * 根据账户获取用户信息
	 * 
	 * @param email
	 * @return
	 */
	public UserAccount getUserAccountByEmail(String email);

	/**
	 * 更新登录Token
	 * 
	 * @param userID
	 * @return
	 */
	public boolean updateToken(long userID);

	/**
	 * 使用Token登陆
	 * 
	 * @param userID
	 * @param token
	 * @return
	 */
	public boolean tokenCheck(long userID, String token);

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
	 * @return
	 */
	public boolean userActive(long userID);

	/**
	 * 根据用户ID得到用户详细信息
	 * 
	 * @param user_id
	 * @return
	 */
	public UserDetail getUserDetailByUserId(long userID);
}
