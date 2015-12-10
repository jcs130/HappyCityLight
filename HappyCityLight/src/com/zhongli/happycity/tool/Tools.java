package com.zhongli.happycity.tool;

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
import javax.mail.MessagingException;

import com.zhongli.happycity.mail.mailSender;

/**
 * 工具类，包括加密解密以及邮件操作
 * 
 * @author zhonglili
 *
 */
public class Tools {
	private static final String MAC_NAME = "HmacSHA256";
	private static final String ENCODING = "UTF-8";

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
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
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
	 * 展示了一个生成指定算法密钥的过程 初始化HMAC密钥
	 * 
	 * @return
	 * 
	 * @throws Exception
	 * 
	 * public static String initMacKey() throws Exception { //得到一个 指定算法密钥的密钥生成器
	 * KeyGenerator KeyGenerator keyGenerator
	 * =KeyGenerator.getInstance(MAC_NAME); //生成一个密钥 SecretKey secretKey
	 * =keyGenerator.generateKey(); return null; }
	 */

	/**
	 * 使用 HMAC-SHA256 签名方法对对encryptText进行签名
	 * 
	 * @param encryptText
	 *            被签名的字符串
	 * @param encryptKey
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static String HmacSHA256Encrypt(String encryptText, String encryptKey)
			throws Exception {
		byte[] data = encryptKey.getBytes(ENCODING);
		// 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
		SecretKey secretKey = new SecretKeySpec(data, "HmacSHA256");
		// 生成一个指定 Mac 算法 的 Mac 对象
		Mac mac = Mac.getInstance(MAC_NAME);
		// 用给定密钥初始化 Mac 对象
		mac.init(secretKey);

		byte[] text = encryptText.getBytes(ENCODING);
		// 完成 Mac 操作
		return byteArr2HexStr(mac.doFinal(text));
	}

	/**
	 * DES 算法解密
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
		System.out.println(res);
		return res;
	}

	/**
	 * DES 算法加密
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
	 * DES 算法生成密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String initKey() throws Exception {
		return initKey(null);
	}

	/**
	 * DES 算法生成密钥
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
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
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

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	public static void sendVerificationEmail(String userEmail, String verifyUrl)
			throws UnsupportedEncodingException, MessagingException {
		mailSender.sendMail("lzl19920403@163.com", "Digital City", userEmail,
				userEmail, "Last Step. Verify your email address.", verifyUrl);
	}

	public static void sendResetPasswordEmail(String email, String token,
			String baseURL) throws UnsupportedEncodingException,
			MessagingException {
		mailSender
				.sendMail(
						"lzl19920403@163.com",
						"Digital City",
						email,
						email,
						"Please reset your password.",
						"Please copy the token and use the changepassword API to change your Password. \n "
								+ baseURL.substring(0, baseURL.indexOf("api/"))
								+ "/resetpassword.html?token="
								+ token
								+ "\n token: " + token);
	}
}
