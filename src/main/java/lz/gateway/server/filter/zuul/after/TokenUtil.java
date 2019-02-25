package lz.gateway.server.filter.zuul.after;

import java.security.SecureRandom;

import lz.gateway.server.constant.Constant;

public class TokenUtil {


	private static final int DEFAULT_LENGTH = 6;
	
	private static final String[] BASE_STRING = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	
	private static final String[] BASE_ALPHA = { "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
			"T", "U", "V", "W", "X", "Y", "Z" };
	
	private static final String[] BASE_NUMBER = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9" };

	public static final String getRandomString() {
		return getRandomString(DEFAULT_LENGTH, Constant.SCOPE_WORD);
	}
	
	public static final String getRandomString(int len) {
		return getRandomString(len, Constant.SCOPE_WORD);
	}
	
	public static final String getRandomString(int len, String scope) {
		SecureRandom random = new SecureRandom();
		String[] seedString = new String[0];
		
		if (Constant.SCOPE_WORD.equals(scope)) {
			seedString = BASE_STRING;
		} else if (Constant.SCOPE_ALPHA.equals(scope)) {
			seedString = BASE_ALPHA;
		} else if (Constant.SCOPE_NUMBER.equals(scope)) {
			seedString = BASE_NUMBER;
		}
		
		int length = seedString.length;
		String randomString = "";
		for (int i = 0; i < length; i++) {
			randomString += seedString[random.nextInt(length)];
		}
		random = new SecureRandom();
		String resultStr = "";
		for (int i = 0; i < len; i++) {
			resultStr += randomString.charAt(random.nextInt(randomString.length() - 1));
		}
		return resultStr;
	}

 

}
