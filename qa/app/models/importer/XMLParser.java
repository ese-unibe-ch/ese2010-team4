package models.importer;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Reputation;
import models.User;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

	private User user;
	private long ownerID;
	private String content, title;
	private StringBuffer buf = new StringBuffer();

	public void processURL(URL url) throws Exception {
		InputSource inputSource = new InputSource(url.openStream());
		SAXParserFactory fact = SAXParserFactory.newInstance();
		SAXParser parser = fact.newSAXParser();
		parser.parse(inputSource, this);
	}

	public void startElement(String uri, String localName, String qname,
			Attributes atts) {
		buf.setLength(0);
		if (qname.equals("user")) {
			user = new User();
			// user.setId(Long.parseLong(atts.getValue("id")));
			user.save();
			System.out.println(user.id);
			user.rating = new Reputation().save();
		}

	}

	public void endElement(String uri, String localName, String qname) {
		if (qname.equals("displayname")) {
			user.fullname = buf.toString();
			user.save();
		}
		if (qname.equals("email")) {
			user.email = buf.toString();
			user.save();
		}
		if (qname.equals("password")) {
			user.password = buf.toString();
			user.save();
		}
		if (qname.equals("ownerid")) {
			this.ownerID = Long.parseLong(buf.toString());
		}
		if (qname.equals("body")) {
			this.content = buf.toString();
		}

		if (qname.equals("title")) {
			this.title = buf.toString();
		}

		/*
		 * if (qname.equals("question")) { // System.out.println(title); User u
		 * = User.findById(ownerID); // System.out.println(u);
		 * user.addQuestion(title, content).save(); }
		 */
	}

	public void characters(char[] chars, int start, int length) {
		buf.append(chars, start, length);
	}
}
