package com.server_utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public static void main(String[] args){
		System.out.println(PasswordUtil.getInstance().createNewPwd());//a3i7j0h5
		System.out.println(PasswordUtil.getInstance().toMD5("a3i7j0h5"));//d205dd342bd620c00b817cc5a38005f8
	}
	
	
	public static PasswordUtil getInstance() {
		return new PasswordUtil();
	}
	
	/**
	 * Ëæ»úÉú³ÉÃÜÂë
	 * @return
	 */
	public String createNewPwd(){
		String pwd = "";
		for(int i = 0; i < 8; i++){
			if(i % 2 ==0){
				pwd += (char)((int)(Math.random() * 10) + 'a') + "";
			}
			else 
				pwd += (int)(Math.random() * 10);
		}
		return pwd;
	}

	private String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	private String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	public String toMD5(String pwd) {
		byte[] results_byte = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			results_byte = md5.digest(pwd.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return byteArrayToHexString(results_byte);
	}
}
