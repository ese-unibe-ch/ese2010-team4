package models.importer;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Answer;
import models.Question;
import models.Reputation;
import models.User;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML Parser but it's currently very slow - big file -> wait 30sec
 * 
 * @author d3orn
 * 
 */
public class XMLParser extends DefaultHandler {

	private User user;
	private int userCounter, questionCounter, answerCounter;
	private long ownerID, questionId, answerId;
	private HashMap<Long, Long> userIdMap = new HashMap<Long, Long>();
	private HashMap<Long, Long> questionIdMap = new HashMap<Long, Long>();
	private HashMap<Long, Long> answerIdMap = new HashMap<Long, Long>();
	private String content, title;
	private StringBuffer buf = new StringBuffer();
	private StringBuilder report = new StringBuilder();
	private ArrayList<String> tags = new ArrayList<String>();

	public void processURL(URL url) throws Exception {
		InputSource inputSource = new InputSource(url.openStream());
		SAXParserFactory fact = SAXParserFactory.newInstance();
		SAXParser parser = fact.newSAXParser();
		parser.parse(inputSource, this);
	}

	@Override
	public void startElement(String uri, String localName, String qname,
			Attributes atts) {
		buf.setLength(0);
		if (qname.equals("user")) {
			// I should check for multiple IDs...
			user = new User("", "", "");
			user.avatarPath = "/public/uploads/standardAvatar.png";
			user.save();
			// I should probably check if the id is already in use or not..
			userIdMap.put(Long.parseLong(atts.getValue("id")), user.id);
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

	@Override
	public void endElement(String uri, String localName, String qname) {
		if (qname.equals("user")) {
			userCounter++;
		}
		if (qname.equals("displayname")) {
			user.username = buf.toString();
			user.save();
		}
		if (qname.equals("email")) {
			user.email = buf.toString();
			user.save();
		}

		if (qname.equals("avatar")) {
			user.avatarPath = buf.toString();
			user.save();
		}
		if (qname.equals("password")) {
			user.password = buf.toString();
			user.save();
		}
		if (qname.equals("ismoderator")) {
			user.isAdmin = buf.toString().equals("true");
			user.save();
		}
		if (qname.equals("ownerid")) {
			this.ownerID = Long.parseLong(buf.toString());
		}

		if (qname.equals("questionid")) {
			this.questionId = Long.parseLong(buf.toString());
		}
		if (qname.equals("body")) {
			String body = buf.toString();
			if (body.contains("<![CDATA[")) {
				this.content = body.replace("<![CDATA[", "");
				this.content = this.content.substring(0,
						this.content.length() - 3);
			} else
				this.content = body;
		}

		if (qname.equals("title")) {
			this.title = buf.toString();
		}

		if (qname.equals("tag")) {
			this.tags.add(buf.toString());
		}

		if (qname.equals("question")) {
			if (!userIdMap.containsKey(ownerID)) {
				report.append("ERROR: question {" + questionId
						+ "} could not be imported - no owner found \n");
			} else {
				long searchId = userIdMap.get(ownerID);
				User u = User.findById(searchId);
				Question q = u.addQuestion(title, content).save();
				for (String tag : tags)
					q.tagItWith(tag).save();
				tags.clear();
				questionIdMap.put(questionId, q.id);
				questionCounter++;
			}
		}

		if (qname.equals("answer")) {
			if (!userIdMap.containsKey(ownerID)) {
				report.append("ERROR: answer {" + answerId
						+ "} could not be imported - no owner found \n");
			} else if (!questionIdMap.containsKey(questionId)) {
				report.append("ERROR: answer {" + answerId
						+ "} could not be imported - question not found \n");
			} else {
				long searchId = userIdMap.get(ownerID);
				User u = User.findById(searchId);
				searchId = questionIdMap.get(questionId);
				Question q = Question.findById(searchId);
				Answer a = new Answer(q, u, content).save();
				q.addNewAnswer(a).save();
				answerIdMap.put(answerId, a.id);
				answerCounter++;
			}
		}

		if (qname.equals("QA")) {
			System.out.println(info());
		}
	}

	public String info() {
		return userCounter + " users, " + questionCounter + " questions and "
				+ answerCounter + " answers have been added";
	}

	public String getReport() {
		return report.toString();
	}

	@Override
	public void characters(char[] chars, int start, int length) {
		buf.append(chars, start, length);
	}
}
