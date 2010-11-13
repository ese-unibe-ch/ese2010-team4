package models.importer;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Question;
import models.Reputation;
import models.User;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser extends DefaultHandler {

	private User user;
	private long ownerID;
	private HashMap<Long, Long> idMap = new HashMap<Long, Long>();
	private String content, title;
	private StringBuffer buf = new StringBuffer();
	private ArrayList<String> tags = new ArrayList<String>();

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
			user.save();
			// I should probably check if the id is already in use or not..
			idMap.put(Long.parseLong(atts.getValue("id")), user.id);
			System.out.println(idMap);
			user.save();
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

		if (qname.equals("tag")) {
			this.tags.add(buf.toString());
		}

		if (qname.equals("question")) {
			long searchId = idMap.get(ownerID);
			User u = User.findById(searchId);
			Question q = u.addQuestion(title, content).save();
			for (String tag : tags)
				q.tagItWith(tag).save();
		}

	}

	public void characters(char[] chars, int start, int length) {
		buf.append(chars, start, length);
	}
}
