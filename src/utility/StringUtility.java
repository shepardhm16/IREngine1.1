package utility;

import indexer.Stemmer;

public class StringUtility {

	public static String refineToken(Stemmer stemmer, String str) {
		String token = str.toLowerCase().trim();
		token = token.replaceAll("[^a-zA-Z]+","");
		stemmer.add(token.toCharArray(), token.length());
		stemmer.stem();
		token = stemmer.toString();
		return isStopWord(token) ? null : token;
	}
	
	/*********************
	 * EXTRA POINTS 20 points
	 * Returns true if the str is one of stop words
	 * Returns flase if the str is not one of strop words
	 * build stopwords list and complete the fuction.
	 */
	private static boolean isStopWord(String  str) {
		boolean b = false;
		
		return b;
		
	}
}

