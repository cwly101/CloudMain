package com.caowei.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="email")
public class ReadConfigInfo {

	private Map<String,String> types;

	public Map<String, String> getTypes() {
		return types;
	}

	public void setTypes(Map<String, String> types) {
		this.types = types;
	}
	
}
