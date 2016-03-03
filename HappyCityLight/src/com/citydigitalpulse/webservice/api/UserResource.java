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
package com.citydigitalpulse.webservice.api;

import java.util.ArrayList;
import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.citydigitalpulse.webservice.dao.UserAccountDAO;
import com.citydigitalpulse.webservice.dao.impl.userAccountDAOimpl;
import com.citydigitalpulse.webservice.model.message.ResMsg;
import com.citydigitalpulse.webservice.model.user.Role;
import com.citydigitalpulse.webservice.model.user.UserAccount;
import com.citydigitalpulse.webservice.model.user.UserDetail;
import com.citydigitalpulse.webservice.tool.Tools;

/**
 * 用于用户注册登陆以及验证
 * 
 * @author zhonglili
 *
 */
@Path("/user")
public class UserResource {
	@Context
	private UriInfo urlInfo;
	private UserAccountDAO userAccountDAO = new userAccountDAOimpl();
	private static String RequreRole = "ROLE_USER";

	// 测试方法
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg test() {
		ResMsg res = new ResMsg();
		String baseURL = urlInfo.getBaseUri().toString();
		String serverURL = baseURL.substring(0, baseURL.indexOf("api/"));
		res.setCode(Response.Status.OK.getStatusCode());
		res.setMessage("Hello  " + urlInfo.getPath() + " <> " + baseURL
				+ " <> " + serverURL);
		return res;

	}

	@POST
	@Path("/tokencheck")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg tokenCheck(@FormParam("userID") long userID,
			@FormParam("token") String token) {
		ResMsg res = new ResMsg();
		System.out.println(userID + " <> " + token);
		if (!userAccountDAO.tokenCheck(userID, token)) {
			res.setCode(Response.Status.FORBIDDEN.getStatusCode());
			res.setType(Response.Status.FORBIDDEN.name());
			res.setMessage("Token expaired. Please login again.");
			return res;
		} else {
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Token is ok.");
			return res;
		}
	}

