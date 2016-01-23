package com.citydigitalpulse.webservice.model.message;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * 标记用的信息
 * 
 * @author zhonglili
 *
 */
public class MarkMsg2Web {
	private long msg_id;
	private String text;
	private String lang;
	private int media_num;
	private ArrayList<MediaObject> medias;

	/**
	 * 
	 * @param markMessageObj
	 */
	public MarkMsg2Web(MarkMessageObj markMessageObj) {
		this.msg_id = markMessageObj.getMsg_id();
		this.text = markMessageObj.getText();
		this.lang = markMessageObj.getLang();
		this.medias = new ArrayList<MediaObject>();
		for (int i = 0; i < markMessageObj.getMedia_types().size(); i++) {
			medias.add(new MediaObject(markMessageObj.getMedia_types().get(i), markMessageObj.getMedia_urls().get(i),markMessageObj.getMedia_urls_local().get(i)));
		}
		this.media_num = medias.size();
	}

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

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public int getMedia_num() {
		return media_num;
	}

	public void setMedia_num(int media_num) {
		this.media_num = media_num;
	}

	public ArrayList<MediaObject> getMedias() {
		return medias;
	}

	public void setMedias(ArrayList<MediaObject> medias) {
		this.medias = medias;
	}

	@Override
	public String toString() {
		return "MarkMsg2Web [msg_id=" + msg_id + ", text=" + text + ", lang=" + lang + ", media_num=" + media_num
				+ ", medias=" + medias + "]";
	}

}
