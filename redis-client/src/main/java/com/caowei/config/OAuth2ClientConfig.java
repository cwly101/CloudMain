package com.caowei.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.MultiValueMap;

//@Configuration
//@EnableOAuth2Client
public class OAuth2ClientConfig {
	
	@Value("${spring.security.oauth2.client.provider.pre-established-redirect-uri}")
	String redirectUri;
	@Value("${spring.security.oauth2.client.provider.check-token-access}")
	String checkTokenEndpointUrl;
	@Value("${spring.security.oauth2.client.provider.accessTokenUri}")
	String accessTokenUri;
	@Value("${spring.security.oauth2.client.clientId}")
	String clientId;
	@Value("${spring.security.oauth2.client.provider.clientSecret}")
	String clientSecret;
	@Value("${spring.security.oauth2.client.provider.userAuthorizationUri}")
	String userAuthorizationUri;
	
//	@Bean
//	public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext context,OAuth2ProtectedResourceDetails details) {
//		OAuth2RestTemplate template=new OAuth2RestTemplate(details,context);
//		
//		AuthorizationCodeAccessTokenProvider authCodeProvider=new AuthorizationCodeAccessTokenProvider();
//		authCodeProvider.setStateMandatory(false);
//		authCodeProvider.setAuthenticationHandler(new ClientAuthenticationHandler() {
//			
//			@Override
//			public void authenticateTokenRequest(
//					OAuth2ProtectedResourceDetails resource, 
//					MultiValueMap<String, String> form,
//					HttpHeaders headers) {
//				headers.set("Authorization", "Basic ");
//			}
//		});
//		
//		AccessTokenProviderChain provider=new AccessTokenProviderChain(Arrays.asList(authCodeProvider));
//		template.setAccessTokenProvider(provider);
//		return template;
//	}
//	
//	/**
//	 * 注册处理redirect uri的过滤器
//	 * @param oAuth2RestTemplate
//	 * @param tokenServices
//	 * @return
//	 */
//	@Bean
//	public OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter(
//			OAuth2RestTemplate oAuth2RestTemplate,
//			RemoteTokenServices tokenServices) {
//		System.out.println("oAuth2ClientAuthenticationProcessingFilter()... redirectUri:"+redirectUri);
//		OAuth2ClientAuthenticationProcessingFilter filter=new OAuth2ClientAuthenticationProcessingFilter(redirectUri);
//		filter.setRestTemplate(oAuth2RestTemplate);
//		filter.setTokenServices(tokenServices);
//		AuthenticationSuccessHandler successHandler=new SimpleUrlAuthenticationSuccessHandler() {
//			@Override
//			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//					Authentication authentication) throws IOException, ServletException {
//				this.setDefaultTargetUrl("/home");
//				super.onAuthenticationSuccess(request, response, authentication);
//			}
//		};
//		filter.setAuthenticationSuccessHandler(successHandler);
//		
//		return filter;
//	}
//	
//	@Bean
//	public RemoteTokenServices tokenServices(OAuth2ProtectedResourceDetails details) {
//		System.out.println("tokenServices()...");
//		System.out.println(checkTokenEndpointUrl);
//		System.out.println(details.getClientId()+","+details.getClientSecret()+","+details.getAccessTokenUri());
//		
//		RemoteTokenServices tokenServices=new RemoteTokenServices();
//		tokenServices.setCheckTokenEndpointUrl(details.getAccessTokenUri());
//		tokenServices.setClientId(details.getClientId());
//		tokenServices.setClientSecret(details.getClientSecret());
//		return tokenServices;
//	}
//	
//	@Bean
//    protected OAuth2ProtectedResourceDetails resource() {
//
//		//ClientCredentialsResourceDetails resource=new ClientCredentialsResourceDetails();
//		AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
//		resource.setAccessTokenUri(accessTokenUri);
//		resource.setClientId(clientId);
//		resource.setGrantType("authorization_code");
//		resource.setUserAuthorizationUri(userAuthorizationUri);  //获取token
//		resource.setClientSecret(clientSecret);		
//		resource.setTokenName("test_token");
//		resource.setPreEstablishedRedirectUri(redirectUri);
//		List<String> scope=new ArrayList<String>();
//		scope.add("read");
//		resource.setScope(scope);
//		//resource.setAuthenticationScheme(AuthenticationScheme.form);
//        return resource;
//    }

}
