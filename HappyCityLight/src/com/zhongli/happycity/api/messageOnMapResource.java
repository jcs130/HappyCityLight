package com.zhongli.happycity.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.utilities.reflection.Logger;

import com.zhongli.happycity.model.message.ResMsg;
import com.zhongli.happycity.model.message.StructuredFullMessage;
import com.zhongli.happycity.tool.Tools;

//即时显示界面的API
@Path("/messageonmap")
public class messageOnMapResource {
	// 设置缓存数据的大小
	private static int CACHE_NUMBER = 1000;
	// 用于存储缓存数据的队列
	private static ArrayList<StructuredFullMessage> cache_messages = new ArrayList<StructuredFullMessage>(
			CACHE_NUMBER);

	/**
	 * 用于从爬虫程序上传最新数据的API
	 * 
	 * @param token
	 * @param raw_id_str
	 * @param user_name
	 * @param text
	 * @param creat_at
	 * @param emotion_text
	 * @param media_types
	 * @param media_urls
	 * @param media_urls_local
	 * @param emotion_medias
	 * @param emotion_all
	 * @param place_type
	 * @param place_name
	 * @param place_fullname
	 * @param country
	 * @param province
	 * @param city
	 * @param query_location_latitude
	 * @param query_location_langtitude
	 * @param hashtags
	 * @param replay_to
	 * @param message_from
	 * @return
	 */
	@POST
	@Path("/uploadnewmessage")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg uploadNewMessage(
			@FormParam("token") String token,
			@FormParam("raw_id_str") String raw_id_str,
			@FormParam("user_name") String user_name,
			@FormParam("text") String text,
			@FormParam("creat_at") String creat_at,
			@FormParam("emotion_text") String emotion_text,
			@FormParam("media_types") String media_types,
			@FormParam("media_urls") String media_urls,
			@FormParam("media_urls_local") String media_urls_local,
			@FormParam("emotion_medias") String emotion_medias,
			@FormParam("emotion_all") String emotion_all,
			@FormParam("place_type") String place_type,
			@FormParam("place_name") String place_name,
			@FormParam("place_fullname") String place_fullname,
			@FormParam("country") String country,
			@FormParam("province") String province,
			@FormParam("city") String city,
			@FormParam("query_location_latitude") String query_location_latitude,
			@FormParam("query_location_langtitude") String query_location_langtitude,
			@FormParam("is_real_location") String is_real_location,
			@FormParam("hashtags") String hashtags,
			@FormParam("replay_to") String replay_to,
			@FormParam("message_from") String message_from) {
		ResMsg res = new ResMsg();
		try {
			if (!token.equals("Imagoodboy")) {
				System.out.println(token);
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Wrong Token.");
				return res;
			}
			// 根据传来的参数构建对象
			StructuredFullMessage msg = new StructuredFullMessage();
			msg.setRaw_id_str(raw_id_str);
			msg.setUser_name(user_name);
			msg.setText(text);
			msg.setCreat_at(Long.parseLong(creat_at));
			msg.setEmotion_text(emotion_text);
			msg.setMedia_type(Tools.buildListFromString(media_types));
			msg.setMedia_urls(Tools.buildListFromString(media_urls));
			msg.setMedia_urls_local(Tools.buildListFromString(media_urls_local));
			msg.setEmotion_medias(Tools.buildListFromString(emotion_medias));
			msg.setEmotion_all(emotion_all);
			msg.setPlace_type(place_type);
			msg.setPlace_name(place_name);
			msg.setPlace_fullname(place_fullname);
			msg.setCountry(country);
			msg.setProvince(province);
			msg.setCity(city);
			msg.setQuery_location_latitude(Double
					.parseDouble(query_location_latitude));
			msg.setQuery_location_langtitude(Double
					.parseDouble(query_location_langtitude));
			msg.setReal_location(Boolean.parseBoolean(is_real_location));
			msg.setHashtags(Tools.buildListFromString(hashtags));
			msg.setReplay_to(replay_to);
			msg.setMessage_from(message_from);
			// 将该信息保存到缓存队列中
			if (cache_messages.size() < CACHE_NUMBER) {
				// 如果缓存大小小于规定值，则直接存储
				cache_messages.add(msg);
				System.out.println(cache_messages.size());
			} else {
				// 移除队列头部的元素，然后增加新元素
				cache_messages.remove(0);
				cache_messages.add(msg);
			}
			// 将该数据存储到数据库?
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Upload Success.");
			System.out.println(msg.getText());
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

	// 从缓存中获得按照指定要求筛选的数据
	@GET
	@Path("/getlatest")
	@Produces(MediaType.APPLICATION_JSON)
	public ResMsg getLatest(
			@QueryParam("token") @DefaultValue("") String token,
			@QueryParam("message_from") @DefaultValue("") String message_from,
			@QueryParam("keyword") @DefaultValue("") String keyword,
			@QueryParam("hashtags") @DefaultValue("") String hashtags,
			@QueryParam("city") @DefaultValue("") String city,
			@QueryParam("location_lat_min") @DefaultValue("0") double location_lat_min,
			@QueryParam("location_lan_min") @DefaultValue("0") double location_lan_min,
			@QueryParam("location_lat_max") @DefaultValue("0") double location_lat_max,
			@QueryParam("location_lan_max") @DefaultValue("0") double location_lan_max,
			@QueryParam("lang") @DefaultValue("") String lang,
			@QueryParam("limit") @DefaultValue("1") int limit) {
		ResMsg res = new ResMsg();
		try {
			// 检查客户端Token
			if (!token.equals("ArashiArashiFordream")) {
				System.out.println(token);
				res.setCode(Response.Status.BAD_REQUEST.getStatusCode());
				res.setType(Response.Status.BAD_REQUEST.name());
				res.setMessage("Token is wrong.");
				return res;
			}
			System.out.println(cache_messages.size());
			// 得到所有的查询条件并且返回最新的符合条件的数据
			ArrayList<StructuredFullMessage> cache = new ArrayList<StructuredFullMessage>(
					CACHE_NUMBER);
			ArrayList<StructuredFullMessage> list = new ArrayList<StructuredFullMessage>();
			StructuredFullMessage temp;
			cache.addAll(cache_messages);
			for (int i = cache.size() - 1; i > 0; i--) {
				temp = cache.get(i);
				if (isMatch(temp, message_from, keyword, hashtags, city,
						location_lat_min, location_lat_max, location_lat_min,
						location_lan_max, lang)) {
					list.add(temp);
					if (list.size() >= limit) {
						break;
					}
				}
			}
			res.setObj(list);
			res.setCode(Response.Status.OK.getStatusCode());
			res.setType(Response.Status.OK.name());
			res.setMessage("Get latest Message Success.");
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

	/**
	 * 检查一条消息是否满足条件
	 * 
	 * @param temp
	 * @param message_from
	 * @param keyword
	 * @param hashtags
	 * @param city
	 * @param location_lat
	 * @param location_lan
	 * @param range
	 * @param lang
	 * @return
	 */
	private boolean isMatch(StructuredFullMessage temp, String message_from,
			String keyword, String hashtags, String city,
			double location_lat_min, double location_lat_max,
			double location_lan_min, double location_lan_max, String lang) {
		// 语言，传入对象为列表
		if (!"".equals(lang) && !"null".equals(lang)) {
			System.out.println("lang: " + lang);
			if (!isEquals(lang.trim().split(","), temp.getLang())) {
				return false;
			}
		}
		// 消息来源，传入对象为列表
		if (!"".equals(message_from) && !"null".equals(message_from)) {
			System.out.println("message_from" + message_from);
			if (!isEquals(message_from.trim().split(","),
					temp.getMessage_from())) {
				return false;
			}
		}
		// 是否包含关键字
		if (!"".equals(keyword) && !"null".equals(keyword)) {
			if (!isContains(keyword.trim().split(","), temp.getText())) {
				return false;
			}
		}
		// 是否包含Hashtag
		if (!"".equals(hashtags) && !"null".equals(hashtags)) {
			if (!isEquals(hashtags.trim().split(","), temp.getHashtags())) {
				return false;
			}
		}
		// 是否在指定区域内（名称）
		if (!"".equals(city) && !"null".equals(city)) {
			if (!(isEquals(city.trim().split(","), temp.getPlace_name()) || isEquals(
					city.trim().split(","), temp.getCity()))) {
				return false;
			}
		}
		// 是否在指定地理坐标和范围
		if (location_lat_min != 0 && location_lan_min != 0
				&& location_lat_max != 0 && location_lan_max != 0) {
			if (!temp.isReal_location()) {
				return false;
			}
			if (temp.getQuery_location_latitude() < location_lat_min
					|| temp.getQuery_location_latitude() > location_lat_max
					|| temp.getQuery_location_langtitude() < location_lan_min
					|| temp.getQuery_location_langtitude() > location_lan_max) {
				return false;
			}
		}
		// System.out.println(temp + "符合条件");
		return true;
	}

	/**
	 * 两个数组（队列）进行比较
	 * 
	 * @param split
	 * @param hashtags
	 * @return
	 */
	private boolean isEquals(String[] split, List<String> dest) {
		for (int i = 0; i < dest.size(); i++) {
			for (int j = 0; j < split.length; j++) {
				if (dest.get(i).toLowerCase().equals(split[j].toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 查询数组中是否有某元素
	 * 
	 * @param split
	 * @param message_from
	 * @return
	 */
	private boolean isContains(String[] split, String dest) {
		for (int i = 0; i < split.length; i++) {
			if (dest.toLowerCase().contains(split[i].toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 查询数组中是否有某元素
	 * 
	 * @param split
	 * @param message_from
	 * @return
	 */
	private boolean isEquals(String[] split, String dest) {
		for (int i = 0; i < split.length; i++) {
			if (dest.toLowerCase().equals(split[i].toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
