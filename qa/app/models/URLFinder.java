package models;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds ALL URLs in a given String and replace them either with a short form or
 * with HTML and the short form of the URL
 * 
 * @author d3orn
 * 
 */
public class URLFinder {

	private static String checkedContent;
	private static HashMap<String, String> URLMap = new HashMap();
	static Pattern pattern = Pattern
			.compile("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
	static Pattern htmlPattern = Pattern
			.compile("<a href=http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]</a>");

	/**
	 * Checkes a String <code>content</code> for URLs and replace them with HTML
	 * 
	 * @param content
	 * @return checkedContent
	 */
	public static String check(String content) {
		content = deleteAllHTMLAnchors(content);
		findAllURLS(content);
		replaceAllURLSWithHTML(content);
		return checkedContent;
	}

	private static String deleteAllHTMLAnchors(String content) {
		Matcher m = htmlPattern.matcher(content);
		while (m.find()) {
			Matcher n = pattern.matcher(content.substring(m.start(), m.end()));
			content = content.replace(content.substring(m.start(), m.end()),
					content.substring(n.start(), n.end()));
			System.out.println(content);
		}
		return content;
	}

	@ForTestingOnly
	public static void findAllURLS(String content) {
		Matcher m = pattern.matcher(content);
		while (m.find()) {
			String substring = content.substring(m.start(), m.end());
			String shortURL = shortenURL(substring);
			URLMap.put(substring, shortURL);
		}
	}

	@ForTestingOnly
	public static String shortenURL(String url) {
		if (url.length() < 30)
			return url;
		else {
			return url.substring(0, 27) + "...";
		}
	}

	@ForTestingOnly
	public static void replaceAllURLS(String content) {
		checkedContent = content;
		for (String key : URLMap.keySet()) {
			checkedContent = checkedContent.replace(key, URLMap.get(key));
		}
		URLMap.clear();
	}

	@ForTestingOnly
	public static void replaceAllURLSWithHTML(String content) {
		checkedContent = content;
		for (String key : URLMap.keySet()) {
			checkedContent = checkedContent.replace(key, "<a href=" + key
					+ " target=\"_blank\" title= \"" + key + "\">"
					+ URLMap.get(key) + "</a>");
		}
		URLMap.clear();
	}

	public static HashMap getURLMap() {
		return URLMap;
	}

	public static String getCheckedContent() {
		return checkedContent;
	}

}
