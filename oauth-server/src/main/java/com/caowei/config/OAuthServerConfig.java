package com.caowei.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * OAuth2.0 服务器的核心配置类（授权服务器配置）
 * @author cwly1
 *
 */
@Configuration
@EnableAuthorizationServer  //这个注解告诉 Spring 这个应用是 OAuth2 的授权 服务器
public class OAuthServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;
	
	@Autowired
	RedisConnectionFactory redisConnectionFactory;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		//super.configure(clients);
		
		
		/**
		 * 客户端对象重要的属性有：
		 * clientId：（必须）客户端id。
		 * secret：（对于可信任的客户端是必须的）客户端的私密信息。
		 * scope：用来限制客户端的访问范围，如果为空（默认）的话，那么客户端拥有全部的访问范围。
		 * authorizedGrantTypes：授权给客户端使用的权限类型。默认值为空。
		 * authorities：此客户端可以使用的权限（基于Spring Security authorities）
		 */
		clients.inMemory()
		  /**
		   * client模式，没有用户的概念，直接与认证服务器交互，用配置中的客户端信息去申请accessToken，客户端有自己的client_id,
		   * client_secret对应于用户的username,password，而客户端也拥有自己的authorities，
		   * 当采取client模式认证时，对应的权限也就是客户端自己的authorities。
		   * 如：http://localhost:8080/oauth/token?grant_type=client_credentials&scope=select&client_id=client_1&client_secret=123456
		   */
		  .withClient("client_1") //客户端ID
		  //.resourceIds("users-info")  //这个资源服务的ID，这个属性是可选的，但是推荐设置并在授权服务中进行验证。	 
		  /**
		   * authorization_code：授权码类型。
		   * implicit：隐式授权类型。
		   * password：资源所有者（即用户）密码类型。
		   * client_credentials：客户端凭据（客户端ID以及Key）类型。
		   * refresh_token：通过以上授权获得的刷新令牌来获取新的令牌。
		   */
		  .authorizedGrantTypes("authorization_code","refresh_token")	
		  .scopes("read")  //无固定值，无取值范围，可以任意写。 请求url中包含的scopes参数的值，必须与此处设置值相同。
		  //.authorities("ROLE_USER")  //此客户端可以使用的权限（基于Spring Security authorities）
		  .secret(new BCryptPasswordEncoder().encode("123456"))
		  //http://localhost:8080/oauth/authorize?client_id=client_1&response_type=code&scopes=read&redirect_uri=http://localhost:8083/getoauthinfo
		  .redirectUris("http://localhost:8083/login/oauth2/code/aaa","https://www.getpostman.com/oauth2/callback")  //https://www.getpostman.com/oauth2/callback
		  //redirectUris 关于这个配置项，是在 OAuth2 协议 中，认证成功后的回调地址，因为稍后我们会使用 Postman 作为 测试 工具，
		  //故此处值固定为 https://www.getpostman.com/oauth2/callback ,此值同样可以配置多个
		  /**
		   * 重要：关于授权码类型的redirectUris配置，初始化时至少提供一个回调地址。
		   * OAuth服务器的目的是为众多应用提供用户授权认证的，如果服务器初始化时一个能正常使用认证应用也没有，服务器也失去了存在的意义。
		   * 这里一个重定向url地址，就相当于一个应用，有多少重定向url，就相当于支持多少个应用使用本服务器提供的oauth授权验证。
		   * 举个例子：如腾讯QQ提供的oauth授权验证，也不是随便一个第三应就能使用的。肯定得需要向腾讯申请开通oauth,并提供一个回调重定向url
		   * 供腾讯审核，审核通过即意味着此重定向url添加到了腾讯oauth授权服务器的重定向列表里边。
		   * 同理，每一家oauth授权认证服务器，也不每个第三方应用随时使用的。
		   * 问题：如果在不重新启动oauth服务情况下，动态添加扩展重定向url？？（我还不会！！！）   小问题会着时间自己消失，大问题你无可奈何。
		   * 扩展思维：一个大型的第三方软件商，假设旗下有100个独立的软件，都想集成腾讯的QQ oauth授权验证，并不需要每个都申请一次，只要提供
		   * 一个重定向url给腾讯oauth服务器，所有软件走这个url登录即可。
		   */
		  .and()
		  /**
		   * password模式，自己本身有一套用户体系，在认证时需要带上自己的用户名和密码，以及客户端的client_id,client_secret。
		   * 此时，accessToken所包含的权限是用户本身的权限，而不是客户端的权限。
		   * 注：认证服务器只有在其他授权模式无法执行的情况下，才能考虑使用这种模式。（用户名密码需要暴露给第三方应用程序，不安全）
		   */
		  .withClient("client_2")
		  .resourceIds("users-info")
		  .authorizedGrantTypes("password","refresh_token")
		  .scopes("read")
		  .authorities("ROLE_USER")
		  .secret(new BCryptPasswordEncoder().encode("123456"))
		  ;
		/**
		 * 模式说明：
		 * 我对于两种模式的理解便是，如果你的系统已经有了一套用户体系，每个用户也有了一定的权限，可以采用password模式；
		 * 如果仅仅是接口的对接，不考虑用户，则可以使用client模式。
		 */
		System.out.println("OAuthServerConfig ...");
	}
	
	/**
	 * 配置token存储方式。这里使用Redis存储。
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//super.configure(endpoints);
		endpoints
		 .tokenStore(new RedisTokenStore(redisConnectionFactory))
		 .authenticationManager(authenticationManager);
	}
	
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		//super.configure(security);
		//允许表认证
		security.allowFormAuthenticationForClients();
	}
}
