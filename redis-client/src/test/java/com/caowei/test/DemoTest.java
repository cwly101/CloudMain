package com.caowei.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoTest {

	@Value("${spring.security.oauth2.client.provider.accessTokenUri}")
	private String clientSecret;
	
	@Test
	public void deme() {
		System.out.println("值："+clientSecret);
	}
}
