package com.citydigitalpulse.messagegetter.TwitterGetter.app;

/**
 * 
 * @author zhonglili
 *
 */
public class Config {
	// public static String
	// DCI_SERVER_URL="http://localhost:8080/HappyCityLight/api/";
	public static String DCI_SERVER_URL = "http://137.122.89.207/api/";
	public static String UPLOAD_TOKEN = "Imagoodboy";

//	// 用于控制爬虫程序的数据库
//	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL = "jdbc:mysql://localhost:3307/happycityproject";
//	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME = "root";
//	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD = "jcsss130";
//
//	// 用于存储结构化后的数据的数据库
//	public static String MESSAGE_SAVING_DATABASE_URL = "jdbc:mysql://localhost:3307/MsgSaving";
//	public static String MESSAGE_SAVING_DATABASE_USER_NAME = "root";
//	public static String MESSAGE_SAVING_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于控制爬虫程序的数据库
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL = "jdbc:mysql://137.122.89.207:3306/happycityproject";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME = "jcs130";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储结构化后的数据的数据库
	public static String MESSAGE_SAVING_DATABASE_URL = "jdbc:mysql://137.122.89.207:3306/MsgSaving";
	public static String MESSAGE_SAVING_DATABASE_USER_NAME = "jcs130";
	public static String MESSAGE_SAVING_DATABASE_USER_PASSWORD = "jcsss130";
}
