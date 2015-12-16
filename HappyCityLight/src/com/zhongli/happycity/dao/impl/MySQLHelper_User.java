package com.zhongli.happycity.dao.impl;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class MySQLHelper_User implements DataSource {
	// private static final String url =
	// "jdbc:mysql://localhost:3307/digitalcity";
	// 阿里云数据库
	private static final String url = "jdbc:mysql://rdsv068s015f5ee7z6cl.mysql.rds.aliyuncs.com:3306/ru28t8ce91gc69ss";
	// private static final String user = "root";
	// 阿里云数据库
	private static final String user = "jcs130";
	private static final String password = "jcsss130";
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
		return DriverManager.getConnection(url, username, password);// 获取连接
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			Class.forName(name);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 指定连接类型
		return DriverManager.getConnection(url, user, password);// 获取连接
	}

}
