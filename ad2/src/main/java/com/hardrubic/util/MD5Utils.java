package com.hardrubic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	
	public static String getDigest(String txt) {
		byte[] hash;
	    try {
	        hash = MessageDigest.getInstance("MD5").digest(txt.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
	}

	/**
	 * 计算文件的md5的值
	 *
	 * @param file
	 *            传入文件的实例
	 * @return 文件的MD5
	 */
	public static String calculate(final File file) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("MD5");
			final FileInputStream fis = new FileInputStream(file);
			final byte[] buffer = new byte[8192];
			int read = 0;
			while ((read = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, read);
			}
			fis.close();
			byte[] b = digest.digest();
			StringBuilder result = new StringBuilder();
			for (byte element : b) {
				result.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
			}
			return result.toString();
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
