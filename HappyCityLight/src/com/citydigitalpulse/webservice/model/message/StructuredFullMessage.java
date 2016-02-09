package com.citydigitalpulse.webservice.model.message;

import java.util.ArrayList;
import java.util.List;

/**
 * 格式化之后的完整的数据库中的一行数据
 * 
 * @author zhonglili
 *
 */
public class StructuredFullMessage {
	// 主键
	private long num_id = 0;
	// 消息原来的编号
	private String raw_id_str = "";
	// 用户名称
	private String user_name = "";
	//用户头像
	private String profile_image_url="";
	// 创建时间(时间戳)
	private long creat_at = 0;
	// 消息内容
	private String text = "";
	// 媒体类型
	private List<String> media_types = new ArrayList<String>();
	// 媒体地址
	private List<String> media_urls = new ArrayList<String>();
	private List<String> media_urls_local = new ArrayList<String>();
	// 国家，省，城市，坐标区域等
	private String place_type = "null";
	private String place_name = "null";
	private String place_fullname = "null";
	private String country = "null";
	private String province = "null";
	private String city = "null";
	// 综合地理位置，如果有具体的位置，则直接使用原始位置，如果位置是个区域则使用大概的坐标位置
	private double query_location_latitude = 0;
	private double query_location_langtitude = 0;
	// HT标签
	private List<String> hashtags = new ArrayList<String>();
	// 转发的消息的原编号
	private String replay_to = "";
	// 语言
	private String lang = "";
	// 文字机器标记情感
	private String emotion_text = "";
	// 媒体机器标记情感
	private List<String> emotion_medias = new ArrayList<String>();
	// 总体情感
	private String emotion_all = "";
	// 来源
	private String message_from = "";
	// 是否为精确位置
	private boolean real_location = false;

	public StructuredFullMessage() {
		super();
	}

	public long getNum_id() {
		return num_id;
	}

	public void setNum_id(long num_id) {
		this.num_id = num_id;
	}

	public String getRaw_id_str() {
		return raw_id_str;
	}

	public void setRaw_id_str(String raw_id_str) {
		this.raw_id_str = raw_id_str;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public long getCreat_at() {
		return creat_at;
	}

	public void setCreat_at(long creat_at) {
		this.creat_at = creat_at;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getMedia_types() {
		return media_types;
	}

	public void setMedia_types(List<String> media_types) {
		this.media_types = media_types;
	}

	public List<String> getMedia_urls() {
		return media_urls;
	}

	public void setMedia_urls(List<String> media_urls) {
		this.media_urls = media_urls;
	}

	public List<String> getMedia_urls_local() {
		return media_urls_local;
	}

	public void setMedia_urls_local(List<String> media_urls_local) {
		this.media_urls_local = media_urls_local;
	}

	public String getPlace_type() {
		return place_type;
	}

	public void setPlace_type(String place_type) {
		this.place_type = place_type;
	}

	public String getPlace_name() {
		return place_name;
	}

	public void setPlace_name(String place_name) {
		this.place_name = place_name;
	}

	public String getPlace_fullname() {
		return place_fullname;
	}

	public void setPlace_fullname(String place_fullname) {
		this.place_fullname = place_fullname;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public double getQuery_location_latitude() {
		return query_location_latitude;
	}

	public void setQuery_location_latitude(double query_location_latitude) {
		this.query_location_latitude = query_location_latitude;
	}

	public double getQuery_location_langtitude() {
		return query_location_langtitude;
	}

	public void setQuery_location_langtitude(double query_location_langtitude) {
		this.query_location_langtitude = query_location_langtitude;
	}

	public List<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(List<String> hashtags) {
		this.hashtags = hashtags;
	}

	public String getReplay_to() {
		return replay_to;
	}

	public void setReplay_to(String replay_to) {
		this.replay_to = replay_to;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getEmotion_text() {
		return emotion_text;
	}

	public void setEmotion_text(String emotion_text) {
		this.emotion_text = emotion_text;
	}

	public List<String> getEmotion_medias() {
		return emotion_medias;
	}

	public void setEmotion_medias(List<String> emotion_medias) {
		this.emotion_medias = emotion_medias;
	}

	public String getEmotion_all() {
		return emotion_all;
	}

	public void setEmotion_all(String emotion_all) {
		this.emotion_all = emotion_all;
	}

	public String getMessage_from() {
		return message_from;
	}

	public void setMessage_from(String message_from) {
		this.message_from = message_from;
	}

	public boolean isReal_location() {
		return real_location;
	}

	public void setReal_location(boolean real_location) {
		this.real_location = real_location;
	}

	@Override
	public String toString() {
		return "StructuredFullMessage [num_id=" + num_id + ", raw_id_str="
				+ raw_id_str + ", user_name=" + user_name + ", creat_at="
				+ creat_at + ", text=" + text + ", media_types=" + media_types
				+ ", media_urls=" + media_urls + ", media_urls_local="
				+ media_urls_local + ", place_type=" + place_type
				+ ", place_name=" + place_name + ", place_fullname="
				+ place_fullname + ", country=" + country + ", province="
				+ province + ", city=" + city + ", query_location_latitude="
				+ query_location_latitude + ", query_location_langtitude="
				+ query_location_langtitude + ", hashtags=" + hashtags
				+ ", replay_to=" + replay_to + ", lang=" + lang
				+ ", emotion_text=" + emotion_text + ", emotion_medias="
				+ emotion_medias + ", emotion_all=" + emotion_all
				+ ", message_from=" + message_from + ", real_location="
				+ real_location + "]";
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

}
