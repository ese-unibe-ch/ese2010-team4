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
	private StringBuffer buf = new StringBuffer();

	public void processURL(URL url) throws Exception {
		InputSource inputSource = new InputSource(url.openStream());
		SAXParserFactory fact = SAXParserFactory.newInstance();
		SAXParser parser = fact.newSAXParser();
		parser.parse(inputSource, this);
	}

	public void startElement(String uri, String localName, String qname,
			Attributes attributes) {
		buf.setLength(0);
		if (qname.equals("user")) {
			user = new User();
			user.save();
			user.raiting = new Reputation().save();
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
	}

	public void characters(char[] chars, int start, int length) {
		buf.append(chars, start, length);
	}
}
