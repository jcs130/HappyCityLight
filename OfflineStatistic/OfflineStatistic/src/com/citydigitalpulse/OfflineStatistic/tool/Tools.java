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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.citydigitalpulse.OfflineStatistic.app.AppConfig;

/**
 * 工具类，包括加密解密以及邮件操作
 * 
 * @author zhonglili
 *
 */
public class Tools {
	private static Set<String> banHashtags = null;

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

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param hashtags
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> hashtagFiliter(List<String> hashtags) {
		ArrayList<String> res = new ArrayList<String>();
		// 从文件中读取屏蔽词列表
		if (banHashtags == null) {
			banHashtags = new HashSet<String>();
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream("banTopics.txt")));
				for (String line = br.readLine(); line != null; line = br
						.readLine()) {
					// System.out.println(line);
					banHashtags.add(line);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 如果不在屏蔽列表中，则添加
		for (int i = 0; i < hashtags.size(); i++) {
			if (!banHashtags.contains(hashtags.get(i))) {
				res.add(hashtags.get(i));
			}
		}
		return res;
	}

	public static void readSettingFile() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"settings.txt")));
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				String[] sets = line.split(" ");
				if (sets[0].equals("limit")) {
					AppConfig.LIMIT = Integer.parseInt(sets[1]);
				}
				if (sets[0].equals("start_id")) {
					AppConfig.START_ID = Integer.parseInt(sets[1]);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	public static void updateStartID() {
		try {
			FileWriter fw = new FileWriter("settings.txt", false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("limit " + AppConfig.LIMIT + "\r\n");
			bw.write("start_id " + AppConfig.START_ID + "\r\n");
			bw.close();
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
