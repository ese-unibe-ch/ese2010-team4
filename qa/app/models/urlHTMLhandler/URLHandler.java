package models.urlHTMLhandler;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.ForTestingOnly;

/**
 * Finds ALL URLs in a given String and replace them either with a short form or
 * with HTML and the short form of the URL
 * 
 * @author d3orn
 * 
 */
public class URLHandler implements IHandler {

	private String checkedContent;
	private URLShortner uShortner;
	private HashMap<String, String> URLMap;
	static Pattern URLPattern = Pattern
			.compile("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

	private Matcher htmlMatcher, linkMatcher;

	public URLHandler() {
		URLMap = new HashMap<String, String>();
		uShortner = new URLShortner(30);
	}

	/**
	 * Checkes a String <code>content</code> for URLs and replace them with HTML
	 * 
	 * @param content
	 * @return checkedContent
	 */
	public String check(String content) {
		findURLS(content);
		replaceAllURLSWithHTML(content);
		return checkedContent;
	}

	@ForTestingOnly
	public void findURLS(String content) {
		Matcher m = URLPattern.matcher(content);
		while (m.find()) {
			String substring = content.substring(m.start(), m.end());
			String shortURL = uShortner.check(substring);
			URLMap.put(substring, shortURL);
		}
	}

	@ForTestingOnly
	public void replaceAllURLS(String content) {
		checkedContent = content;
		for (String key : URLMap.keySet()) {
			checkedContent = checkedContent.replace(key, URLMap.get(key));
		}
		URLMap.clear();
	}

	@ForTestingOnly
	public void replaceAllURLSWithHTML(String content) {
		checkedContent = content;
		for (String key : URLMap.keySet()) {
			checkedContent = checkedContent.replace(key, "<a href=" + key
					+ " target=\"_blank\" title= \"" + key + "\">"
					+ URLMap.get(key) + "</a>");
		}
		URLMap.clear();
	}

	public HashMap getURLMap() {
		return URLMap;
	}

	public String getCheckedContent() {
		return checkedContent;
	}

}
