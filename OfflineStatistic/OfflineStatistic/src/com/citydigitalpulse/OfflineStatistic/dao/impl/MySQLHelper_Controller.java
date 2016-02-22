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
package com.citydigitalpulse.OfflineStatistic.dao.impl;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.citydigitalpulse.OfflineStatistic.app.AppConfig;


public class MySQLHelper_Controller implements DataSource {
	private static final String name = "com.mysql.jdbc.Driver";

	// private static final String password = "ilikexiah";

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		// TODO Auto-generated method stub
		return DriverManager.getConnection(
				AppConfig.MESSAGE_GETTER_CONTROLLER_DATABASE_URL, username, password);// 获取连接
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			Class.forName(name);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 指定连接类型
		return DriverManager.getConnection(
				AppConfig.MESSAGE_GETTER_CONTROLLER_DATABASE_URL,
				AppConfig.MESSAGE_GETTER_CONTROLLER_DATABASE_USER_NAME,
				AppConfig.MESSAGE_GETTER_CONTROLLER_DATABASE_USER_PASSWORD);// 获取连接
	}

}
