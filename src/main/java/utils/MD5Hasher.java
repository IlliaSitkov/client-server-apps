package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class MD5Hasher {

	public static String getHash(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
	        md.update(str.getBytes());
	        byte[] digest = md.digest();
	        return DatatypeConverter.printHexBinary(digest).toUpperCase();
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
