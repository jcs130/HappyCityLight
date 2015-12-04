package com.zhongli.happycity.api;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.zhongli.happycity.dao.UserAccountDAO;
import com.zhongli.happycity.dao.impl.userAccountDAOimpl;
import com.zhongli.happycity.model.message.ResMsg;
import com.zhongli.happycity.model.user.UserAccount;
import com.zhongli.happycity.tool.Tools;

@Path("/user")
public class UserResource {
	@Context
	private UriInfo urlInfo;
	private UserAccountDAO userAccountDAO = new userAccountDAOimpl();
	private final String secureKey_verification = "digitalcityverif";
	private final String secureKey_resetpassword = "digitalcityreset";
	private final String secureKey_savepassword = "digitalcitysavep";

	// 用户注册
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg userRegister(@QueryParam("email") String email,
			@QueryParam("password") String password) {
		ResMsg res = new ResMsg();
		res.setCode(200);
		res.setMessage("email:" + email + " password:" + password);
		return res;

	}

	@POST
	@Path("/registration")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg registerUserAccount(@QueryParam("email") String email,
			@QueryParam("password") String password) {
		ResMsg res = new ResMsg();
		try {
			// Save data to ds;
			String encriptPassword = Tools.HmacSHA256Encrypt(
					Tools.MD5(password), secureKey_savepassword);
			if (Tools.emailFormat(email)) {
				if (userAccountDAO.getUserIDbyEmail(email) != -1) {
					res.setCode(500);
					res.setType("error");
					res.setMessage("Email already exist.");
					return res;
				}
				if (userAccountDAO.createUser(email, encriptPassword)) {
					sendConfirmEmail(email);
					res.setType("success");
					res.setCode(200);
					res.setMessage("Create User successful");
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
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getMessage());
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
			String email = Tools.DESdecrypt(token, secureKey_verification);
			System.out.println(email);
			UserAccount user = userAccountDAO.getUserAccountByEmail(email);
			if (user == null) {
				res.setMessage("Can not find the user. Please register first.");
				res.setCode(400);
				res.setType("error");
				return res;
			} else {
				if (!user.isEnabled()) {
					res.setCode(500);
					res.setType("error");
					res.setMessage("This user is already verified. Please login.");
					return res;
				}
				// 改变用户验证状态
				if (userAccountDAO.userActive(user.getUser_id())) {
					res.setMessage("Success. Please Login.");
					res.setCode(200);
					res.setType("success");
				}
				res.setCode(500);
				res.setType("error");
				res.setMessage("Server busy. Please try again.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setMessage(e.getMessage());
			res.setCode(500);
			res.setType("error");
			return res;
		}

	}

	// 发送用户邮箱确认连接
	@GET
	@Path("/resendRegistrationToken")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg sendConfirmEmail(String email) {
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
			url = hostURL + "/user/regitrationConfirm?token="
					+ Tools.DESencrypt(email, secureKey_verification);
			System.out.println(url);
			Tools.sendVerificationEmail(email, url);
			res.setType("success");
			res.setMessage("Send email to " + email
					+ " success, please check in your email.");
			res.setCode(200);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
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
	public ResMsg userLogin(@QueryParam("email") String email,
			@QueryParam("password") String password) {
		ResMsg res = new ResMsg();
		String encriptPassword;
		try {
			encriptPassword = Tools.HmacSHA256Encrypt(Tools.MD5(password),
					secureKey_savepassword);
			UserAccount user = userAccountDAO.getUserAccountByEmail(email);
			if (user == null) {
				// 没有此用户
				res.setCode(401);
				res.setType("error");
				res.setMessage("Username or password wrong. Please try again.");
				return res;
			}
			if (!user.isEnabled()) {
				// 账户没有激活
				res.setCode(401);
				res.setType("error");
				res.setMessage("Please check your email and active your account.");
				return res;
			}
			if (user.getPassword() != encriptPassword) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Username or password wrong. Please try again.");
				return res;
			}
			// 更新用户的Token和Token过期时间
			if (userAccountDAO.updateToken(user.getUser_id())) {
				res.setCode(200);
				res.setType("success");
				res.setMessage("Login seccess.");
				return res;
			} else {
				res.setCode(500);
				res.setType("error");
				res.setMessage("Server busy. Pleasy try again.");
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}
	
	// 更改密码
	// 更改密码确认链接
	// 用户资料修改

	// Reset password

	// @RequestMapping(value = "/user/resetPassword", method =
	// RequestMethod.POST)

	// @RequestMapping(value = "/user/changePassword", method =
	// RequestMethod.GET)
	// return "redirect:/updatePassword.html?lang=" + locale.getLanguage();

	// @RequestMapping(value = "/user/savePassword", method =
	// RequestMethod.POST)

	// change user password

	// @RequestMapping(value = "/user/updatePassword", method =
	// RequestMethod.POST)
}
