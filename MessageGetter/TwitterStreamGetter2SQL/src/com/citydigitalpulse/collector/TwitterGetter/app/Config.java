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
package com.citydigitalpulse.collector.TwitterGetter.app;

/**
 * 
 * @author zhonglili
 *
 */
public class Config {

	/********************** 本机测试上传地址 **************************/
	public static String DCI_SERVER_URL = "http://localhost:8080/HappyCityLight/api/";
	/********************** 本机测试上传地址 **************************/

	/********************** 部署上传地址 **************************/
	// public static final String DCI_SERVER_URL_AMAZON =
	// "http://citydigitalpulse.us-west-2.elasticbeanstalk.com/api/";
	// public static String DCI_SERVER_URL =
	// "http://citypulse1.site.uottawa.ca/api/";
	/********************** 部署测试上传地址 **************************/

	public static String UPLOAD_TOKEN = "Imagoodboy";

	// 用于控制爬虫程序的数据库
	// The Database of Getter Controller
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL = "jdbc:mysql://137.122.93.153:3306/happycityproject?useSSL=false";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME = "jcs130";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储统计数据的数据库
	// The Database for statistic records
	public static String MESSAGE_SAVING_DATABASE_STATISTIC_URL = "jdbc:mysql://citypulse1.site.uottawa.ca:3306/MsgSaving?useSSL=false";
	public static String MESSAGE_SAVING_DATABASE_STATISTIC_USER_NAME = "jcs130";
	public static String MESSAGE_SAVING_DATABASE_STATISTIC_USER_PASSWORD = "jcsss130";

	// 用于存储原始数据的数据库
	// The Database for saving raw/split structure data
	public static String MESSAGE_RAW_DATABASE_URL = "jdbc:mysql://137.122.93.153:3306/MsgSaving?useSSL=false";
	public static String MESSAGE_RAW_DATABASE_USER_NAME = "jcs130";
	public static String MESSAGE_RAW_DATABASE_USER_PASSWORD = "jcsss130";
}
