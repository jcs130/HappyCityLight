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

/**
 * 直接返回的城市脉搏值
 * 
 * @author zhonglili
 *
 */
public class ImpuseValue {
	private long timestamp;
	private int positive_num;
	private int negative_num;
	private int neutral_num;
	private int unknown_num;
	private int sum_num;
	private double impuse_value;

	public ImpuseValue() {
		super();
		this.timestamp = 0;
		this.positive_num = 0;
		this.negative_num = 0;
		this.neutral_num = 0;
		this.unknown_num = 0;
		this.sum_num = 0;
		this.impuse_value = 0;
	}

	public ImpuseValue(long time) {
		this();
		this.timestamp = time;
	}

	public void addNewValue(String emotion) {
		if (emotion == null) {
			this.unknown_num += 1;
		} else if ("positive".equals(emotion.toLowerCase())) {
			this.positive_num += 1;
		} else if ("neutral".equals(emotion.toLowerCase())) {
			this.neutral_num += 1;
		} else if ("negative".equals(emotion.toLowerCase())) {
			this.negative_num += 1;
		} else {
			// unknown
			this.unknown_num += 1;
		}
		this.sum_num += 1;
		updateImpuse_value();
	}

	public void updateImpuse_value() {
		int a = this.positive_num;
		int b = this.neutral_num;
		int c = this.negative_num;
		int s = this.sum_num;
		int u = this.unknown_num;
		this.impuse_value = (2 * a + b - c) / (double) s;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getPositive_num() {
		return positive_num;
	}

	public void setPositive_num(int positive_num) {
		this.positive_num = positive_num;
	}

	public int getNegative_num() {
		return negative_num;
	}

	public void setNegative_num(int negative_num) {
		this.negative_num = negative_num;
	}

	public int getNeutral_num() {
		return neutral_num;
	}

	public void setNeutral_num(int neutral_num) {
		this.neutral_num = neutral_num;
	}

	public int getSum_num() {
		return sum_num;
	}

	public void setSum_num(int sum_num) {
		this.sum_num = sum_num;
	}

	public double getImpuse_value() {
		return impuse_value;
	}

	public void setImpuse_value(double impuse_value) {
		this.impuse_value = impuse_value;
	}

	public int getUnknown_num() {
		return unknown_num;
	}

	public void setUnknown_num(int unknown_num) {
		this.unknown_num = unknown_num;
	}

}
