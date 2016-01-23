package com.citydigitalpulse.webservice.model.message;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于标注的数据的数据结构
 * 
 * @author zhonglili
 *
 */
public class MarkMessage {
	// 标注数据库中的编号
	private long msg_id;
	// 文字消息
	private String text;
	// 媒体url
	private String first_media_url = new String();
	// 标注次数
	private String mark_time;
	// 文字情感
	private String emotion_text;
	// 图片情感
	private String emotion_first_media = new String();
	// 语言
	private String lang;
	// 消息来源
	private String message_from;
	public long getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(long msg_id) {
		this.msg_id = msg_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getFirst_media_url() {
		return first_media_url;
	}
	public void setFirst_media_url(String first_media_url) {
		this.first_media_url = first_media_url;
	}
	public String getEmotion_text() {
		return emotion_text;
	}
	public void setEmotion_text(String emotion_text) {
		this.emotion_text = emotion_text;
	}
	public String getEmotion_first_media() {
		return emotion_first_media;
	}
	public void setEmotion_first_media(String emotion_first_media) {
		this.emotion_first_media = emotion_first_media;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getMessage_from() {
		return message_from;
	}
	public void setMessage_from(String message_from) {
		this.message_from = message_from;
	}
	public String getMark_time() {
		return mark_time;
	}
	public void setMark_time(String mark_time) {
		this.mark_time = mark_time;
	}
	@Override
	public String toString() {
		return "MarkMessage [msg_id=" + msg_id + ", text=" + text + ", first_media_url=" + first_media_url
				+ ", mark_time=" + mark_time + ", emotion_text=" + emotion_text + ", emotion_first_media="
				+ emotion_first_media + ", lang=" + lang + ", message_from=" + message_from + "]";
	}


	
	


}
