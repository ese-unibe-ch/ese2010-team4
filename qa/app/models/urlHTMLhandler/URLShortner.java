package models.urlHTMLhandler;

import models.ForTestingOnly;

/**
 * Shortens a URL if it's lenght is greater than <code>maxURLLenght</code>
 * 
 * @author d3orn
 * 
 */
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
