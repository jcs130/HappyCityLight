package com.zhongli.happycity.api;

import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.zhongli.happycity.dao.UserAccountDAO;
import com.zhongli.happycity.dao.impl.userAccountDAOimpl;
import com.zhongli.happycity.model.message.ResMsg;
import com.zhongli.happycity.model.user.UserAccount;
import com.zhongli.happycity.model.user.UserDetail;
import com.zhongli.happycity.tool.Tools;

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

	// 测试方法
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg test() {
		ResMsg res = new ResMsg();
		String baseURL = urlInfo.getBaseUri().toString();
		String serverURL = baseURL.substring(0, baseURL.indexOf("api/"));
		res.setCode(200);
		res.setMessage("Hello  " + urlInfo.getPath() + " <> " + baseURL
				+ " <> " + serverURL);
		return res;

	}

	@POST
	@Path("/registration")
	@Produces(MediaType.APPLICATION_JSON)
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
					res.setCode(500);
					res.setType("error");
					System.out.println("Email already exist.");
					res.setMessage("Email already exist.");
					return res;
				}
				if (userAccountDAO.createUser(email, encriptPassword)) {
					System.out.println(sendConfirmEmail(email).getCode());
					res.setType("success");
					res.setCode(200);
					res.setMessage("Create User successful");
					return res;
				}
				res.setCode(500);
				res.setType("error");
				res.setMessage("Please try again.");
				return res;
			} else {
				res.setType("error");
				res.setCode(500);
				res.setMessage("Email format error.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(500);
			res.setType("error");
			res.setMessage("unknown" + e.getLocalizedMessage());
			return res;
		}
	}

	// 确认用户邮箱
	@GET
	@Path("/regitrationConfirm")
	@Produces(MediaType.APPLICATION_JSON)
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
				res.setCode(400);
				res.setType("error");
				return res;
			} else {
				if (user.isEnabled()) {
					res.setCode(500);
					res.setType("error");
					res.setMessage("This user is already verified. Please login.");
					return res;
				}
				// 改变用户验证状态
				if (userAccountDAO.userActive(user.getUser_id(),
						user.getEmail())) {
					res.setMessage("Success. Please Login.");
					res.setCode(200);
					res.setType("success");
					return res;
				}
				res.setCode(500);
				res.setType("error");
				res.setMessage("Server busy. Please try again.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setMessage(e.getMessage());
			res.setCode(500);
			res.setType("error");
			return res;
		}

	}

	// 发送用户邮箱确认连接
	@POST
	@Path("/resendRegistrationToken")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg sendConfirmEmail(@FormParam("email") String email) {
		ResMsg res = new ResMsg();
		// 检查用户是否存在
		UserAccount user = userAccountDAO.getUserAccountByEmail(email);
		if (user == null) {
			res.setType("error");
			res.setCode(400);
			res.setMessage("Can't find this user email: " + email);
			return res;
		}
		if (user.isEnabled()) {
			res.setType("error");
			res.setCode(401);
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
			res.setType("success");
			res.setMessage("Send email to " + email
					+ " success, please check in your email.");
			res.setCode(200);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setType("error");
			res.setCode(500);
			res.setMessage(e.getLocalizedMessage());
			return res;
		}

	}

	// 用户登录
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
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
				res.setCode(401);
				res.setType("error");
				res.setMessage("Username or password wrong. Please try again.");
				return res;
			}
			if (!user.isEnabled()) {
				System.out.println("账户没有激活");
				res.setCode(401);
				res.setType("error");
				res.setMessage("Please check your email and active your account.");
				return res;
			}
			if (!user.getPassword().equals(encriptPassword)) {
				System.out.println("密码错误:" + user.getPassword() + " <> "
						+ encriptPassword);
				res.setCode(401);
				res.setType("error");
				res.setMessage("Username or password wrong. Please try again.");
				return res;
			}
			// 更新用户的Token和Token过期时间
			String token = userAccountDAO.updateToken(user.getUser_id());
			if (!"".equals(token)) {
				ud = userAccountDAO.getUserDetailByUserId(user.getUser_id());
				res.setCode(200);
				res.setType("success");
				res.setMessage(token);
				res.setObj(ud);
				return res;
			} else {
				res.setCode(500);
				res.setType("error");
				res.setMessage("Server busy. Pleasy try again.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	// 更改密码
	@POST
	@Path("/resetPassword")
	@Produces(MediaType.APPLICATION_JSON)
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
					res.setCode(400);
					res.setType("error");
					res.setMessage("Can not fine the user.");
					return res;
				}
				if (!user.isEnabled()) {
					res.setCode(401);
					res.setType("error");
					res.setMessage("Please active your account first.");
					return res;
				}
				String encriptPassword = Tools.HmacSHA256Encrypt(
						Tools.MD5(newPassword),
						ConfigValues.secureKey_savepassword);
				// 存入数据库
				if (userAccountDAO.changePassword(userID, encriptPassword)) {
					res.setCode(200);
					res.setType("success");
					res.setMessage("Reset password successed. Please login.");
					res.setObj(urlInfo.getBaseUri().toString() + "login.html");
					return res;
				} else {
					res.setCode(500);
					res.setType("error");
					res.setMessage("Server busy. Please try again.");
					return res;
				}
			} else {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Reset url out of date, please reset again.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	// 发送更改密码确认链接
	@POST
	@Path("/sendresetpwdmail")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg sendPasswordResetMail(@FormParam("email") String email) {
		ResMsg res = new ResMsg();
		String token;
		String baseURL = urlInfo.getBaseUri().toASCIIString();
		try {
			// 首先先检查邮件地址是否为已注册用户
			UserAccount user = userAccountDAO.getUserAccountByEmail(email);
			if (user == null) {
				res.setCode(400);
				res.setType("error");
				res.setMessage("Can not fine the user.");
				return res;
			}
			if (!user.isEnabled()) {
				res.setCode(401);
				res.setMessage("Please verify your email and login again.");
				res.setType("error");
				return res;
			}
			token = Tools.DESencrypt(
					"" + user.getUser_id() + "," + new Date().getTime(),
					ConfigValues.secureKey_resetpassword);
			System.out.println(token + " <> " + baseURL);
			Tools.sendResetPasswordEmail(email, token, baseURL);
			System.out.println(token);
			res.setType("success");
			res.setCode(200);
			res.setMessage("Send reset mail success.");
			return res;

		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setType("error");
			res.setCode(500);
			res.setMessage(e.getMessage());
			return res;
		}
	}

	// 用户资料修改

}
