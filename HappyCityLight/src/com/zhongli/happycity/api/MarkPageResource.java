package com.zhongli.happycity.api;

import java.util.ArrayList;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.zhongli.happycity.dao.MessageDAO;
import com.zhongli.happycity.dao.UserAccountDAO;
import com.zhongli.happycity.dao.impl.MessageDAOimpl;
import com.zhongli.happycity.dao.impl.userAccountDAOimpl;
import com.zhongli.happycity.model.message.*;
import com.zhongli.happycity.model.user.Role;

/**
 * 用于语料标注界面
 * 
 * @author zhonglili
 *
 */
@Path("/annotation")
public class MarkPageResource {
	private static MessageDAO msgDAO = new MessageDAOimpl(20, 3);
	private static UserAccountDAO userAccountDao = new userAccountDAOimpl();
	private static String RequreRole = "ROLE_USER";

	@GET
	@Path("/getnewmessage")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg getUnMarkedMessage(
			@QueryParam("userID") long userID,
			@QueryParam("token") String token,
			@QueryParam("user_email") @DefaultValue("null") String user_email,
			@QueryParam("lang") @DefaultValue("en") String languages,
			@QueryParam("annotate_part") @DefaultValue("word_and_media") String annotate_part) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDao.tokenCheck(userID, token)) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDao.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Primition decline.");
				return res;
			}
			// System.out.println(user);
			// System.out.println("我要获得"+num+"条数据");
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
			res.setCode(200);
			res.setType("success");
			res.setMessage("Get new unmarked message success.");
			res.setObj(ma);
			return res;

		} catch (Exception e) {
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	// 向标注记录表更新数据
	@POST
	@Path("/sendannotatedmessage")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg getMarkedMessage(
			@QueryParam("userID") long userID,
			@QueryParam("token") String token,
			@QueryParam("user_email") @DefaultValue("null") String user_email,
			@QueryParam("msg_id") @DefaultValue("0") long msg_id,
			@QueryParam("text_emotion") @DefaultValue("null") String text_emotion,
			@QueryParam("media_emotions") @DefaultValue("null") String media_emotions) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDao.tokenCheck(userID, token)) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDao.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Primition decline.");
				return res;
			}
			// System.out.println(user);
			// System.out.println("我要获得"+num+"条数据");
			ArrayList<String> medias = new ArrayList<>();
			String[] tmp = media_emotions.split(",");
			for (int i = 0; i < tmp.length; i++) {
				medias.add(tmp[i]);
			}
			msgDAO.recordForMessage(userID, msg_id, text_emotion, medias);
			// 连接数据库更新数据，将标注记录加入到标注记录数据库
			StatusMsg status = new StatusMsg(200, "Success");
			res.setCode(200);
			res.setType("success");
			res.setMessage("Mark success");
			res.setObj(status);
			return res;
		} catch (Exception e) {
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	@GET
	@Path("/getrecentannotation")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg getRecentMarkedMsg(@QueryParam("userID") long userID,
			@QueryParam("token") String token) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDao.tokenCheck(userID, token)) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDao.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Primition decline.");
				return res;
			}

			ArrayList<MarkRecordObj> recent = msgDAO
					.getRecentRecords(4, userID);
			res.setCode(200);
			res.setType("success");
			res.setMessage("Get recent mark success.");
			res.setObj(recent);
			return res;
		} catch (Exception e) {
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	@GET
	@Path("/getrecordcount")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg getMarkedMsgNumber(@QueryParam("userID") long userID,
			@QueryParam("token") String token) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDao.tokenCheck(userID, token)) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDao.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Primition decline.");
				return res;
			}
			int count = msgDAO.getRecordCount(userID);
			res.setCode(200);
			res.setType("success");
			res.setObj(count);
			return res;
		} catch (Exception e) {
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	@GET
	@Path("/getallrecord")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg getAllMarkedMsg(@QueryParam("userID") long userID,
			@QueryParam("token") String token) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDao.tokenCheck(userID, token)) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDao.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Primition decline.");
				return res;
			}
			ArrayList<MarkRecordObj> all = msgDAO.getRecentRecords(0, userID);
			res.setCode(200);
			res.setType("success");
			res.setObj(all);
			return res;
		} catch (Exception e) {
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	@GET
	@Path("/getnewmessage2")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg getUnMarkedMessage2(@QueryParam("userID") long userID,
			@QueryParam("token") String token,
			@QueryParam(value = "number") @DefaultValue("20") int number) {
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDao.tokenCheck(userID, token)) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDao.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(401);
				res.setType("error");
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
			res.setCode(200);
			res.setType("success");
			res.setObj(ma);
			return res;
		} catch (Exception e) {
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}

	@GET
	@Path("/sendannotatedmessage2")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg getMarkedMessage2(
			@QueryParam("userID") long userID,
			@QueryParam("token") String token,
			@QueryParam(value = "ids") @DefaultValue("null") String ids,
			@QueryParam(value = "emotions") @DefaultValue("null") String emotions) {
		// System.out.println(user);
		ResMsg res = new ResMsg();
		try {
			if (!userAccountDao.tokenCheck(userID, token)) {
				res.setCode(401);
				res.setType("error");
				res.setMessage("Token expaired. Please login again.");
				return res;
			}
			// 检察权限,若权限不足则返回错误信息
			if (!userAccountDao.getUserRolesByUserId(userID).contains(
					new Role(RequreRole))) {
				res.setCode(401);
				res.setType("error");
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
			StatusMsg status = new StatusMsg(200, "Success");
			res.setCode(200);
			res.setType("success");
			res.setObj(status);
			return res;
		} catch (Exception e) {
			res.setCode(500);
			res.setType("error");
			res.setMessage(e.getLocalizedMessage());
			return res;
		}
	}
}
