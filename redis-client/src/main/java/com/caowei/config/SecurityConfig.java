package com.caowei.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		   .anyRequest().authenticated()
		   .and()
		   .oauth2Login()    // 1. 使用oauth2认证
		   .and()
		   .logout()             // 2. 支持注销
		   .and()
		   .csrf().disable()
		   ;
	}
	
	
	
//	@Autowired
//	OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter;
//	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		//super.configure(http);
//		http		 
//		 .addFilterBefore(oAuth2ClientAuthenticationProcessingFilter, BasicAuthenticationFilter.class)
//		 .csrf().disable()
//		 .authorizeRequests()
//		 .anyRequest().permitAll()
//		 .and()
//		 ;
//	}
	
	
}
