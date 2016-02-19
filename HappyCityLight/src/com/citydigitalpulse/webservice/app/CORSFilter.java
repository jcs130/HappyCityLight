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

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext request,
			ContainerResponseContext response) throws IOException {
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		response.getHeaders().add("Access-Control-Allow-Headers",
				"origin, content-type, accept, authorization");
		response.getHeaders().add("Access-Control-Allow-Credentials", "true");
		response.getHeaders().add("Access-Control-Allow-Methods",
				"GET, POST, PUT, DELETE, OPTIONS, HEAD");
	}
}