package com.zhongli.happycity.app;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

import com.zhongli.happycity.api.UserResource;

public class APIApplication extends ResourceConfig {
	public APIApplication() {
		// 加载Resource
		register(UserResource.class);
		// Logging.
		register(LoggingFilter.class);
	}
}