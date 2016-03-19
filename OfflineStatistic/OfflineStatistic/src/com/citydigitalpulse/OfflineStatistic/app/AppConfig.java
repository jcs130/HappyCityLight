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
package com.citydigitalpulse.OfflineStatistic.app;

import java.util.Date;

public class AppConfig {
	public static int LIMIT = 5000;
	public static long START_ID = 0;

	public static String date_format = "yyyy_MM_dd";
	public static Date START_DATE = new Date();
	public static Date END_DATE = new Date();

	// 用于控制爬虫程序的数据库
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL = "jdbc:mysql://citypulse2.site.uottawa.ca:3306/happycityproject?useSSL=false";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME = "jcs130";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储原始消息的数据的数据库
	public static String MESSAGE_RAW_DATABASE_URL = "jdbc:mysql://citypulse2.site.uottawa.ca:3306/MsgSaving?useSSL=false";
	public static String MESSAGE_RAW_DATABASE_USER_NAME = "jcs130";
	public static String MESSAGE_RAW_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储分库后的数据的数据库
	public static String MESSAGE_SAVING_DATABASE_URL = "jdbc:mysql://citypulse1.site.uottawa.ca:3306/MsgSaving?useSSL=false";
	public static String MESSAGE_SAVING_DATABASE_USER_NAME = "jcs130";
	public static String MESSAGE_SAVING_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储统计数据的数据库
	public static String MESSAGE_SAVING_DATABASE_STATISTIC_URL = "jdbc:mysql://citypulse1.site.uottawa.ca:3306/MsgSaving?useSSL=false";
	public static String MESSAGE_SAVING_DATABASE_STATISTIC_USER_NAME = "jcs130";
	public static String MESSAGE_SAVING_DATABASE_STATISTIC_USER_PASSWORD = "jcsss130";

	// // 用于控制爬虫程序的数据库
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL =
	// "jdbc:mysql://localhost:3307/happycityproject?useSSL=false";
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME =
	// "root";
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD =
	// "jcsss130";
	//
	// // 用于存储原始消息的数据的数据库
	// public static String MESSAGE_RAW_DATABASE_URL =
	// "jdbc:mysql://localhost:3307/MsgSaving?useSSL=false";
	// public static String MESSAGE_RAW_DATABASE_USER_NAME = "root";
	// public static String MESSAGE_RAW_DATABASE_USER_PASSWORD = "jcsss130";
	//
	// // 用于存储结构化后的数据的数据库
	// public static String MESSAGE_SAVING_DATABASE_URL =
	// "jdbc:mysql://localhost:3307/MsgSaving?useSSL=false";
	// public static String MESSAGE_SAVING_DATABASE_USER_NAME = "root";
	// public static String MESSAGE_SAVING_DATABASE_USER_PASSWORD = "jcsss130";

}
