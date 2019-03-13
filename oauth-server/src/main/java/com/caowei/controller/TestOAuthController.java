package com.caowei.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
}
