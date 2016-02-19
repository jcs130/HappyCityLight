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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.citydigitalpulse.webservice.dao.MarkingMessageDAO;
import com.citydigitalpulse.webservice.dao.UserAccountDAO;
import com.citydigitalpulse.webservice.dao.impl.MarkingMessageDAOimpl;
import com.citydigitalpulse.webservice.dao.impl.userAccountDAOimpl;
import com.citydigitalpulse.webservice.model.message.*;
import com.citydigitalpulse.webservice.model.user.Role;

/**
 * 用于语料标注界面
 * 
 * @author zhonglili
 *
 */
@Path("/annotation")
public class MarkPageResource {
	private static MarkingMessageDAO msgDAO = new MarkingMessageDAOimpl(20, 3);
	private static UserAccountDAO userAccountDAO = new userAccountDAOimpl();
	private static String RequreRole = "ROLE_USER";

	@GET
	@Path("/getnewmessage")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg getUnMarkedMessage(
			@QueryParam("userID") long userID,
			@QueryParam("token") String token,
			@QueryParam("user_email") @DefaultValue("") String user_email,
			@QueryParam("lang") @DefaultValue("en") String languages,
			@QueryParam("annotate_part") @DefaultValue("word_and_media") String annotate_part) {
		ResMsg res = new ResMsg();
		try {
			System.out.println(userID + " <> " + token);
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
			ArrayList<String> lang = new ArrayList<>();
			String[] tmp = languages.split(",");
			for (int i = 0; i < tmp.length; i++) {
				lang.add(tmp[i]);
			}
			System.out.println("user:" + user_email + "lang:" + lang
					+ "annotate_part" + annotate_part);

			// 连接数据库获得数据
			MarkMsg2Web ma = msgDAO.getOneNewMsg();
			System.out.println("return:" + ma);
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Get new unmarked message success.");
			res.setObj(ma);
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

	// 向标注记录表更新数据
	@POST
	@Path("/sendannotatedmessage")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg sendAnnotatedMessage(
			@FormParam("userID") long userID,
			@FormParam("token") String token,
			@FormParam("user_email") @DefaultValue("null") String user_email,
			@FormParam("msg_id") @DefaultValue("0") long msg_id,
			@FormParam("text_emotion") @DefaultValue("null") String text_emotion,
			@FormParam("media_emotions") @DefaultValue("null") String media_emotions) {
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
			ArrayList<String> medias = new ArrayList<>();
			String[] tmp = media_emotions.split(",");
			for (int i = 0; i < tmp.length; i++) {
				medias.add(tmp[i]);
			}
			msgDAO.recordForMessage(userID, msg_id, text_emotion, medias);
			// 连接数据库更新数据，将标注记录加入到标注记录数据库
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Mark success");
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
	@Path("/getrecentannotation")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg getRecentMarkedMsg(@QueryParam("userID") long userID,
			@QueryParam("token") String token) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDAO.tokenCheck(userID, token)) {
				res.setCode(Response.Status.FORBIDDEN.getStatusCode());
				res.setType(Response.Status.FORBIDDEN.name());
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDAO.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(Response.Status.FORBIDDEN.getStatusCode());
				res.setType(Response.Status.FORBIDDEN.name());
				res.setMessage("Primition decline.");
				return res;
			}

			ArrayList<MarkRecordObj> recent = msgDAO
					.getRecentRecords(4, userID);
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Get recent mark success.");
			res.setObj(recent);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printThrowable(e);
			res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
			res.setType(Response.Status.BAD_REQUEST.name());
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	@GET
	@Path("/getrecordcount")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg getMarkedMsgNumber(@QueryParam("userID") long userID,
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
			int count = msgDAO.getRecordCount(userID);
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setObj(count);
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
	@Path("/getallrecord")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg getAllMarkedMsg(@QueryParam("userID") long userID,
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
			ArrayList<MarkRecordObj> all = msgDAO.getRecentRecords(0, userID);
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setObj(all);
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
	@Path("/getnewmessage2")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg getUnMarkedMessage2(@QueryParam("userID") long userID,
			@QueryParam("token") String token,
			@QueryParam(value = "number") @DefaultValue("20") int number) {
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
			// System.out.println(user);
			// 连接数据库获得数据
			ArrayList<MarkMsg2Web> ma = new ArrayList<>();
			for (int i = 0; i < number; i++) {
				ma.add(msgDAO.getOneNewMsg());
				System.out.println("return:" + msgDAO.getOneNewMsg());
			}
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setObj(ma);
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
	@Path("/sendannotatedmessage2")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ResMsg getMarkedMessage2(
			@QueryParam("userID") long userID,
			@QueryParam("token") String token,
			@QueryParam(value = "ids") @DefaultValue("null") String ids,
			@QueryParam(value = "emotions") @DefaultValue("null") String emotions) {
		// System.out.println(user);
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
			// System.out.println("我要获得"+num+"条数据");
			ArrayList<String> id = new ArrayList<>();
			ArrayList<String> emotion = new ArrayList<>();
			String[] tmpids = ids.split(",");
			String[] tmpemo = emotions.split(",");
			for (int i = 0; i < tmpids.length; i++) {
				id.add(tmpids[i]);
				emotion.add(tmpemo[i]);
				System.out.println("【id】" + tmpids[i] + " 【emotion】"
						+ tmpemo[i]);
			}
			// 连接数据库更新数据，将标注记录加入到标注记录数据库
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
}
