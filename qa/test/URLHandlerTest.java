
import java.util.HashMap;
import java.util.List;

import models.urlHTMLhandler.URLHandler;

import org.junit.Test;

import play.test.UnitTest;

public class URLHandlerTest extends UnitTest {

	private URLHandler uHandler = new URLHandler();
	private HashMap<String, String> checkMap;
	private List<String> checkAnchorList;

	@Test
	public void shouldFindAllURLS() {
		String content = "This is just for testing if the Finder findes all different URLS"
				+ " from http://d3orn.ch to https://github.com and probably ftp://drahm.ch";
		uHandler.findURLS(content);
		HashMap URLMap = uHandler.getURLMap();
		assertEquals(URLMap.size(), 3);
		assertTrue(URLMap.containsKey("http://d3orn.ch"));
		assertTrue(URLMap.containsKey("https://github.com"));
		assertTrue(URLMap.containsKey("ftp://drahm.ch"));
	}

	@Test
	public void shouldNotReplaceURLS() {
		String content = "This is just for testing if the Finder findes and replace all different URLS"
				+ " from http://d3orn.ch to ftp://drahm.ch and http://google.com";
		String result = "This is just for testing if the Finder findes and replace all different URLS"
				+ " from http://d3orn.ch to ftp://drahm.ch and http://google.com";
		uHandler.findURLS(content);
		uHandler.replaceAllURLS(content);
		assertEquals(uHandler.getCheckedContent(), result);
	} // Normal replacement w/out HTML

	@Test
	public void shouldReplaceURLS() {
		String content = "This is just for testing if the Finder findes and replace all different URLS"
				+ " from https://github.com/ese-unibe-ch/ese2010-team4/network and probably ftp://drahm1234567890-markus.ch";
		String result = "This is just for testing if the Finder findes and replace all different URLS"
				+ " from https://github.com/ese-unib... and probably ftp://drahm1234567890-marku...";
		uHandler.findURLS(content);
		uHandler.replaceAllURLS(content);
		assertEquals(uHandler.getCheckedContent(), result);
	}

	// replacement w/ HTML
	@Test
	public void shouldReplaceURLSWithHTML() {
		String content = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from http://d3orn.ch to https://github.com/ese-unibe-ch/ese2010-team4/network and probably ftp://drahm.ch";
		String result = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from <a href=http://d3orn.ch target=\"_blank\" title= \"http://d3orn.ch\">http://d3orn.ch</a> "
				+ "to <a href=https://github.com/ese-unibe-ch/ese2010-team4/network target=\"_blank\" title= \"https://github.com/ese-unibe-ch/ese2010-team4/network\">https://github.com/ese-unib...</a> and probably <a href=ftp://drahm.ch target=\"_blank\" title= \"ftp://drahm.ch\">ftp://drahm.ch</a>";
		uHandler.findURLS(content);
		uHandler.replaceAllURLSWithHTML(content);
		assertEquals(uHandler.getCheckedContent(), result);
	}

	@Test
	public void fullCheck() {
		String content = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from http://d3orn.ch to https://github.com/ese-unibe-ch/ese2010-team4/network and probably ftp://drahm.ch";
		String result = "This is just for testing if the Finder findes and replace all different URLS with HTML"
				+ " from <a href=http://d3orn.ch target=\"_blank\" title= \"http://d3orn.ch\">http://d3orn.ch</a> "
				+ "to <a href=https://github.com/ese-unibe-ch/ese2010-team4/network target=\"_blank\" title= \"https://github.com/ese-unibe-ch/ese2010-team4/network\">https://github.com/ese-unib...</a> and probably <a href=ftp://drahm.ch target=\"_blank\" title= \"ftp://drahm.ch\">ftp://drahm.ch</a>";
		content = uHandler.check(content);
		assertEquals(content, result);
	}

}
