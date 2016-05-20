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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.MessageModel;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.placeID;

/**
 * Tools for list to string and string to list *
 */
public class Tools {

	private static final String MAC_NAME = "HmacSHA256";
	private static final String ENCODING = "UTF-8";
	public static ArrayList<MessageModel> msgList = new ArrayList<MessageModel>();
	public static HashMap<Long, MessageModel> cacheUpdateMessages = new HashMap<Long, MessageModel>();
	public static HashMap<String, placeID> cacheUpdatePlacesIDs = new HashMap<String, placeID>();

	public static ArrayList<String> buildListFromString(String listString) {
		String[] temp = listString.split(",");
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < temp.length; i++) {
			if (!"".equals(temp[i])) {
				res.add(temp[i]);
			}
		}
		return res;
	}

	public static ArrayList<Long> buildLongListFromString(String listString) {
		String[] temp = listString.split(",");
		ArrayList<Long> res = new ArrayList<Long>();
		for (int i = 0; i < temp.length; i++) {
			if (!"".equals(temp[i])) {
				res.add(Long.parseLong(temp[i]));
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

	public static boolean emailFormat(String email) {
		boolean res = true;
		final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			res = false;
		}
		return res;
	}

	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 鑾峰緱MD5鎽樿绠楁硶鐨� MessageDigest 瀵硅薄
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 浣跨敤鎸囧畾鐨勫瓧鑺傛洿鏂版憳瑕�
			mdInst.update(btInput);
			// 鑾峰緱瀵嗘枃
			byte[] md = mdInst.digest();
			// 鎶婂瘑鏂囪浆鎹㈡垚鍗佸叚杩涘埗鐨勫瓧绗︿覆褰㈠紡
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * 灞曠ず浜嗕竴涓敓鎴愭寚瀹氱畻娉曞瘑閽ョ殑杩囩▼ 鍒濆鍖朒MAC瀵嗛挜
	 * 
	 * @return
	 * 
	 * @throws Exception
	 * 
	 * public static String initMacKey() throws Exception { //寰楀埌涓�涓� 鎸囧畾绠楁硶瀵嗛挜鐨勫瘑閽ョ敓鎴愬櫒
	 * KeyGenerator KeyGenerator keyGenerator
	 * =KeyGenerator.getInstance(MAC_NAME); //鐢熸垚涓�涓瘑閽� SecretKey secretKey
	 * =keyGenerator.generateKey(); return null; }
	 */

	/**
	 * 浣跨敤 HMAC-SHA256 绛惧悕鏂规硶瀵瑰encryptText杩涜绛惧悕
	 * 
	 * @param encryptText
	 *            琚鍚嶇殑瀛楃涓�
	 * @param encryptKey
	 *            瀵嗛挜
	 * @return
	 * @throws Exception
	 */
	public static String HmacSHA256Encrypt(String encryptText, String encryptKey) throws Exception {
		byte[] data = encryptKey.getBytes(ENCODING);
		// 鏍规嵁缁欏畾鐨勫瓧鑺傛暟缁勬瀯閫犱竴涓瘑閽�,绗簩鍙傛暟鎸囧畾涓�涓瘑閽ョ畻娉曠殑鍚嶇О
		SecretKey secretKey = new SecretKeySpec(data, "HmacSHA256");
		// 鐢熸垚涓�涓寚瀹� Mac 绠楁硶 鐨� Mac 瀵硅薄
		Mac mac = Mac.getInstance(MAC_NAME);
		// 鐢ㄧ粰瀹氬瘑閽ュ垵濮嬪寲 Mac 瀵硅薄
		mac.init(secretKey);

		byte[] text = encryptText.getBytes(ENCODING);
		// 瀹屾垚 Mac 鎿嶄綔
		return byteArr2HexStr(mac.doFinal(text));
	}

	/**
	 * DES 绠楁硶瑙ｅ瘑
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String DESdecrypt(String data, String key) throws Exception {
		String res = "";
		SecretKey secretKey = null;
		DESKeySpec dks = new DESKeySpec(key.getBytes("ISO-8859-1"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		secretKey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		res = new String(cipher.doFinal(hexStr2ByteArr(data)), "ISO-8859-1");
		// System.out.println(res);
		return res;
	}

	/**
	 * DES 绠楁硶鍔犲瘑
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String DESencrypt(String data, String key) throws Exception {
		SecretKey secretKey = null;
		String res = "";
		DESKeySpec dks = new DESKeySpec(key.getBytes("ISO-8859-1"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		secretKey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		res = byteArr2HexStr(cipher.doFinal(data.getBytes()));
		return res;
	}

	/**
	 * DES 绠楁硶鐢熸垚瀵嗛挜
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String initKey() throws Exception {
		return initKey(null);
	}

	/**
	 * DES 绠楁硶鐢熸垚瀵嗛挜
	 * 
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static String initKey(String seed) throws Exception {
		SecureRandom secureRandom = null;
		if (seed != null) {
			secureRandom = new SecureRandom(seed.getBytes("ISO-8859-1"));
		} else {
			secureRandom = new SecureRandom();
		}
		KeyGenerator kg = KeyGenerator.getInstance("DES");
		kg.init(secureRandom);
		SecretKey secretKey = kg.generateKey();
		return new String(secretKey.getEncoded());
	}

	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 姣忎釜byte鐢ㄤ袱涓瓧绗︽墠鑳借〃绀猴紝鎵�浠ュ瓧绗︿覆鐨勯暱搴︽槸鏁扮粍闀垮害鐨勪袱鍊�
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 鎶婅礋鏁拌浆鎹负姝ｆ暟
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 灏忎簬0F鐨勬暟闇�瑕佸湪鍓嶉潰琛�0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		// 涓や釜瀛楃琛ㄧず涓�涓瓧鑺傦紝鎵�浠ュ瓧鑺傛暟缁勯暱搴︽槸瀛楃涓查暱搴﹂櫎浠�2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	public static void sendNewMessage(String url, String token, MessageModel msg) {
		// System.out.println("Send to " + url);
		HttpClient httpClient = new HttpClient();
		// System.out.println("1");
		PostMethod postMethod = new PostMethod(url);
		// System.out.println("2");
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		postMethod.addParameter("token", token);
		// System.out.println("3");
		postMethod.addParameter("num_id", "" + msg.getNum_id());
		postMethod.addParameter("raw_id_str", msg.getRaw_id_str());
		// System.out.println("4");
		postMethod.addParameter("user_name", msg.getUser_name());
		postMethod.addParameter("profile_image_url", msg.getProfile_image());

		// System.out.println("5");
		postMethod.addParameter("text", msg.getText());
		// System.out.println("6");
		postMethod.addParameter("creat_at", "" + msg.getCreat_at());
		// System.out.println("7");
		if (msg.getEmotion_text() != null) {
			postMethod.addParameter("emotion_text", msg.getEmotion_text());
		} else {
			postMethod.addParameter("emotion_text", "");
		}

		// System.out.println("8");
		postMethod.addParameter("media_types", buildStringFromList(msg.getMedia_types()));
		// System.out.println("9");
		postMethod.addParameter("media_urls", buildStringFromList(msg.getMedia_urls()));
		// System.out.println("10");
		postMethod.addParameter("media_urls_local", buildStringFromList(msg.getMedia_urls_local()));
		// System.out.println("11");
		postMethod.addParameter("emotion_medias", buildStringFromList(msg.getEmotion_medias()));
		// System.out.println("12");
		postMethod.addParameter("emotion_all", msg.getEmotion_all());
		// System.out.println("13");
		postMethod.addParameter("place_type", msg.getPlace_type());
		// System.out.println("14");
		postMethod.addParameter("place_name", msg.getPlace_name());
		// System.out.println("15");
		postMethod.addParameter("place_fullname", msg.getPlace_fullname());
		// System.out.println("16");
		postMethod.addParameter("country", msg.getCountry());
		// System.out.println("17");
		postMethod.addParameter("province", msg.getProvince());
		// System.out.println("18");
		postMethod.addParameter("city", msg.getCity());
		// System.out.println("19");
		postMethod.addParameter("query_location_latitude", "" + msg.getQuery_location_latitude());
		// System.out.println("20");
		postMethod.addParameter("query_location_langtitude", "" + msg.getQuery_location_langtitude());
		// System.out.println("21");
		postMethod.addParameter("hashtags", buildStringFromList(msg.getHashtags()));
		// System.out.println("22");
		postMethod.addParameter("replay_to", msg.getReplay_to());
		postMethod.addParameter("lang", msg.getLang());
		// System.out.println("23");
		postMethod.addParameter("message_from", msg.getMessage_from());
		// System.out.println("24");
		postMethod.addParameter("is_real_location", msg.getisreal());
		try {
			// System.out.println("URL:" + url);
			httpClient.executeMethod(postMethod);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
			try {
				String resp = postMethod.getResponseBodyAsString();
				System.out.println(resp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// ...postMethod.getStatusLine();
		}
	}

	public static long getTimeStamp(String dateToGet) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse(dateToGet);
		long time = date.getTime();
		// getTime will be in million second. Uql time stamp is in second
		return time / 1000;
	}

	private static final String CURRENT_DATE_FORMAT = "yyyy-MM-dd";

	public static String format(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(CURRENT_DATE_FORMAT);
		return dateFormat.format(date);
	}

	public static String formatYesterday(String currentDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(CURRENT_DATE_FORMAT);
		GregorianCalendar gc = new GregorianCalendar();
		java.util.Date d = sdf.parse(currentDate);
		gc.setTime(d);
		int dayBefore = gc.get(Calendar.DAY_OF_YEAR);
		gc.roll(Calendar.DAY_OF_YEAR, -1);
		int dayAfter = gc.get(Calendar.DAY_OF_YEAR);
		if (dayAfter > dayBefore) {
			gc.roll(Calendar.YEAR, -1);
		}
		gc.get(Calendar.DATE);
		java.util.Date yesterday = gc.getTime();
		return sdf.format(yesterday);
	}

	public static String formatTomorrow(String currentDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(CURRENT_DATE_FORMAT);
		GregorianCalendar gc = new GregorianCalendar();
		java.util.Date d = sdf.parse(currentDate);
		gc.setTime(d);
		int dayBefore = gc.get(Calendar.DAY_OF_YEAR);
		gc.roll(Calendar.DAY_OF_YEAR, +1);
		int dayAfter = gc.get(Calendar.DAY_OF_YEAR);
		if (dayAfter < dayBefore) {
			gc.roll(Calendar.YEAR, +1);
		}
		gc.get(Calendar.DATE);
		java.util.Date tomorrom = gc.getTime();
		return sdf.format(tomorrom);
	}
}