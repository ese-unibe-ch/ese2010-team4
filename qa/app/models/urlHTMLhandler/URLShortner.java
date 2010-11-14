package models.urlHTMLhandler;

import models.ForTestingOnly;

public class URLShortner {

	private int maxURLLenght;

	public URLShortner(int maxURLLenght) {
		this.maxURLLenght = maxURLLenght;
	}

	@ForTestingOnly
	public String check(String url) {
		if (url.length() < maxURLLenght)
			return url;
		else {
			return url.substring(0, maxURLLenght - 3) + "...";
		}
	}

}
