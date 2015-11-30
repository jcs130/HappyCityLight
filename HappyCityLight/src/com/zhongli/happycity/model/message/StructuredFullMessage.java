package com.zhongli.happycity.model.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 格式化之后的完整的数据库中的一行数据
 * 
 * @author zhonglili
 *
 */
public class StructuredFullMessage {
	// 主键
	private int num_id;
	// 消息在缓存数据库中的ID
	private String MongoId;
	// 消息原来的编号
	private String raw_id_str;
	// 创建时间(GMT)
	private Date creat_at;
	SimpleDateFormat sdf;
	// 时间戳
	private long timestamp_ms;
	// 消息内容
	private String text;
	// 媒体类型
	private List<String> media_type = new ArrayList<String>();
	// 媒体地址
	private List<String> media_urls = new ArrayList<String>();
	// 国家，省，城市，坐标区域等
	private String placeType = "null";
	private String placeName = "null";
	private String placeFullName = "null";
	private String placeBoundingType = "null";
	private List<Double[]> placeCoordinates = new ArrayList<Double[]>();
	private String country = "null";
	private String province = "null";
	private String city = "null";
	private String geo_type = "null";
	private List<Double[]> geo_coordinates = new ArrayList<Double[]>();
	// HT标签
	private List<String> hashtags = new ArrayList<String>();
	// 转发的消息的原编号
	private String replay_to = "null";
	// 语言
	private String lang = "null";
	// 文字人工标记情感
	private String emotion_text_human = "null";
	// 文字人工标记次数
	private int emotion_text_human_times;
	// 文字人工标记可信度
	private double emotion_text_human_confidence;
	// 媒体人工标记情感
	private List<String> emotion_media_human;
	// 媒体人工标记次数
	private int emotion_media_human_times;
	// 媒体人工标记可信度
	private List<Double> emotion_media_human_confidence;
	// 文字机器标记情感
	private String emotion_text_machine;
	// 媒体机器标记情感
	private List<Double> emotion_media_machine;
	// 来源
	private String messageFrom;

	public StructuredFullMessage() {
		super();
		sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public int getNum_id() {
		return num_id;
	}

	public void setNum_id(int num_id) {
		this.num_id = num_id;
	}

	public String getRaw_id_str() {
		return raw_id_str;
	}

	public void setRaw_id_str(String raw_id_str) {
		this.raw_id_str = raw_id_str;
	}

	public Date getCreat_at() {
		return creat_at;
	}

	public void setCreat_at(Date creat_at) {
		this.creat_at = creat_at;
	}

	public long getTimestamp_ms() {
		return timestamp_ms;
	}

	public void setTimestamp_ms(long timestamp_ms) {
		this.timestamp_ms = timestamp_ms;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getMedia_type() {
		return media_type;
	}

	public void setMedia_type(List<String> media_type) {
		this.media_type = media_type;
	}

	public List<String> getMedia_urls() {
		return media_urls;
	}

	public void setMedia_urls(List<String> media_urls) {
		this.media_urls = media_urls;
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

	public String getEmotion_text_human() {
		return emotion_text_human;
	}

	public void setEmotion_text_human(String emotion_text_human) {
		this.emotion_text_human = emotion_text_human;
	}

	public int getEmotion_text_human_times() {
		return emotion_text_human_times;
	}

	public void setEmotion_text_human_times(int emotion_text_human_times) {
		this.emotion_text_human_times = emotion_text_human_times;
	}

	public double getEmotion_text_human_confidence() {
		return emotion_text_human_confidence;
	}

	public void setEmotion_text_human_confidence(
			double emotion_text_human_confidence) {
		this.emotion_text_human_confidence = emotion_text_human_confidence;
	}

	public List<String> getEmotion_media_human() {
		return emotion_media_human;
	}

	public void setEmotion_media_human(List<String> emotion_media_human) {
		this.emotion_media_human = emotion_media_human;
	}

	public int getEmotion_media_human_times() {
		return emotion_media_human_times;
	}

	public void setEmotion_media_human_times(int emotion_media_human_times) {
		this.emotion_media_human_times = emotion_media_human_times;
	}

	public List<Double> getEmotion_media_human_confidence() {
		return emotion_media_human_confidence;
	}

	public void setEmotion_media_human_confidence(
			List<Double> emotion_media_human_confidence) {
		this.emotion_media_human_confidence = emotion_media_human_confidence;
	}

	public String getEmotion_text_machine() {
		return emotion_text_machine;
	}

	public void setEmotion_text_machine(String emotion_text_machine) {
		this.emotion_text_machine = emotion_text_machine;
	}

	public List<Double> getEmotion_media_machine() {
		return emotion_media_machine;
	}

	public void setEmotion_media_machine(List<Double> emotion_media_machine) {
		this.emotion_media_machine = emotion_media_machine;
	}

	public String getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	public String getMongoId() {
		return MongoId;
	}

	public void setMongoId(String mongoId) {
		MongoId = mongoId;
	}

	public String getGeo_type() {
		return geo_type;
	}

	public void setGeo_type(String geo_type) {
		this.geo_type = geo_type;
	}

	public List<Double[]> getGeo_coordinates() {
		return geo_coordinates;
	}

	public void setGeo_coordinates(List<Double[]> geo_coordinates) {
		this.geo_coordinates = geo_coordinates;
	}

	public String getPlaceType() {
		return placeType;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getPlaceFullName() {
		return placeFullName;
	}

	public void setPlaceFullName(String placeFullName) {
		this.placeFullName = placeFullName;
	}

	@Override
	public String toString() {
		return "SQLmessage [num_id=" + num_id + ", MongoId=" + MongoId
				+ ", raw_id_str=" + raw_id_str + ", creat_at="
				+ sdf.format(creat_at) + ", timestamp_ms=" + timestamp_ms
				+ ", text=" + text + ", media_type=" + media_type
				+ ", media_urls=" + media_urls + ", placeType=" + placeType
				+ ", placeName=" + placeName + ", placeFullName="
				+ placeFullName + ", placeBoundingType=" + placeBoundingType
				+ ", placeCoordinates=" + placeCoordinates + ", country="
				+ country + ", province=" + province + ", city=" + city
				+ ", geo_type=" + geo_type + ", geo_coordinates="
				+ geo_coordinates + ", hashtags=" + hashtags + ", replay_to="
				+ replay_to + ", lang=" + lang + ", emotion_text_human="
				+ emotion_text_human + ", emotion_text_human_times="
				+ emotion_text_human_times + ", emotion_text_human_confidence="
				+ emotion_text_human_confidence + ", emotion_media_human="
				+ emotion_media_human + ", emotion_media_human_times="
				+ emotion_media_human_times
				+ ", emotion_media_human_confidence="
				+ emotion_media_human_confidence + ", emotion_text_machine="
				+ emotion_text_machine + ", emotion_media_machine="
				+ emotion_media_machine + ", messageFrom=" + messageFrom + "]";
	}

	public List<Double[]> getPlaceCoordinates() {
		return placeCoordinates;
	}

	public void setPlaceCoordinates(List<Double[]> placeCoordinates) {
		this.placeCoordinates = placeCoordinates;
	}

	public String getPlaceBoundingType() {
		return placeBoundingType;
	}

	public void setPlaceBoundingType(String placeBoundingType) {
		this.placeBoundingType = placeBoundingType;
	}

}
