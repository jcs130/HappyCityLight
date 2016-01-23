package com.citydigitalpulse.webservice.model.message;

public class MediaObject {
	private String media_type;
	private String media_url;
	private String media_url_local;

	public MediaObject(String media_type, String media_url,
			String media_url_local) {
		this.media_type = media_type;
		this.media_url = media_url;
		this.media_url_local = media_url_local;
	}

	public String getMedia_type() {
		return media_type;
	}

	public String getMedia_url() {
		return media_url;
	}

	public String getMedia_url_local() {
		return media_url_local;
	}

	public void setMedia_url_local(String media_url_local) {
		this.media_url_local = media_url_local;
	}

	@Override
	public String toString() {
		return "MediaObject [media_type=" + media_type + ", media_url="
				+ media_url + ", media_url_local=" + media_url_local + "]";
	}

}
