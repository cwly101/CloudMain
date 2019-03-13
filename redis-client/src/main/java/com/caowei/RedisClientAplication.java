package com.caowei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableTransactionManagement  启用事务。jdbc和jpa实现了事务管理器接口类。必须引入二者其一。
@ConfigurationProperties
@EnableScheduling
public class RedisClientAplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RedisClientAplication.class, args);
		System.out.println("===== RedisClient 应用程序启动完成 =====");
	}

}
