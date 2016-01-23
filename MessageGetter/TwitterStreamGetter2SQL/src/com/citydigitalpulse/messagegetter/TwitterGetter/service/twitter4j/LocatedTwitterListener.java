package com.citydigitalpulse.messagegetter.TwitterGetter.service.twitter4j;

import java.util.ArrayList;
import java.util.HashMap;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.RawStreamListener;

import com.citydigitalpulse.messagegetter.TwitterGetter.app.Config;
import com.citydigitalpulse.messagegetter.TwitterGetter.dao.TwitterSaveDAO;
import com.citydigitalpulse.messagegetter.TwitterGetter.model.StructuredFullMessage;
import com.citydigitalpulse.messagegetter.TwitterGetter.tool.Tools;

/**
 * 监听API获得的数据
 * 
 * @author zhonglili
 *
 */
public class LocatedTwitterListener implements RawStreamListener {

	private TwitterSaveDAO db;
	private HashMap<Long, StructuredFullMessage> cacheMessages;
	private ArrayList<StructuredFullMessage> writeList;
	private int CACHE_NUM = 1000;

	public LocatedTwitterListener(TwitterSaveDAO db) {
		this.db = db;
		this.cacheMessages = new HashMap<Long, StructuredFullMessage>();
		this.writeList = new ArrayList<StructuredFullMessage>();
	}

