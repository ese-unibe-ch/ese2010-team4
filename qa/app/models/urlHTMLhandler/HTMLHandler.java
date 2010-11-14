package models.urlHTMLhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.ForTestingOnly;

public class HTMLHandler {
	private String checkedContent;
	private HashMap<String, String> HTMLMap;
	private List<String> anchorList;
	private Pattern htmlPattern = Pattern
			.compile("<a\\s[^>]*href\\s*=\\s*[\"\']?([^\"\' ]*)[\"\']?[^>]*>(.*?)</a>");
	private Pattern htmlLink = Pattern.compile("href=\"[^>]*\">");
	private Matcher htmlMatcher, linkMatcher;

	public HTMLHandler() {
		anchorList = new ArrayList<String>();
		HTMLMap = new HashMap<String, String>();
	}

	public String check(String content) {
		findHTMLAnchors(content);
		findHTMLAnchorReplacement(anchorList);
		replaceAllHTMLAnchors(content, HTMLMap);
		return checkedContent;
	}

	@ForTestingOnly
	public void findHTMLAnchors(String content) {
		htmlMatcher = htmlPattern.matcher(content);
		while (htmlMatcher.find()) {
			anchorList.add(content.substring(htmlMatcher.start(), htmlMatcher
					.end()));
		}
	}

	@ForTestingOnly
	public void findHTMLAnchorReplacement(List<String> anchorList) {
		for (String anchor : anchorList) {
			// this could be done easier with Aaron regex^^
			String link = anchor.replaceFirst("<a href=", "").replaceFirst(
					"</a>", "");
			if (link.contains(" "))
				link = link.substring(0, link.indexOf(" "));
			else if (link.contains(">")) {
				link = link.substring(0, link.indexOf(">"));
			}
			HTMLMap.put(anchor, link);
		}
	}

	@ForTestingOnly
	public void replaceAllHTMLAnchors(String content,
			HashMap<String, String> HTMLMap) {
		checkedContent = content;
		for (String key : HTMLMap.keySet()) {
			checkedContent = (checkedContent.replace(key, HTMLMap.get(key)));
		}
	}

	public HashMap<String, String> getHTMLMap() {
		return HTMLMap;
	}

	public void setHTMLMap(HashMap<String, String> HTMLMap) {
		this.HTMLMap = HTMLMap;
	}

	public List<String> getAnchorList() {
		return anchorList;
	}

	public void setAnchorList(List<String> anchorList) {
		this.anchorList = anchorList;
	}

	public void setCheckedContent(String checkedContent) {
		this.checkedContent = checkedContent;
	}

	public String getCheckedContent() {
		return checkedContent;
	}

}
