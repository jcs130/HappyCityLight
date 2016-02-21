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
package com.citydigitalpulse.OfflineStatistic.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Zhongli Li
 *
 */
public class HotTopic implements Comparator<HotTopic> {
	private String text;
	private List<ImgAndScore> images;
	private double impuse_value;
	private ImpuseValue impuse_obj;
	private int count;

	/**
	 * 
	 */
	public HotTopic() {
		super();
		this.text = "";
		this.images = new ArrayList<ImgAndScore>();
		this.impuse_value = 0;
		this.impuse_obj = new ImpuseValue();
		this.count = 0;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<ImgAndScore> getImages() {
		return images;
	}

	public void setImages(List<ImgAndScore> images) {
		this.images = images;
	}

	public double getImpuse_value() {
		return impuse_value;
	}

	public void setImpuse_value(double impuse_value) {
		this.impuse_value = impuse_value;
	}

	public ImpuseValue getImpuse_obj() {
		return impuse_obj;
	}

	public void setImpuse_obj(ImpuseValue impuse_obj) {
		this.impuse_obj = impuse_obj;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}


	@Override
	public int compare(HotTopic o1, HotTopic o2) {
		int val1 = o1.getCount();
		int val2 = o2.getCount();
		return val1 < val2 ? 0 : 1;
	}

}
