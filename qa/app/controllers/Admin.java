package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import models.Answer;
import models.Post;
import models.Question;
import models.User;
import models.importer.XMLParser;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import controllers.Secure.Security;

@Check("admin")
@With(Secure.class)
public class Admin extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byUsername", Security.connected()).first();
			renderArgs.put("user", user.username);
		}
	}

	public static void importXML(File xml) throws FileNotFoundException,
			IOException {
		XMLParser parser = new XMLParser();
		URL url;
		String report = "";
		assert xml != null;

		try {
			url = xml.toURI().toURL();
			parser.processURL(url);
			report = parser.getReport();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		flash.success(parser.info());
		render(report);

	}

	public static void showSpams() {
		List<Question> spamQuestion = new ArrayList<Question>();
		List<Answer> spamAnswer = new ArrayList<Answer>();
		spamQuestion = Question.all().fetch();
		spamAnswer = Answer.all().fetch();

		/**
		 * for (Question p : spamQuestion) { if (!p.isSpam()) {
		 * spamQuestion.remove(p); } }
		 * 
		 * for (Answer a : spamAnswer) { if (!a.isSpam()) {
		 * spamAnswer.remove(a); } }
		 */

		render(spamQuestion, spamAnswer);
	}

	public static void unspamPost(Long id) {
		Post post = Post.findById(id);
		post.spamreport.clear();
		post.save();
		Admin.showSpams();
	}

	public static void showUsers() {
		List<User> userList = new ArrayList<User>();
		userList = User.all().fetch();
		render(userList);
	}

	public static void unspamUser(Long id) {
		User user = User.findById(id);
		user.spamreport.clear();
		user.save();
		Admin.showUsers();
	}

	public static void clearReputation(Long id) {
		Admin.showUsers();
	}

	public static void deleteUser(Long id) {
		User user = User.findById(id);
		// user.delete();
		// user.deleteAll();
		Admin.showUsers();
	}

	public static void index() {
		render("CRUD/index.html");
	}

}
