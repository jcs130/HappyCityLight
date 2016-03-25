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
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.citydigitalpulse.OfflineStatistic.app.AppConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 工具类，包括加密解密以及邮件操作
 * 
 * @author zhonglili
 *
 */
public class Tools {
	private static Set<String> banHashtags = null;
	private static ObjectMapper mapper = new ObjectMapper();

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
			if (!banHashtags.contains(hashtags.get(i).toLowerCase())) {
				res.add(hashtags.get(i));
			}
		}
		return res;
	}

	public static void readSplitSettingFile() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"split_settings.txt")));
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
	public static void updateSplitStartID() {
		try {
			FileWriter fw = new FileWriter("split_settings.txt", false);
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

	public static void readStatisticSettingFile() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"statistic_settings.txt")));
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				String[] sets = line.split(" ");
				if (sets[0].equals("limit")) {
					AppConfig.LIMIT = Integer.parseInt(sets[1]);
				}
				if (sets[0].equals("start_date")) {
					AppConfig.START_DATE = new SimpleDateFormat(
							AppConfig.date_format).parse(sets[1]);
				}
				if (sets[0].equals("end_date")) {
					AppConfig.END_DATE = new SimpleDateFormat(
							AppConfig.date_format).parse(sets[1]);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	public static void updateStatisticStartDate() {
		try {
			FileWriter fw = new FileWriter("statistic_settings.txt", false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("limit " + AppConfig.LIMIT + "\r\n");
			bw.write("start_date "
					+ new SimpleDateFormat(AppConfig.date_format)
							.format(AppConfig.START_DATE) + "\r\n");
			bw.write("end_date "
					+ new SimpleDateFormat(AppConfig.date_format)
							.format(AppConfig.START_DATE) + "\r\n");
			bw.close();
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param string
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getEnglish(String source, String text)
			throws UnsupportedEncodingException {
		String api_url = "https://www.googleapis.com/language/translate/v2";
		HttpClient httpClient = new HttpClient();
		// System.out.println(URLEncoder.encode(api_url, "UTF-8"));
		GetMethod getMethod = new GetMethod(api_url);
		getMethod.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		getMethod.setQueryString("q=" + URLEncoder.encode(text, "UTF-8")
				+ "&source=" + URLEncoder.encode(source, "UTF-8")
				+ "&target=en&key=AIzaSyB23J-EUTInuYwX5AYMcvfjWEX5g2KY69c");
		try {
			// System.out.println("URL:" + url);
			httpClient.executeMethod(getMethod);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
			try {
				String resp = getMethod.getResponseBodyAsString();
				// System.out.println(resp);
				JsonNode rootNode = mapper.readTree(resp);
				JsonNode translateNode = rootNode.get("data").get(
						"translations");
				for (JsonNode node : translateNode) {
					String restext = node.path("translatedText").asText();
					return restext;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return "<error>" + getMethod.getStatusCode();
		}
		return "<error>";
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param string
	 * @param string2
	 * @param string3
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String googleTranslate(String from, String to, String text)
			throws UnsupportedEncodingException {
		String api_url = "https://www.googleapis.com/language/translate/v2";
		HttpClient httpClient = new HttpClient();
		// System.out.println(URLEncoder.encode(api_url, "UTF-8"));
		GetMethod getMethod = new GetMethod(api_url);
		getMethod.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		getMethod.setQueryString("q=" + URLEncoder.encode(text, "UTF-8")
				+ "&source=" + URLEncoder.encode(from, "UTF-8") + "&target="
				+ URLEncoder.encode(to, "UTF-8")
				+ "&key=AIzaSyB23J-EUTInuYwX5AYMcvfjWEX5g2KY69c");
		try {
			// System.out.println("URL:" + url);
			httpClient.executeMethod(getMethod);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
			try {
				String resp = getMethod.getResponseBodyAsString();
				// System.out.println(resp);
				JsonNode rootNode = mapper.readTree(resp);
				JsonNode translateNode = rootNode.get("data").get(
						"translations");
				for (JsonNode node : translateNode) {
					String restext = node.path("translatedText").asText();
					return restext;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return "<error>" + getMethod.getStatusCode();
		}
		return "<error>";
	}
}
