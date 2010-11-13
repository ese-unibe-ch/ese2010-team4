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
	private long ownerID, questionId, answerId;
	private HashMap<Long, Long> userIdMap = new HashMap<Long, Long>();
	private HashMap<Long, Long> questionIdMap = new HashMap<Long, Long>();
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
			userIdMap.put(Long.parseLong(atts.getValue("id")), user.id);
			System.out.println(userIdMap);
			user.save();
			user.rating = new Reputation().save();
		}

		if (qname.equals("question")) {
			questionId = Long.parseLong(atts.getValue("id"));
		}

		if (qname.equals("answer")) {
			answerId = Long.parseLong(atts.getValue("id"));
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
			long searchId = userIdMap.get(ownerID);
			User u = User.findById(searchId);
			Question q = u.addQuestion(title, content).save();
			for (String tag : tags)
				q.tagItWith(tag).save();
			questionIdMap.put(questionId, q.id);
		}

		/*
		 * if (qname.equals("answer")) { long searchId = userIdMap.get(ownerID);
		 * User u = User.findById(searchId); searchId =
		 * questionIdMap.get(questionId); Question q =
		 * Question.findById(searchId); q.addAnswer(u, content); }
		 */

	}

	public void characters(char[] chars, int start, int length) {
		buf.append(chars, start, length);
	}
}
