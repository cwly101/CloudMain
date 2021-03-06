package com.caowei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestOAuthController {
	
	@GetMapping("/product/{id}")
	public String getProduct(@PathVariable String id) {
		Authentication authorization=SecurityContextHolder.getContext().getAuthentication();
		return "product id: "+id;
	}

	@GetMapping("/order/{id}")
	public String getOrder(@PathVariable String id) {
		Authentication authorization=SecurityContextHolder.getContext().getAuthentication();
		String username= authorization.getName();
		System.out.println(username);
		return "{\"username\":\""+username+"\"}";
	}
	
	/**
	 * 说明：暴露一个商品查询接口，后续不做安全限制，一个订单查询接口，后续添加访问控制。
	 */
	
	
	/**
	 * 配置客户端信息
	 */
	@Autowired
	ClientDetailsServiceConfigurer clients;
	
	/**
	 * 动态注册支持oauth2验证的第三方应用程序客户端
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/readconfig")
	public String readconfig() throws Exception {
		clients.inMemory()
		  .withClient("client_postman")
		  .secret(new BCryptPasswordEncoder().encode("my7366"))
		  .authorizedGrantTypes("authorization_code","refresh_token")	
		  .scopes("read") 
		  .redirectUris("https://www.getpostman.com/oauth2/callback")
		  ;
		return "read config";
	}
}
