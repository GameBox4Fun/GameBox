package top.naccl.gamebox.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	public static String getMD5(String str) {
		byte[] digest = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			digest = md5.digest(str.getBytes("utf-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String md5String = new BigInteger(1, digest).toString(16);
		return md5String;
	}
}
