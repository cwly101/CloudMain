package com.caowei.common;

public class UnicodeUtil {
	
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

}
