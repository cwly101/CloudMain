package com.caowei;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudMainApplicationTests {

	@Test
	public void contextLoads() {
		Calendar calendar = Calendar.getInstance();
		TimeZone timeZone = TimeZone.getTimeZone("GMT+08:00");
		calendar.setTimeZone(timeZone);
		System.out.println(calendar.getTimeInMillis()/1000);
		System.out.println(new Date().getTime()/1000);
	}

}

