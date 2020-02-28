package com.moe.x4jdm.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Base64;

public class Md5
{
	public static String encode(String data,String salt){
		/*try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			return hex(md.digest((data+salt).getBytes()));
			
		}
		catch (NoSuchAlgorithmException e)
		{}*/
		return hex(Base64.encode((data+salt).getBytes(),Base64.DEFAULT));
		//return null;
	}
	public static String hex(byte[] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; ++i) {
			sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}
}
