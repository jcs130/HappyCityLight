/** 
 * Copyright (C) 2016 City Digital Pulse - All Rights Reserved
 *  
 * Author: Yuanyuan Li
 *  
 * Design: Zhongli Li and Shiai Zhu
 *  
 * Concept and supervision Abdulmotaleb El Saddik
 *
 */
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.ipml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.dao.PlaceIdInterface;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.model.placeID;

/**
 * SQLs on operations of queuing table
 */
public class PlaceIDInterface_MySQL implements PlaceIdInterface {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PlaceIDInterface_MySQL() {
		dataSource = new PlaceIDConnector();
	}

	@Override
	public HashMap<String, double[]> GetPlaceID() {
		// Return list of placeids
		String sqlString = "SELECT * FROM placeidinfor;";
		HashMap<String, double[]> results = new HashMap<String, double[]>();
		// Query the DB and return the result
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sqlString);
			placeID places = null;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				places = new placeID();
				places.set_Placeid(rs.getString("placeid"));
				places.set_Latitude(rs.getDouble("latitude"));
				places.set_Longitude(rs.getDouble("longitude"));
				// System.out.println(ask);
				double[] latlong = new double[2];
				latlong[0] = places.get_Latitude();
				latlong[1] = places.get_Longitude();
				results.put(places.get_Placeid(), latlong);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return results;
	}

	@Override
	public void insert(placeID placeid) {
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			String sqlString = "INSERT INTO placeidinfor (placeid, latitude, longitude) VALUES (?, ?, ?);";
			ps = conn.prepareStatement(sqlString);
			ps.setString(1, placeid.get_Placeid());
			ps.setDouble(2, placeid.get_Latitude());
			ps.setDouble(3, placeid.get_Longitude());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
				// throw new RuntimeException(e);
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
