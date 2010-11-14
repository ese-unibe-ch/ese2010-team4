
import models.urlHTMLhandler.URLShortner;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class URLShortnerTest extends UnitTest {

	URLShortner uShortner;

	@Before
	public void setUp() {
		uShortner = new URLShortner(30);
	}

	@Test
	public void shouldShortenURL() {
		String url = "https://github.com/ese-unibe-ch/ese2010-team4/network";
		String shortURL = uShortner.check(url);
		assertTrue(shortURL.length() <= 30);
		assertTrue(shortURL.length() < url.length());
		assertEquals(shortURL, "https://github.com/ese-unib...");
	}

	@Test
	public void shouldNotShortenURL() {
		String url = "http://d3orn.ch";
		String shortURL = uShortner.check(url);
		assertEquals(shortURL.length(), url.length());
		assertEquals(shortURL, url);
	}
}
