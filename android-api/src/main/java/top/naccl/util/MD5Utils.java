package top.naccl.util;

import org.springframework.util.DigestUtils;

public class MD5Utils {
	public static String getMD5(String str) {
		return DigestUtils.md5DigestAsHex(str.getBytes());
	}

	public static void main(String[] args) {
		System.out.println(getMD5("123456"));
	}
}
