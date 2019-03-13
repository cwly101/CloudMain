package com.caowei.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.caowei.service.autocomplete.AutoCompleteService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AutoCompleteTest {
	
	@Autowired
	AutoCompleteService autoCom;
	
	@Test
	public void autocomplete_prefixTest() {
		String prefix="王".toLowerCase();
		autoCom.autocomplete_prefix(prefix);
//		String zh_to_unicode=codeUnicode("安");	 	
//		System.out.println(zh_to_unicode);
//		String unicode_to_zh=decodeUnicode(zh_to_unicode);
//		System.out.println(unicode_to_zh);
//		
//		String[] start_end_position=findPrefixRange(zh_to_unicode);
//		System.out.println(start_end_position[0]+","+start_end_position[1]);
	}
	
	
	private static final String VALID_CHARACTERS = ",-0123456789abcdefg";
	
	/**
	 * 中文字符串编码为16进制字符串
	 * 说明：英文字符或中文字符均支持，全部转换为16进制字符串
	 * @param gbString
	 * @return
	 */
	public static String codeUnicode(String gbString) {
		if (gbString == null) {
			return "";
		}
		char[] utfChar = gbString.toCharArray();
		String unicodeString = "";
		for (int i=0; i<utfChar.length; i++) {
			String hexB = Integer.toHexString(utfChar[i]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeString = unicodeString + "-" + hexB;
		}
		return unicodeString;
	}
	
	/**
	 * 解码转换回中文
	 * @param unicodeString
	 * @return
	 */
	public static String decodeUnicode(String unicodeString) {
		if (unicodeString == null) {
			return "";
		}
		String[] unicodeArray = unicodeString.split("-");
		StringBuffer stringBuffer = new StringBuffer();
		if (unicodeArray != null && unicodeArray.length > 0) {
			for (int i=0; i<unicodeArray.length; i++) {
				String arrayString = unicodeArray[i].trim();
				if (arrayString != null && !"".equals(arrayString)) {
					char c = (char) Integer.parseInt(arrayString, 16);
					stringBuffer.append(new Character(c).toString());
				}
			}
		}
		return stringBuffer.toString();
	}
	
	
	/**
	 * 根据给出的前缀字符串计算出查找发范围
	 * 原理：
	 * 在redis的有序集合里面，当所有成员的分值都相等时，有序集合将按照成员的名字进行排序；而当所有成员的分值都是0时，成员按照字符串的二进制顺序排序；
	 * 因为假系人的姓名先转换为了16进制字符串，那么比-小的字符是',',比'f'大的字符是'g'；
	 * 所以假如prefix='-00ff'，那么所有已-00ff开头的字符串都在'-00feg'到'-00ffg'之间
	 * @param prefix
	 * @return
	 */
	private String[] findPrefixRange(String prefix) {
		int posn = VALID_CHARACTERS.indexOf(prefix.charAt(prefix.length() - 1));	//查找出前缀字符串最后一个字符在列表中的位置
                char suffix = VALID_CHARACTERS.charAt(posn > 0 ? posn - 1 : 0);				//找出前驱字符
                String start = prefix.substring(0, prefix.length() - 1) + suffix + 'g';		//生成前缀字符串的前驱字符串
                String end = prefix + 'g';													//生成前缀字符串的后继字符串
                return new String[]{start, end};
	}
}