	@POST
	@Path("/registration")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg registerUserAccount(@FormParam("email") String email,
			@FormParam("password") String password) {
		ResMsg res = new ResMsg();
		try {
			System.out.println(email);
			// Save data to ds;
			String encriptPassword = Tools.HmacSHA256Encrypt(
					Tools.MD5(password), ConfigValues.secureKey_savepassword);
			if (Tools.emailFormat(email)) {
				if (userAccountDAO.getUserIDbyEmail(email) != -1) {
					res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
					res.setType(Response.Status.BAD_REQUEST.name());
					System.out.println("Email already exist.");
					res.setMessage("Email already exist.");
					return res;
				}
				if (userAccountDAO.createUser(email, encriptPassword)) {
					System.out.println(sendConfirmEmail(email).getCode());
					res.setType(Response.Status.OK.name());
					res.setCode(Response.Status.OK.getStatusCode());
					res.setMessage("Create User successful");
					return res;
				}
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Please try again.");
				return res;
			} else {
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Email format error.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
			res.setMessage("unknown" + e.getLocalizedMessage());
			return res;
		}
	}

	// 确认用户邮箱
	@GET
	@Path("/regitrationConfirm")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg regitrationConfirm(@QueryParam("token") String token) {
		ResMsg res = new ResMsg();
		try {
			System.out.println(token);
			String email = Tools.DESdecrypt(token,
					ConfigValues.secureKey_verification);
			System.out.println(email);
			UserAccount user = userAccountDAO.getUserAccountByEmail(email);
			if (user == null) {
				res.setMessage("Can not find the user. Please register first.");
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				return res;
			} else {
				if (user.isEnabled()) {
					res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
					res.setType(Response.Status.BAD_REQUEST.name());
					res.setMessage("This user is already verified. Please login.");
					return res;
				}
				// 改变用户验证状态
				if (userAccountDAO.userActive(user.getUser_id(),
						user.getEmail())) {
					res.setMessage("Success. Please Login.");
					res.setCode(Response.Status.OK.getStatusCode());
					res.setType(Response.Status.OK.name());
					return res;
				}
				res.setCode(Response.Status.INTERNAL_SERVER_ERROR
						.getStatusCode());
				res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
				res.setMessage("Server busy. Please try again.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setMessage(e.getMessage());
			res.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
			return res;
		}

	}

	// 发送用户邮箱确认连接
	@POST
	@Path("/resendRegistrationToken")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg sendConfirmEmail(@FormParam("email") String email) {
		ResMsg res = new ResMsg();
		// 检查用户是否存在
		UserAccount user = userAccountDAO.getUserAccountByEmail(email);
		if (user == null) {
			res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
			res.setType(Response.Status.BAD_REQUEST.name());
			res.setMessage("Can't find this user email: " + email);
			return res;
		}
		if (user.isEnabled()) {
			res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
			res.setType(Response.Status.BAD_REQUEST.name());
			res.setMessage("This email is already actived.");
			return res;
		}
		// 组装确认链接
		String hostURL = urlInfo.getBaseUri().toASCIIString();
		String url;
		try {
			url = hostURL
					+ "user/regitrationConfirm?token="
					+ Tools.DESencrypt(email,
							ConfigValues.secureKey_verification);
			System.out.println(url);
			Tools.sendVerificationEmail(email, url);
			res.setMessage("Send email to " + email
					+ " success, please check in your email.");
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
			res.setMessage(e.getLocalizedMessage());
			return res;
		}

	}

	// 用户登录
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg userLogin(@FormParam("email") String email,
			@FormParam("password") String password) {
		System.out.println("login " + email + "<>" + password);
		ResMsg res = new ResMsg();
		UserDetail ud = new UserDetail();
		String encriptPassword;
		try {
			encriptPassword = Tools.HmacSHA256Encrypt(Tools.MD5(password),
					ConfigValues.secureKey_savepassword);
			UserAccount user = userAccountDAO.getUserAccountByEmail(email);
			if (user == null) {
				System.out.println("没有此用户");
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Username or password wrong. Please try again.");
				return res;
			}
			if (!user.isEnabled()) {
				System.out.println("账户没有激活");
				res.setCode(Response.Status.FORBIDDEN.getStatusCode());
				res.setType(Response.Status.FORBIDDEN.name());
				res.setMessage("Please check your email and active your account.");
				return res;
			}
			if (!user.getPassword().equals(encriptPassword)) {
				System.out.println("密码错误:" + user.getPassword() + " <> "
						+ encriptPassword);
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Username or password wrong. Please try again.");
				return res;
			}
			// 更新用户的Token和Token过期时间
			String token = userAccountDAO.updateToken(user.getUser_id());
			if (!"".equals(token)) {
				ud = userAccountDAO.getUserDetailByUserId(user.getUser_id());
				res.setCode(Response.Status.OK.getStatusCode());
				res.setType(Response.Status.OK.name());
				res.setMessage(token);
				res.setObj(ud);
				return res;
			} else {
				res.setCode(Response.Status.INTERNAL_SERVER_ERROR
						.getStatusCode());
				res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
				res.setMessage("Server busy. Pleasy try again.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	// 更改密码
	@POST
	@Path("/resetPassword")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg resetPassword(@FormParam("token") String token,
			@FormParam("newpassword") String newPassword) {
		ResMsg res = new ResMsg();
		try {
			String[] info = Tools.DESdecrypt(token,
					ConfigValues.secureKey_resetpassword).split(",");
			String user_GUID = info[0];
			Long resetTime = Long.parseLong(info[1]);
			long userID = Long.parseLong(user_GUID);
			// 5分钟后过期
			if ((new Date().getTime() - resetTime) < 300000) {
				UserAccount user = userAccountDAO
						.getUserAccountByUserID(userID);
				if (user == null) {
					res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
					res.setType(Response.Status.BAD_REQUEST.name());
					res.setMessage("Can not fine the user.");
					return res;
				}
				if (!user.isEnabled()) {
					res.setCode(Response.Status.FORBIDDEN.getStatusCode());
					res.setType(Response.Status.FORBIDDEN.name());
					res.setType("error");
					res.setMessage("Please active your account first.");
					return res;
				}
				String encriptPassword = Tools.HmacSHA256Encrypt(
						Tools.MD5(newPassword),
						ConfigValues.secureKey_savepassword);
				// 存入数据库
				if (userAccountDAO.changePassword(userID, encriptPassword)) {
					res.setCode(Response.Status.OK.getStatusCode());
					res.setType(Response.Status.OK.name());
					res.setMessage("Reset password successed. Please login.");
					res.setObj(urlInfo.getBaseUri().toString() + "login.html");
					return res;
				} else {
					res.setCode(Response.Status.INTERNAL_SERVER_ERROR
							.getStatusCode());
					res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
					res.setMessage("Server busy. Please try again.");
					return res;
				}
			} else {
				res.setCode(Response.Status.FORBIDDEN.getStatusCode());
				res.setType(Response.Status.FORBIDDEN.name());
				res.setMessage("Reset url out of date, please reset again.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	// 发送更改密码确认链接
	@POST
	@Path("/sendresetpwdmail")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg sendPasswordResetMail(@FormParam("email") String email) {
		ResMsg res = new ResMsg();
		String token;
		String baseURL = urlInfo.getBaseUri().toASCIIString();
		try {
			// 首先先检查邮件地址是否为已注册用户
			UserAccount user = userAccountDAO.getUserAccountByEmail(email);
			if (user == null) {
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Can not fine the user.");
				return res;
			}
			if (!user.isEnabled()) {
				res.setCode(Response.Status.FORBIDDEN.getStatusCode());
				res.setType(Response.Status.FORBIDDEN.name());
				res.setMessage("Please verify your email and login again.");
				return res;
			}
			token = Tools.DESencrypt(
					"" + user.getUser_id() + "," + new Date().getTime(),
					ConfigValues.secureKey_resetpassword);
			System.out.println(token + " <> " + baseURL);
			Tools.sendResetPasswordEmail(email, token, baseURL);
			System.out.println(token);
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Send reset mail success.");
			return res;

		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
			res.setMessage(e.getMessage());
			return res;
		}
	}

	// 用户登出
	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg userLogout(@FormParam("userID") long userID,
			@FormParam("token") String token) {
		ResMsg res = new ResMsg();
		try {
			// System.out.println(userID + " <> " + token);
			res = tokenCheck(userID, token);
			if (res.getCode() != 200) {
				return res;
			}
			ArrayList<Role> userRoles = userAccountDAO
					.getUserRolesByUserId(userID);
			// 检察权限,若权限不足则返回错误信息
			if (!userRoles.contains(new Role(RequreRole))) {
				res.setCode(Response.Status.FORBIDDEN.getStatusCode());
				res.setType(Response.Status.FORBIDDEN.name());
				res.setMessage("Primition decline.");
				return res;
			}
			// 将Token重置
			userAccountDAO.tokenReset(userID);
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Logout success.");
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	// 获取用户资料，默认获取自己的
	@GET
	@Path("/getuserdetail")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg getUserDetailByEmail(
			@QueryParam("userID") long userID,
			@QueryParam("token") String token,
			@QueryParam("other_userID") @DefaultValue("0") long other_userID,
			@QueryParam("other_user_email") @DefaultValue("") String other_user_email) {
		ResMsg res = new ResMsg();
		try {
			// System.out.println(userID + " <> " + token);
			if (!userAccountDAO.tokenCheck(userID, token)) {
				res.setCode(Response.Status.FORBIDDEN.getStatusCode());
				res.setType(Response.Status.FORBIDDEN.name());
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			ArrayList<Role> userRoles = userAccountDAO
					.getUserRolesByUserId(userID);
			// 检察权限,若权限不足则返回错误信息
			if (!userRoles.contains(new Role(RequreRole))) {
				res.setCode(Response.Status.FORBIDDEN.getStatusCode());
				res.setType(Response.Status.FORBIDDEN.name());
				res.setMessage("Primition decline.");
				return res;
			}
			// 如果是查询别人的信息
			if (!"".equals(other_user_email) || other_userID != 0) {
				// 查询别人的信息是否需要管理员权限？以后再做修改
				if (!userRoles.contains(new Role(RequreRole))) {
					res.setCode(Response.Status.FORBIDDEN.getStatusCode());
					res.setType(Response.Status.FORBIDDEN.name());
					res.setMessage("Primition decline.");
					return res;
				} else {
					if (other_userID != 0) {
						res.setObj(userAccountDAO
								.getUserDetailByUserId(other_userID));
					} else {
						res.setObj(userAccountDAO
								.getUserDetailByUserId(userAccountDAO
										.getUserIDbyEmail(other_user_email)));
					}
				}
			} else {
				// 查询自己的详细信息
				res.setObj(userAccountDAO.getUserDetailByUserId(userID));
			}
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Get user details success.");
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			res.setType(Response.Status.INTERNAL_SERVER_ERROR.name());
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}
	// 用户资料修改

}
