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
 * 直接返回的城市脉搏值
 * 
 * @author zhonglili
 *
 */
public class PulseValue {
	private long timestamp;
	private int positive_num;
	private int negative_num;
	private int neutral_num;
	private int unknown_num;
	private int sum_num;
	private double score;
	private double pulse_value;

	public PulseValue() {
		super();
		this.timestamp = 0;
		this.positive_num = 0;
		this.negative_num = 0;
		this.neutral_num = 0;
		this.unknown_num = 0;
		this.sum_num = 0;
		this.score = 0;
		this.pulse_value = 0;
	}

	public void addNewValue(String emotion, double value) {
		this.sum_num += 1;
		this.score += value;
		if (emotion == null) {
			this.unknown_num += 1;
		} else if ("positive".equals(emotion.toLowerCase())) {
			this.positive_num += 1;
			this.pulse_value += value;
			calculate_pulse_value();
		} else if ("neutral".equals(emotion.toLowerCase())) {
			this.neutral_num += 1;
			this.pulse_value += value;
			calculate_pulse_value();
		} else if ("negative".equals(emotion.toLowerCase())) {
			this.negative_num += 1;
			this.pulse_value += value;
			calculate_pulse_value();
		} else {
			// unknown
			this.unknown_num += 1;
		}
	}

	public void calculate_pulse_value() {
		// double a = this.positive_num;
		// double b = this.neutral_num;
		// double c = this.negative_num;
		double s = this.sum_num;
		double u = this.unknown_num;
		double t = this.score;
		if ((s - u) > 500) {
			this.pulse_value = t / (s - u);
		} else {
			this.pulse_value = 0;
		}

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

	public double getPulse_value() {
		return pulse_value;
	}

	public void setPulse_value(double pulse_value) {
		this.pulse_value = pulse_value;
	}

	public int getUnknown_num() {
		return unknown_num;
	}

	public void setUnknown_num(int unknown_num) {
		this.unknown_num = unknown_num;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "PulseValue [timestamp=" + timestamp + ", positive_num="
				+ positive_num + ", negative_num=" + negative_num
				+ ", neutral_num=" + neutral_num + ", unknown_num="
				+ unknown_num + ", sum_num=" + sum_num + ", score=" + score
				+ ", pulse_value=" + pulse_value + "]";
	}

}
