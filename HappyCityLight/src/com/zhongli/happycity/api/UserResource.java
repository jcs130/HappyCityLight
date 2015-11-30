package com.zhongli.happycity.api;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.zhongli.happycity.model.message.ResMsg;

@Path("/user")
public class UserResource {
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

	@Path("/registration")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg registerUserAccount(@QueryParam("email") String email,
			@QueryParam("password") String password) {
		ResMsg res = new ResMsg();
		return res;
	}

	// 确认用户邮箱
	// 用户邮箱确认连接
	// 更改密码
	// 更改密码确认链接
	// 用户登录
	// 用户资料修改

	// @Path("/regitrationConfirm")

	// user activation - verification

	// "/user/resendRegistrationToken", method = RequestMethod.GET)

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
