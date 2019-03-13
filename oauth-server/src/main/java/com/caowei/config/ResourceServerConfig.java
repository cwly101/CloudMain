package com.caowei.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 *  OAuth2.0资源服务器配置 (指定了受资源服务器保护的资源链接)
 *  配置类作用：携带token访问受限资源，自然便是与@EnableResourceServer相关的资源服务器配置了
 *  注：资源服务器可以和授权服务器是同一个，也可以分开部署
 * @author cwly1
 *
 */
@Configuration
@EnableResourceServer 
@Order(3)  //必须配置，否则对受保护资源请求不会被OAuth2的拦截器拦截。授权资源服务器必须先于SecurityConfig启动，否则SecurityConfig会接手一切验证。
//扩展类以Adapter结尾的是适配器，以Configurer结尾的是配置器，以Builder结尾的是建造器，他们分别代表不同的设计模式，对设计模式有所了解可以更加方便理解其设计思路
public class ResourceServerConfig extends ResourceServerConfigurerAdapter { 
	
	/**
	 * 资源安全配置相关
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		//super.configure(resources);
		//resourceId 用于分配给可授予的clientId。 这个资源服务的ID，这个属性是可选的，但是推荐设置并在授权服务中进行验证。
        //stateless  标记以指示在这些资源上仅允许基于令牌的身份验证
        //tokenStore token的存储方式
		//参见：https://www.imooc.com/article/details/id/31041 
		resources.resourceId("users-info").stateless(true);
	}
	
	/**
	 * http安全配置相关
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		//super.configure(http);
		http
		//因为我们希望在UI中也可以访问受保护的资源，所以我们需要允许创建会话(默认情况下在2.0.6中禁用)
		 .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		 .and()
		 .requestMatchers() 
		 .antMatchers("/order/**","/product/**")  //授权oauth可访问的资源列表。不包含的资源url，将交由SecurityConfig接手，具体看其配置。
		 .and()
		 .authorizeRequests()
		 .antMatchers("/order/**","/product/**")  
		 .authenticated()  //配置order访问控制，必须认证后才能访问。
		 ;
		//重要：requestMatchers()、authorizeRequests()二者缺一不可。
		System.out.println("RescourceServerConfig ...");
	}
}
