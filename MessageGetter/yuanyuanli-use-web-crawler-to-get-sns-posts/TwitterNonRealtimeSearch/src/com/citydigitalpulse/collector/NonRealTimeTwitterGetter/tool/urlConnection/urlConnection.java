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
package com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool.urlConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import com.citydigitalpulse.collector.NonRealTimeTwitterGetter.tool.ListOfUserAgent;

public class urlConnection {
	private static final String USER_AGENT = ListOfUserAgent.getUserAgent();

	public String getResponse(String url) {
		StringBuffer result = new StringBuffer();
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			// add request header
			request.addHeader("User-Agent", USER_AGENT);
			HttpResponse response = client.execute(request);
			int responsecode = response.getStatusLine().getStatusCode();
			// System.out.println("responsecode " + responsecode);
			if (responsecode == 200) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent(), "utf-8"));
				// Input Stream
				// Read the result
				String Line = "";
				while ((Line = reader.readLine()) != null) {
					result.append(Line);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
