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
package com.citydigitalpulse.OfflineStatistic.tool.senitool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Zhongli Li
 *
 */
public class Translater {
	private String[] languages;
	private ArrayList<ArrayList<String>> dictionary;
	private HashMap<String, Integer> languages_map;

	/**
	 * 
	 */
	public Translater() {
		init();
	}

	public String getEnglish(String language_code, String text) {
		Integer index = languages_map.get(language_code);
		String res = "";
		if (index == null) {
			return res;
		} else {
			ArrayList<String> tempList = dictionary.get(index);
			for (int j = 0; j < tempList.size(); j++) {
				if (text.contains(tempList.get(j))) {
					res += dictionary.get(0).get(j) + " ";
				}
				// text = text.replaceAll(tempList.get(j), dictionary.get(0)
				// .get(j));
			}
			// res = text;
			return res;
		}
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 */
	private void init() {
		languages = new String[] { "en", "ar", "eu", "bn", "ca", "zh-cn",
				"zh-tw", "da", "nl", "eo", "fi", "fr", "de", "el", "gu", "he",
				"hi", "ga", "it", "ja", "la", "mr", "fa", "pt", "ro", "ru",
				"so", "es", "su", "sw", "sv", "ta", "te", "th", "tr", "uk",
				"ur", "vi", "cy", "yi", "zu" };
		languages_map = new HashMap<String, Integer>();
		for (int i = 0; i < languages.length; i++) {
			languages_map.put(languages[i], i);
		}
		dictionary = readDicFromFile("Digital City Pulse based on NRC-2.txt");
	}

	/**
	 * @Author Zhongli Li Email: lzl19920403@gmail.com
	 * @param string
	 * @return
	 */
	private ArrayList<ArrayList<String>> readDicFromFile(String path) {
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		InputStream ins = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(path);
		// 初始化
		for (int i = 0; i < languages.length; i++) {
			res.add(new ArrayList<String>());
		}
		try {
			InputStreamReader read = new InputStreamReader(ins, "UTF-16");
			String line;
			BufferedReader in = new BufferedReader(read);
			while ((line = in.readLine()) != null) {
				// System.out.println(line.trim());
				String[] words = line.trim().toLowerCase().split("\t");
				for (int i = 0; i < languages.length; i++) {
					res.get(i).add(words[i]);
				}
			}

			read.close();
			ins.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ins != null)
				try {
					ins.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		// System.out.println(path + "....done");
		return res;
	}
}
