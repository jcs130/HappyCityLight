package com.zhongli.happycity.model.message;

public class MediaObject {
	private String media_type;
	private String media_url;

	public MediaObject(String media_type, String media_url) {
		this.media_type = media_type;
		this.media_url = media_url;
	}

	public String getMedia_type() {
		return media_type;
	}

	public String getMedia_url() {
		return media_url;
	}

	@Override
	public String toString() {
		return "MediaObject [media_type=" + media_type + ", media_url=" + media_url + "]";
	}

}
