/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Zhongli Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.webservice.model.message;

/**
 * @author Zhongli Li
 *
 */
public class ImgAndScore {
	private String img_url;
	private String img_url_local;
	private String emotion;
	private double cofidence;

	/**
	 * 
	 */
	public ImgAndScore() {
		super();
		this.img_url = "";
		this.img_url_local = "";
		this.emotion = "";
		this.cofidence = 0;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getImg_url_local() {
		return img_url_local;
	}

	public void setImg_url_local(String img_url_local) {
		this.img_url_local = img_url_local;
	}

	public String getEmotion() {
		return emotion;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}

	public double getCofidence() {
		return cofidence;
	}

	public void setCofidence(double cofidence) {
		this.cofidence = cofidence;
	}

}
