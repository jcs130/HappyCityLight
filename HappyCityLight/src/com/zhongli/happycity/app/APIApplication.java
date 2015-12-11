package com.zhongli.happycity.app;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.zhongli.happycity.api.MarkPageResource;
import com.zhongli.happycity.api.UserResource;

public class APIApplication extends ResourceConfig {
	public APIApplication() {
		// 加载Resource
		register(UserResource.class);
		register(MarkPageResource.class);
		// 注册数据转换器
		register(JacksonJsonProvider.class);
		// Logging.
		register(LoggingFilter.class);
	}
}