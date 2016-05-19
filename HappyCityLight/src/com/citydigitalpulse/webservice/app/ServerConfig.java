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
package com.citydigitalpulse.webservice.app;

public class ServerConfig {

	/*************** 亚马逊云服务器 **************/
	// // 用于控制爬虫程序的数据库
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL =
	// "jdbc:mysql://aadnpbvnceozrn.cqkitdz1e3ma.us-west-2.rds.amazonaws.com:3306/happycityproject?useSSL=false";
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME =
	// "jcs130";
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD =
	// "jcsss130";
	//
	// // 用于存储结构化后的数据的数据库
	// public static String MESSAGE_SAVING_DATABASE_URL =
	// "jdbc:mysql://aadnpbvnceozrn.cqkitdz1e3ma.us-west-2.rds.amazonaws.com:3306/msgsaving?useSSL=false";
	// public static String MESSAGE_SAVING_DATABASE_USER_NAME = "jcs130";
	// public static String MESSAGE_SAVING_DATABASE_USER_PASSWORD = "jcsss130";
	//
	// // 用于存储网站用户信息的数据库
	// public static String WEB_SERVER_DATABASE_URL =
	// "jdbc:mysql://aadnpbvnceozrn.cqkitdz1e3ma.us-west-2.rds.amazonaws.com:3306/digitalcity?useSSL=false";
	// public static String WEB_SERVER_DATABASE_USER_NAME = "jcs130";
	// public static String WEB_SERVER_DATABASE_USER_PASSWORD = "jcsss130";
	//
	// // 用于存储人工标注的训练数据的数据库
	// public static String TRAINING_DATABASE_URL =
	// "jdbc:mysql://aadnpbvnceozrn.cqkitdz1e3ma.us-west-2.rds.amazonaws.com:3306/digitalcity?useSSL=false";
	// public static String TRAINING_DATABASE_USER_NAME = "jcs130";
	// public static String TRAINING_DATABASE_USER_PASSWORD = "jcsss130";
	//
	// // 用于存储缓存的服务器地址
	// public static String CACHE_ADDR =
	// "cdpservercache.koywr5.0001.usw2.cache.amazonaws.com";
	// public static int CACHE_PORT = 6379;
	/*************** 亚马逊云服务器 **************/

	/*************** 实验室服务器 **************/
	// // 用于存储缓存的服务器地址
	// // public static String CACHE_ADDR = "137.122.89.207";
	// // public static int CACHE_PORT = 6379;
	// public static String CACHE_ADDR = "127.0.0.1";
	// public static int CACHE_PORT = 6379;
	// // 用于控制爬虫程序的数据库
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL =
	// "jdbc:mysql://137.122.93.153:3306/happycityproject?useSSL=false";
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME =
	// "jcs130";
	// public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD =
	// "jcsss130";
	//
	// // 用于存储结构化后的数据的数据库
	// public static String MESSAGE_SAVING_DATABASE_URL =
	// "jdbc:mysql://137.122.89.207:3306/MsgSaving?useSSL=false";
	// public static String MESSAGE_SAVING_DATABASE_USER_NAME = "jcs130";
	// public static String MESSAGE_SAVING_DATABASE_USER_PASSWORD = "jcsss130";
	//
	// // 用于存储网站用户信息的数据库
	// public static String WEB_SERVER_DATABASE_URL =
	// "jdbc:mysql://137.122.89.207:3306/digitalcity?useSSL=false";
	// public static String WEB_SERVER_DATABASE_USER_NAME = "jcs130";
	// public static String WEB_SERVER_DATABASE_USER_PASSWORD = "jcsss130";
	//
	// // 用于存储人工标注的训练数据的数据库
	// public static String TRAINING_DATABASE_URL =
	// "jdbc:mysql://137.122.89.207:3306/digitalcity?useSSL=false";
	// public static String TRAINING_DATABASE_USER_NAME = "jcs130";
	// public static String TRAINING_DATABASE_USER_PASSWORD = "jcsss130";

	/*************** 实验室服务器 **************/

	/*************** 本机MAC服务器 **************/
	// 用于存储缓存的服务器地址
	public static String CACHE_ADDR = "127.0.0.1";
	public static int CACHE_PORT = 6379;

	// 用于控制爬虫程序的数据库
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_URL = "jdbc:mysql://localhost:3307/happycityproject?useSSL=false";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME = "root";
	public static String MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储结构化后的数据的数据库
	public static String MESSAGE_SAVING_DATABASE_URL = "jdbc:mysql://localhost:3307/MsgSaving?useSSL=false";
	public static String MESSAGE_SAVING_DATABASE_USER_NAME = "root";
	public static String MESSAGE_SAVING_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储网站用户信息的数据库
	public static String WEB_SERVER_DATABASE_URL = "jdbc:mysql://localhost:3307/digitalcity?useSSL=false";
	public static String WEB_SERVER_DATABASE_USER_NAME = "root";
	public static String WEB_SERVER_DATABASE_USER_PASSWORD = "jcsss130";

	// 用于存储人工标注的训练数据的数据库
	public static String TRAINING_DATABASE_URL = "jdbc:mysql://localhost:3307/digitalcity?useSSL=false";
	public static String TRAINING_DATABASE_USER_NAME = "root";
	public static String TRAINING_DATABASE_USER_PASSWORD = "jcsss130";
	/*************** 本机MAC服务器 **************/
}
