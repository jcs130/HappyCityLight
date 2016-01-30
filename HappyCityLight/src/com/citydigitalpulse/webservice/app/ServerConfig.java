package com.citydigitalpulse.webservice.app;

public class ServerConfig {
	// MAC 开发机
	// "jdbc:mysql://localhost:3307/digitalcity";
	// user = "root";
	// password = "jcsss130";

	// 阿里云数据库
	// "jdbc:mysql://rdsv068s015f5ee7z6cl.mysql.rds.aliyuncs.com:3306/ru28t8ce91gc69ss";
	// user = "jcs130";
	// password = "jcsss130";

	// 实验室WEB主机数据库
	// "jdbc:mysql://localhost:3306/digitalcity";
	// user = "root";
	// password = "jcsss130";

	// 用于控制爬虫程序的数据库
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL = "jdbc:mysql://137.122.89.207:3306/happycityproject";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME = "jcs130";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储结构化后的数据的数据库
	public static String MESSAGE_SAVING_DATABASE_URL = "jdbc:mysql://137.122.89.207:3306/MsgSaving";
	public static String MESSAGE_SAVING_DATABASE_USER_NAME = "jcs130";
	public static String MESSAGE_SAVING_DATABASE_USER_PASSWORD = "jcsss130";

	// // 用于控制爬虫程序的数据库
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL =
	// "jdbc:mysql://localhost:3307/happycityproject";
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME =
	// "root";
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD =
	// "jcsss130";
	//
	// // 用于存储结构化后的数据的数据库
	// public static String MESSAGE_SAVING_DATABASE_URL =
	// "jdbc:mysql://localhost:3307/MsgSaving";
	// public static String MESSAGE_SAVING_DATABASE_USER_NAME = "root";
	// public static String MESSAGE_SAVING_DATABASE_USER_PASSWORD = "jcsss130";
	//
	// // 用于存储网站用户信息的数据库
	// public static String WEB_SERVER_DATABASE_URL =
	// "jdbc:mysql://localhost:3307/digitalcity";
	// public static String WEB_SERVER_DATABASE_USER_NAME = "root";
	// public static String WEB_SERVER_DATABASE_USER_PASSWORD = "jcsss130";
	//
	// // 用于存储人工标注的训练数据的数据库
	// public static String TRAINING_DATABASE_URL =
	// "jdbc:mysql://localhost:3307/digitalcity";
	// public static String TRAINING_DATABASE_USER_NAME = "root";
	// public static String TRAINING_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储网站用户信息的数据库
	public static String WEB_SERVER_DATABASE_URL = "jdbc:mysql://137.122.89.207:3306/digitalcity";
	public static String WEB_SERVER_DATABASE_USER_NAME = "jcs130";
	public static String WEB_SERVER_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储人工标注的训练数据的数据库
	public static String TRAINING_DATABASE_URL = "jdbc:mysql://137.122.89.207:3306/digitalcity";
	public static String TRAINING_DATABASE_USER_NAME = "jcs130";
	public static String TRAINING_DATABASE_USER_PASSWORD = "jcsss130";
}
