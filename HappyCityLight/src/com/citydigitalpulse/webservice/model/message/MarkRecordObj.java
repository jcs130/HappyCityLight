package com.citydigitalpulse.webservice.model.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MarkRecordObj {
	// Record ID
	private int record_id;
	// Message ID
	private long msg_id;
	// User ID
	private long user_id;
	// Recorded date time
	private Date mark_at;
	// Text
	private String text;
	// Text of emotion
	private String emotion_text;
	// Media types
	private List<String> media_types;
	// Media urls
	private List<String> media_urls;
	// Media urls local
	private List<String> media_urls_local;
	// emotion of medias
	private List<String> emotion_medias;

	public int getRecord_id() {
		return record_id;
	}

	public void setRecord_id(int record_id) {
		this.record_id = record_id;
	}

	public long getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(long msg_id) {
		this.msg_id = msg_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getMark_at() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(mark_at);
	}

	public void setMark_at(Date mark_at) {
		this.mark_at = mark_at;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getEmotion_text() {
		return emotion_text;
	}

	public void setEmotion_text(String emotion_text) {
		this.emotion_text = emotion_text;
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

	public List<String> getEmotion_medias() {
		return emotion_medias;
	}

	public void setEmotion_medias(List<String> emotion_medias) {
		this.emotion_medias = emotion_medias;
	}

	public List<String> getMedia_urls_local() {
		return media_urls_local;
	}

	public void setMedia_urls_local(List<String> media_urls_local) {
		this.media_urls_local = media_urls_local;
	}

	@Override
	public String toString() {
		return "MarkRecordObj [record_id=" + record_id + ", msg_id=" + msg_id
				+ ", user_id=" + user_id + ", mark_at=" + mark_at + ", text="
				+ text + ", emotion_text=" + emotion_text + ", media_types="
				+ media_types + ", media_urls=" + media_urls
				+ ", media_urls_local=" + media_urls_local
				+ ", emotion_medias=" + emotion_medias + "]";
	}

}
