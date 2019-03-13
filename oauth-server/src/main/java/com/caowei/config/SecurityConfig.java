package com.caowei.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security配置类
 * 扩展WebSecurityConfigurerAdapter（web安全配置适配器）类
 * @author cwly1
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//super.configure(auth);
		auth.inMemoryAuthentication()
		   .passwordEncoder(passwordEncoder())
		   .withUser("cw").password(passwordEncoder().encode("123456")).authorities("ROLE_USER")
		   .and()
		   .passwordEncoder(passwordEncoder())
		   .withUser("user2").password(passwordEncoder().encode("123456")).authorities("ROLE_USER")
		   ;
	}
	
	/**
	 * 作用：对用户授权时输入的登录密码采用指定方式进行加解密。
	 *       （这个配置必须有，否则抛：There is no PasswordEncoder mapped for the id "null" 异常）
	 * 说明：SpringBoot2.0抛弃了原来的NoOpPasswordEncoder，要求用户保存的密码必须要使用加密算法后存储，
	 * 在登录验证的时候Security会将获得的密码在进行编码后再和数据库中加密后的密码进行对比
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
	    // return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	    return new BCryptPasswordEncoder();
	    
	    //注：PasswordEncoderFactories.createDelegatingPasswordEncoder()方法默认返回值为BCryptPasswordEncoder()，两个return等价
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//super.configure(http);
		
		http.formLogin() //登记界面，默认是permit All
		.and()
		.authorizeRequests().antMatchers("/login").permitAll() //不用身份认证可以访问
		.and()
		.authorizeRequests().anyRequest()
		//.access("hasRole('USER') or hasRole('ADMIN')")  
		.hasRole("USER")  //区别就是access支持or、and等组合权限设置。
		//.authenticated() //其它的请求要求必须有身份认证
        .and()
        .csrf() //防止CSRF（跨站请求伪造）配置
        .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable();
		
		System.out.println("SecurityConfig ...");
	}
	
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
