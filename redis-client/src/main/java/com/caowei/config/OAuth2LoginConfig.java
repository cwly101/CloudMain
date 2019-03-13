package com.caowei.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
public class OAuth2LoginConfig {

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(clientRegistration());
	}
	
	@Value("${spring.security.oauth2.client.clientId}")
	String clientId;
	@Value("${spring.security.oauth2.client.provider.clientSecret}")
	String clientSecret;
	
	private ClientRegistration clientRegistration() {
		return ClientRegistration.withRegistrationId("aaa")  // (1)
                .clientId(clientId)  // (2)
                .clientSecret(clientSecret)  // (3)
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)  // (4)  服务端支持的认证方式
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")  // (5)
                .clientName("aaa")       // (6)
                .tokenUri("http://localhost:8080/oauth/token")  // (7)
                .authorizationUri("http://localhost:8080/oauth/authorize")  // (8)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)  // (9)
                .scope("read")  // (10)
                .userNameAttributeName("username")  // (11)
                .userInfoUri("http://localhost:8080/order/32")  // (12)
                .jwkSetUri("")  // (13)
                .build();
	}
}
