import java.util.HashMap;

import models.URLFinder;

import org.junit.Test;

import play.mvc.Before;
import play.test.UnitTest;

public class URLFinderTest extends UnitTest {

	URLFinder finder;

	@Before
	public void setUp() {
		finder = new URLFinder();
	}

	@Test
	public void shouldShortenURL() {
		String url = "https://github.com/ese-unibe-ch/ese2010-team4/network";
		String shortURL = finder.shortenURL(url);
		assertTrue(shortURL.length() <= 30);
		assertTrue(shortURL.length() < url.length());
		assertEquals(shortURL, "https://github.com/ese-unib...");
	}

	@Test
	public void shouldNotShortenURL() {
		String url = "http://d3orn.ch";
		String shortURL = finder.shortenURL(url);
		assertEquals(shortURL.length(), url.length());
		assertEquals(shortURL, url);
	}

	@Test
	public void shouldFindAllURLS() {
		String content = "This is just for testing if the Finder findes all different URLS"
				+ " from http://d3orn.ch to https://github.com and probably ftp://drahm.ch";
		finder.findAllURLS(content);
		HashMap URLMap = finder.getURLMap();
		assertEquals(URLMap.size(), 3);
		assertTrue(URLMap.containsKey("http://d3orn.ch"));
		assertTrue(URLMap.containsKey("https://github.com"));
		assertTrue(URLMap.containsKey("ftp://drahm.ch"));
	}

	// Normal replacement w/out HTML
	@Test
	public void shouldReplaceURLS() {
		String content = "This is just for testing if the Finder findes and replace all different URLS"
				+ " from http://d3orn.ch to https://github.com/ese-unibe-ch/ese2010-team4/network and probably ftp://drahm.ch";
		String result = "This is just for testing if the Finder findes and replace all different URLS"
				+ " from http://d3orn.ch to https://github.com/ese-unib... and probably ftp://drahm.ch";
		finder.findAllURLS(content);
		finder.replaceAllURLS(content);
		assertEquals(finder.getCheckedContent(), result);
	}

	// replacement w/ HTML
	@Test
	public void shouldReplaceURLSWithHTML() {
		String content = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from http://d3orn.ch to https://github.com/ese-unibe-ch/ese2010-team4/network and probably ftp://drahm.ch";
		String result = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from <a href=http://d3orn.ch target=\"_blank\" title= \"http://d3orn.ch\">http://d3orn.ch</a> "
				+ "to <a href=https://github.com/ese-unibe-ch/ese2010-team4/network target=\"_blank\" title= \"https://github.com/ese-unibe-ch/ese2010-team4/network\">https://github.com/ese-unib...</a> and probably <a href=ftp://drahm.ch target=\"_blank\" title= \"ftp://drahm.ch\">ftp://drahm.ch</a>";
		finder.findAllURLS(content);
		finder.replaceAllURLSWithHTML(content);
		assertEquals(finder.getCheckedContent(), result);
	}

	@Test
	public void fullCheck() {
		String content = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from http://d3orn.ch to https://github.com/ese-unibe-ch/ese2010-team4/network and probably ftp://drahm.ch";
		String result = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from <a href=http://d3orn.ch target=\"_blank\" title= \"http://d3orn.ch\">http://d3orn.ch</a> "
				+ "to <a href=https://github.com/ese-unibe-ch/ese2010-team4/network target=\"_blank\" title= \"https://github.com/ese-unibe-ch/ese2010-team4/network\">https://github.com/ese-unib...</a> and probably <a href=ftp://drahm.ch target=\"_blank\" title= \"ftp://drahm.ch\">ftp://drahm.ch</a>";
		content = finder.check(content);
		assertEquals(content, result);
	}

	@Test
	public void shouldNotChangeHTMLAnchors() {
		String content = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from <a href=http://d3orn.ch title= \"http://d3orn.ch\">http://d3orn.ch</a>";
		String result = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from <a href=http://d3orn.ch title= \"http://d3orn.ch\">http://d3orn.ch</a>";
		content = finder.check(content);
		assertEquals(content, result);
	}

}
