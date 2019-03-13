package com.caowei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class Client1Application {

	public static void main(String[] args) {
		SpringApplication.run(Client1Application.class, args);
		System.out.println("======= client-1 started! ===========");
		
	}

}

