
import java.util.HashMap;
import java.util.List;

import models.urlHTMLhandler.HTMLHandler;

import org.junit.Test;

import play.test.UnitTest;

public class HTMLHandlerTest extends UnitTest {

	private HTMLHandler hHandler = new HTMLHandler();
	private HashMap<String, String> checkMap;
	private List<String> checkAnchorList;

	@Test
	public void findHTMLAnchors() {
		String content = "This is just for testing if the Finder findes all HTML Anchors"
				+ " <a href=http://d3orn.ch title= \"http://d3orn.ch\">http://d3orn.ch</a> and probably <a href=http://google.com>http://google.com</a>";
		hHandler.findHTMLAnchors(content);
		checkAnchorList = hHandler.getAnchorList();
		assertEquals(checkAnchorList.size(), 2);
		assertEquals(checkAnchorList.get(0),
				"<a href=http://d3orn.ch title= \"http://d3orn.ch\">http://d3orn.ch</a>");
		assertEquals(checkAnchorList.get(1),
				"<a href=http://google.com>http://google.com</a>");
	}

	@Test
	public void findHTMLAnchorsReplacement() {
		String content = "This is just for testing if the Finder findes all HTML Anchors"
				+ " <a href=http://d3orn.ch title= \"http://d3orn.ch\">http://d3orn.ch</a> and probably <a href=http://google.com>http://google.com</a>";
		hHandler.findHTMLAnchors(content);
		checkAnchorList = hHandler.getAnchorList();
		hHandler.findHTMLAnchorReplacement(checkAnchorList);
		checkMap = hHandler.getHTMLMap();
		assertEquals(checkMap.size(), 2);
		assertEquals(
				checkMap
						.get("<a href=http://d3orn.ch title= \"http://d3orn.ch\">http://d3orn.ch</a>"),
				"http://d3orn.ch");
		assertEquals(checkMap
				.get("<a href=http://google.com>http://google.com</a>"),
				"http://google.com");
	}

	@Test
	public void replaceAllHTMLAnchors() {
		String content = "This is just for testing if the Finder findes all HTML Anchors"
				+ " <a href=http://d3orn.ch title= \"http://d3orn.ch\">http://d3orn.ch</a> and probably <a href=http://google.com>http://google.com</a>";
		hHandler.findHTMLAnchors(content);
		checkAnchorList = hHandler.getAnchorList();
		hHandler.findHTMLAnchorReplacement(checkAnchorList);
		checkMap = hHandler.getHTMLMap();
		hHandler.replaceAllHTMLAnchors(content, checkMap);
		assertEquals(hHandler.getCheckedContent(),
				"This is just for testing if the Finder findes all HTML Anchors"
						+ " http://d3orn.ch and probably http://google.com");
	}

	@Test
	public void allInOneCheck() {
		String content = "This is just for testing if the Finder findes all HTML Anchors"
				+ " <a href=http://d3orn.ch title= \"http://d3orn.ch\">http://d3orn.ch</a> and probably <a href=http://google.com>http://google.com</a>";
		String result = "This is just for testing if the Finder findes all HTML Anchors"
				+ " http://d3orn.ch and probably http://google.com";
		content = hHandler.check(content);
		assertEquals(content, result);
	}
}
