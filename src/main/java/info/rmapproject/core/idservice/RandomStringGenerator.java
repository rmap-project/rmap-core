package info.rmapproject.core.idservice;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Class to generate a random string of characters
 * @author khanson
 *
 */
public class RandomStringGenerator {
	
	/** Characters that are valid for use in a random string generator. */
	private static char[] VALID_CHARACTERS =
		    "abcdefghijklmnopqrstuvwxyz0123456879".toCharArray();
	
	/**
	 * Generate a random string of the length defined
	 * This used to create API keys and secrets.
	 *
	 * @param numChars the length of the string of random characters
	 * @return the string
	 */
	public static String generateRandomString(int numChars) {
	  SecureRandom srand = new SecureRandom();
	    Random rand = new Random();
	    char[] buff = new char[numChars];

	    for (int i = 0; i < numChars; ++i) {
	      // reseed rand once you've used up all available entropy bits
	      if ((i % 10) == 0) {
	          rand.setSeed(srand.nextLong()); // 64 bits of random!
	      }
	      buff[i] = VALID_CHARACTERS[rand.nextInt(VALID_CHARACTERS.length)];
	    }
	    return new String(buff);
	  }
}

