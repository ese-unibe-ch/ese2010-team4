package models;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLFinder {

	private static String checkedContent, originalContent;
	private static HashMap<String, String> URLMap = new HashMap();
	static Pattern pattern = Pattern
			.compile("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

	public static String check(String content) {
		originalContent = content;
		findAllURLS(originalContent);
		replaceAllURLS(originalContent);
		System.out.println(checkedContent);
		return originalContent;
	}

	private static void findAllURLS(String content) {
		Matcher m = pattern.matcher(content);
		while (m.find()) {
			String substring = content.substring(m.start(), m.end());
			String shortURL = shortenURL(substring);
			URLMap.put(substring, shortURL);
		}
		System.out.println(URLMap);
	}

	private static String shortenURL(String url) {
		if (url.length() < 30)
			return url;
		else {
			return url.substring(0, 27) + "...";
		}
	}

	private static void replaceAllURLS(String content) {
		for (String key : URLMap.keySet()) {
			originalContent = originalContent.replace(key, URLMap.get(key));
		}

	}
}
