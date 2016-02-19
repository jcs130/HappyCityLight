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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.citydigitalpulse.webservice.dao.CollectorControllerDAO;
import com.citydigitalpulse.webservice.dao.UserAccountDAO;
import com.citydigitalpulse.webservice.dao.impl.CollectorControllerDAOimpl;
import com.citydigitalpulse.webservice.dao.impl.userAccountDAOimpl;
import com.citydigitalpulse.webservice.model.collector.RegInfo;
import com.citydigitalpulse.webservice.model.message.ResMsg;
import com.citydigitalpulse.webservice.model.user.Role;

/**
 * 用于控制消息获取程序的API
 * 
 * @author zhonglili
 *
 */
@Path("/collector")
public class CollectorControlResource {
	private static CollectorControllerDAO collectorControllerDAO = new CollectorControllerDAOimpl();
	private static UserAccountDAO userAccountDAO = new userAccountDAOimpl();
	private static String RequreRole = "ROLE_USER";

	@GET
	@Path("/getlistenplacelist")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg getListenPlaceList(@QueryParam("userID") long userID,
			@QueryParam("token") String token) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDAO.tokenCheck(userID, token)) {
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDAO.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Primition decline.");
				return res;
			}
			// 从数据库中获得监听的城市列表
			res.setObj(collectorControllerDAO.getListenPlaces());
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Get place list success.");
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

	@GET
	@Path("/getplaceinfo")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg getRegInfoByName(@QueryParam("userID") long userID,
			@QueryParam("token") String token,
			@QueryParam("place_name") @DefaultValue("") String place_name) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDAO.tokenCheck(userID, token)) {
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDAO.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Primition decline.");
				return res;
			}
			// 从数据库中获得监听的城市列表
			RegInfo reg = collectorControllerDAO.getRegInfoByName(place_name);
			if (reg == null) {
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("No place name in the system.");
			}
			res.setObj(reg);
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Get place info success.");
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

	@PUT
	@Path("/updateareabox")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg updateAreaBound(@FormParam("userID") long userID,
			@FormParam("token") String token,
			@FormParam("place_name") @DefaultValue("") String place_name,
			@FormParam("box_points") @DefaultValue("") String box_points) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDAO.tokenCheck(userID, token)) {
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDAO.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Primition decline.");
				return res;
			}
			// 从数据库中获得监听的城市列表
			RegInfo reg = collectorControllerDAO.getRegInfoByName(place_name);
			if (reg == null) {
				res.setCode(Response.Status.NOT_FOUND.getStatusCode());
				res.setType(Response.Status.NOT_FOUND.name());
				res.setMessage("No place name in the system.");
			}
			reg.setBox_points(box_points);
			collectorControllerDAO.updateRegBoxInfo(reg);
			res.setObj(reg);
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Update place info success.");
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

}
