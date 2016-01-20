package com.zhongli.happycity.api;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.zhongli.happycity.model.message.ResMsg;
import com.zhongli.happycity.model.message.StructuredFullMessage;
import com.zhongli.happycity.tool.Tools;

//即时显示界面的API
public class messageOnMapResource {
	// 设置缓存数据的大小
	private static int CACHE_NUMBER = 200;
	// 用于存储缓存数据的队列
	private static Queue<StructuredFullMessage> cache_messages = new ArrayBlockingQueue<StructuredFullMessage>(
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
			@FormParam("hashtags") String hashtags,
			@FormParam("replay_to") String replay_to,
			@FormParam("message_from") String message_from) {
		ResMsg res = new ResMsg();
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
		msg.setHashtags(Tools.buildListFromString(hashtags));
		msg.setReplay_to(replay_to);
		msg.setMessage_from(message_from);
		// 将该信息保存到缓存队列中
		if (cache_messages.size() < CACHE_NUMBER) {
			// 如果缓存大小小于规定值，则直接存储
			cache_messages.add(msg);
		} else {
			// 移除队列头部的元素，然后增加新元素
			cache_messages.remove();
			cache_messages.add(msg);
		}
		// 将该数据存储到数据库?
		res.setCode(Response.Status.OK.getStatusCode());
		res.setType(Response.Status.OK.name());
		res.setMessage("Upload Success.");
		return res;
	}
	
	// 从缓存中获得按照指定要求筛选的数据
}