	@Override
	public void onException(Exception arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(String rawString) {
		StructuredFullMessage msg;
		// System.out.println(rawString);
		try {
			msg = rawTwet2StructuredMsg(rawString);
			// 每次插入一条消息
			// db.insert(msg);
			// 每次插入多条消息
			// cacheMessages.put(Long.parseLong(msg.getRaw_id_str()), msg);
			// if (cacheMessages.size() > CACHE_NUM) {
			// writeList.addAll(cacheMessages.values());
			// db.insert(writeList);
			// writeList.clear();
			// }
			// 向静态区域插入一条数据
			if (!Tools.cacheUpdateMessages.containsKey(Long.parseLong(msg
					.getRaw_id_str()))) {
				Tools.cacheUpdateMessages.put(
						Long.parseLong(msg.getRaw_id_str()), msg);
				if (Tools.cacheUpdateMessages.size() > CACHE_NUM) {
					Tools.cacheUpdateMessages.clear();
				}
				db.insert(msg);
				
				if (msg.isReal_location()) {
					System.out.println(msg.getText());
					// 发送有具体坐标的数据
					Tools.sendNewMessage(Config.DCI_SERVER_URL
							+ "messageonmap/uploadnewmessage",
							Config.UPLOAD_TOKEN, msg);
				}
			}

		} catch (JSONException e) {
			System.out.println("String:" + rawString);
			e.printStackTrace();
		}
	}

	private StructuredFullMessage rawTwet2StructuredMsg(String rawJSONString)
			throws JSONException {
//		System.out.println(rawJSONString);
		JSONObject cur = new JSONObject(rawJSONString);
		JSONObject tmp;
		StructuredFullMessage msg = new StructuredFullMessage();
		msg.setRaw_id_str(cur.getString("id_str"));
		msg.setUser_name(cur.getJSONObject("user").getString("name"));

		msg.setCreat_at(Long.parseLong(cur.getString("timestamp_ms")));
		// 取得回复对象
		if (cur.getString("in_reply_to_status_id_str") != null) {
			msg.setReplay_to(cur.getString("in_reply_to_status_id_str"));
		}
		// 内容
		msg.setText(cur.getString("text"));
		// 转发
		if (cur.getString("in_reply_to_status_id_str") != null) {
			msg.setReplay_to(cur.getString("in_reply_to_status_id_str"));
			// System.out.println("\n\n\n\n"+m.getReplay_to());
		}
		// 坐标
		if (!cur.get("geo").toString().equals("null")) {
			// System.out.println(cur.get("geo").toString());
			tmp = cur.getJSONObject("geo");
			// 如果有具体坐标存储具体坐标
			String type = tmp.getString("type");
			// System.out.println(type);
			if (type.toLowerCase().equals("point")) {
				JSONArray geo = tmp.getJSONArray("coordinates");
				msg.setQuery_location_latitude(geo.getDouble(0));
				msg.setQuery_location_langtitude(geo.getDouble(1));
				msg.setReal_location(true);
				// System.out.println(geo);
			} else if (type.toLowerCase().equals("polygon")) {
				JSONArray geo = tmp.getJSONArray("coordinates");
				int times = 0;
				double lat = 0, lan = 0;
				for (int i = 0; i < geo.length(); i++) {
					for (int j = 0; j < geo.getJSONArray(i).length(); j++) {
						lat += geo.getJSONArray(i).getJSONArray(j).getDouble(1);
						lan += geo.getJSONArray(i).getJSONArray(j).getDouble(0);
						times++;
					}
				}
				msg.setQuery_location_latitude(lat / (double) times);
				msg.setQuery_location_langtitude(lan / (double) times);
				msg.setReal_location(false);
			} else {
				// System.out.println("ttttttttt:" + type);
			}

		}
		// 地点
		if (!cur.get("place").toString().equals("null")) {
			tmp = cur.getJSONObject("place");
			String place_type = tmp.getString("place_type");
			msg.setPlace_type(place_type);
			String name = tmp.getString("name");
			msg.setPlace_name(name);
			String full_name = tmp.getString("full_name");
			msg.setPlace_fullname(full_name);
			String country = tmp.getString("country");
			// 根据type来生成不同的值
			if (place_type.equals("city")) {
				msg.setCity(name);
				msg.setProvince(full_name.split(",")[1].trim());
				msg.setCountry(country);
			} else if (place_type.equals("admin")) {
				msg.setProvince(name);
				msg.setCountry(country);
			} else {
				msg.setCountry(country);
			}
			// 通过边界信息获得中心点坐标
			JSONObject bounding_box = tmp.getJSONObject("bounding_box");
			if (bounding_box != null) {
				if (msg.getQuery_location_latitude() == 0
						|| msg.getQuery_location_langtitude() == 0) {
					String type = bounding_box.getString("type");
					// System.out.println(type);
					if (type.toLowerCase().equals("point")) {
						JSONArray geo = bounding_box
								.getJSONArray("coordinates");
						msg.setQuery_location_latitude(geo.getDouble(0));
						msg.setQuery_location_langtitude(geo.getDouble(1));
						msg.setReal_location(true);
						// System.out.println(geo);
					} else if (type.toLowerCase().equals("polygon")) {
						JSONArray geo = bounding_box
								.getJSONArray("coordinates");
						int times = 0;
						double lat = 0, lan = 0;
						for (int i = 0; i < geo.length(); i++) {
							for (int j = 0; j < geo.getJSONArray(i).length(); j++) {
								lat += geo.getJSONArray(i).getJSONArray(j)
										.getDouble(1);
								lan += geo.getJSONArray(i).getJSONArray(j)
										.getDouble(0);
								times++;
							}
						}
						msg.setQuery_location_latitude(lat / (double) times);
						msg.setQuery_location_langtitude(lan / (double) times);
						msg.setReal_location(false);
					} else {
						// System.out.println("ttttttttt:" + type);
					}
				}
			}
		}

		// 实体
		if (cur.has("entities")) {
			if (!cur.get("entities").toString().equals("null")) {
				tmp = cur.getJSONObject("entities");
				// hashtags
				JSONArray hashtags = tmp.getJSONArray("hashtags");
				if (hashtags != null) {
					for (int i = 0; i < hashtags.length(); i++) {
						JSONObject ht = hashtags.getJSONObject(i);
						if (msg.getHashtags() == null) {
							msg.setHashtags(new ArrayList<String>());
						}
						msg.getHashtags().add(ht.getString("text"));
					}
				}
				// media
				if (tmp.has("media")) {
					if (!tmp.get("media").toString().equals("null")) {
						JSONArray medias = tmp.getJSONArray("media");
						for (int i = 0; i < medias.length(); i++) {
							JSONObject media = medias.getJSONObject(i);
							if (msg.getMedia_urls() == null) {
								msg.setMedia_urls(new ArrayList<String>());
							}
							if (msg.getMedia_type() == null) {
								msg.setMedia_type(new ArrayList<String>());
							}
							if (!msg.getMedia_urls().contains(
									media.getString("media_url"))) {
								msg.getMedia_urls().add(
										media.getString("media_url"));
								msg.getMedia_type()
										.add(media.getString("type"));
							}
						}
					}
				}
			}
		}
		// 扩展实体
		if (cur.has("extended_entities")) {
			if (!cur.get("extended_entities").toString().equals("null")) {
				tmp = cur.getJSONObject("extended_entities");
				// hashtags
				if (tmp.has("hashtags")) {
					JSONArray hashtags = tmp.getJSONArray("hashtags");
					if (hashtags != null) {
						for (int i = 0; i < hashtags.length(); i++) {
							JSONObject ht = hashtags.getJSONObject(i);
							if (msg.getHashtags() == null) {
								msg.setHashtags(new ArrayList<String>());
							}
							msg.getHashtags().add(ht.getString("text"));
						}
					}
				}
				// media
				if (tmp.has("media")) {
					if (!tmp.get("media").toString().equals("null")) {
						JSONArray medias = tmp.getJSONArray("media");
						for (int i = 0; i < medias.length(); i++) {
							JSONObject media = medias.getJSONObject(i);
							if (msg.getMedia_urls() == null) {
								msg.setMedia_urls(new ArrayList<String>());
							}
							if (msg.getMedia_type() == null) {
								msg.setMedia_type(new ArrayList<String>());
							}
							// 获得媒体类型
							String mediaType = media.getString("type");
							// 如果和以前的重复则跳过
							if (msg.getMedia_urls().contains(
									media.getString("media_url"))
									&& mediaType.equals("photo")) {
								continue;
							}
							msg.getMedia_type().add(mediaType);
							if (mediaType.equals("photo")) {
								msg.getMedia_urls().add(
										media.getString("media_url"));
							} else {
								if (media.has("video_info")) {
									if (!media.getJSONObject("video_info")
											.toString().equals("null")) {
										JSONObject video_info = media
												.getJSONObject("video_info");
										// 提取视频连接
										JSONArray variants = video_info
												.getJSONArray("variants");
										int maxBitrateIndex = 0;
										int maxBitrate = 0;
										for (int j = 0; j < variants.length(); j++) {
											// 获取bitrate最高的并且格式为mp4的视频的url
											String content_type = variants
													.getJSONObject(j)
													.getString("content_type");
											// System.out.println("content_type"
											// + content_type);
											if (content_type
													.equals("video/mp4")) {
												int bitrate = variants
														.getJSONObject(j)
														.getInt("bitrate");
												if (bitrate > maxBitrate) {
													maxBitrateIndex = j;
												}
											}
										}
										// 将bitrate最大的MP4格式了url保存
										msg.getMedia_urls().add(
												variants.getJSONObject(
														maxBitrateIndex)
														.getString("url"));

									}
								}
							}
						}
					}
				}
			}
		}
		// 语言
		msg.setLang(cur.getString("lang"));
		msg.setMessage_from("twitter");
		// System.out.println(msg);
		return msg;
	}
}
