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
package com.citydigitalpulse.OfflineStatistic.tool;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 工具类，包括加密解密以及邮件操作
 * 
 * @author zhonglili
 *
 */
public class Tools {

	public static ArrayList<String> buildListFromString(String listString) {
		ArrayList<String> res = new ArrayList<String>();
		if (listString != null) {
			String[] temp = listString.split(",");
			for (int i = 0; i < temp.length; i++) {
				if (!"".equals(temp[i])) {
					res.add(temp[i]);
				}
			}
		}

		return res;
	}

	public static ArrayList<Long> buildLongListFromString(String listString) {
		ArrayList<Long> res = new ArrayList<Long>();
		if (listString != null) {
			String[] temp = listString.split(",");
			for (int i = 0; i < temp.length; i++) {
				if (!"".equals(temp[i])) {
					res.add(Long.parseLong(temp[i]));
				}
			}
		}
		return res;
	}

	public static String buildStringFromList(List<String> stringList) {
		String res = "";
		for (int i = 0; i < stringList.size(); i++) {
			res += stringList.get(i);
			if (i < stringList.size() - 1) {
				res += ",";
			}
		}
		return res;
	}

	public static String buildStringFromLongList(ArrayList<Long> longList) {
		String res = "";
		for (int i = 0; i < longList.size(); i++) {
			res += "" + longList.get(i);
			if (i < longList.size() - 1) {
				res += ",";
			}
		}
		return res;
	}

}
