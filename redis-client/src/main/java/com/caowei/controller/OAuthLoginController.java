package com.caowei.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

@RestController
public class OAuthLoginController {

	// @EnableOAuth2Client 搜索这个，解决oauth客户端验证

	@GetMapping("/home")
	public String home() {
		return "welcome to home!";
	}
	

	@GetMapping("/callback")
	public String deme(@RequestParam("code") String code) {
//		String url="";
//		oAuth2RestTemplate.postfor
//		String textString=oAuth2RestTemplate.postForLocation(url, String.class);
//		System.out.println(textString);
		System.out.println(code);
		return "hi,callback,"+code;
	}



//	@Value("${client.resourceServerUrl}")
//	String resourceServerUrl;
//
//	@GetMapping("/demo/{id}")
//	public String getDemoAuthResource(@PathVariable Long id) {
//		ResponseEntity<String> responseEntity = oAuth2RestTemplate.getForEntity(resourceServerUrl + "/demo/" + id,
//				String.class);
//		return responseEntity.getBody();
//	}

	@GetMapping(value = "/getoauthinfo")
	public void getOAuthServerInfo(@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "access_token", required = false) String access_token,
			@RequestParam(value = "token_type", required = false) String token_type,
			@RequestParam(value = "refresh_token", required = false) String refresh_token,
			@RequestParam(value = "expires_in", required = false) String expires_in) {
		System.out.println("code:" + code);
		System.out.println("access_token:" + access_token);
		System.out.println("token_type:" + token_type);
		System.out.println("refresh_token:" + refresh_token);
		System.out.println("expires_in:" + expires_in);

		// 第二步请求token
		String url = "http://localhost:8080/oauth/authorize?client_id=client_1&grant_type=authorization_code&code="
				+ code + "redirect_uri=http://localhost:8083/getoauthinfo";
		url = "http://localhost:8080/oauth/authorize";
//		JSONObject postData = new JSONObject();
//		postData.put("client_id", "client_1");
//		postData.put("grant_type", "authorization_code");
//		postData.put("code", code);
//		postData.put("redirect_uri", "http://localhost:8083/getoauthinfo");
//		
//		HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//        headers.setContentType(type);
//        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//        System.out.println(postData.toJSONString());     
//        HttpEntity<String> formEntity = new HttpEntity<String>(postData.toString(), headers);
//        String result = restTemplate.postForObject(url, formEntity, String.class);
//        System.out.println(result);

		// String url = "http://localhost/mirana-ee/app/login";
		RestTemplate client = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		// 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		// 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		// 也支持中文
		params.add("client_id", "client_1");
		params.add("grant_type", "authorization_code");
		params.add("code", code);
		params.add("redirect_uri", "http://localhost:8083/getoauthinfo");
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params,
				headers);
		// 执行HTTP请求
		ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, requestEntity, String.class);
		// 输出结果
		System.out.println(response.getBody());
	}

}
