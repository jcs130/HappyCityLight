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
package com.citydigitalpulse.collector.NonRealTimeInstagramGetter.tool;

/**
 * List of user-agent
 */
public class ListofToken {
	private int times = 0;
	private String[] Tokens = { "546785387.c40b2f5.eacef90ac695483aaf8dd035be32dfbb",
			"546785387.beea524.59e28413250f4e8382a2658b15ab367b", "546785387.ea061b4.77650fdfa9094e96a1c1acae41412ef8",
			"546785387.03f1d81.9704ed87fe1d4d3ebe97a4986d46d0d0", "546785387.72d0024.8e34bb8dc6934eeeb09d002244cb8121",
			"546785387.9da59ef.81158ad3d9a6415a865712f4b3d38544", "546785387.775a32b.4f0708f776444dff8acf9a14a372bddc",
			"546785387.544c48c.97a5e5ea5dfb4e338462d3cf3e20b9ab", "546785387.6642deb.1845b1f49d4741e8a75471d987ccc904",
			"546785387.1ff8c5d.0fe2173d81134746acbfc781ded3f94e" };

	public String geTokens() {
		// Select one user agent
		int selectOne = times % 10;
		times++;
		if (times > 100000) {
			times = 0;
		}
		return Tokens[selectOne];
	}
}
