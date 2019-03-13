package com.caowei.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

//@Configuration
public class RestTempleteConfig {

	@Autowired
    RestTemplateBuilder builder;
	
	@Bean
	public RestTemplate	 restTemplate() {
		//return new RestTemplate(factory);
		//return builder.build();
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(1000);
		requestFactory.setReadTimeout(1000);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
}
