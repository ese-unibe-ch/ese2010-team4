package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

	/**
	 * import xml-files for A4Q
	 */
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

	/**
	 * search all posts who are marked as spam
	 */
	public static void showSpams() {
		List<Question> spamQuestion = Question.find("isSpam is true").fetch();
		List<Answer> spamAnswer = Answer.find("isSpam is true").fetch();
		render(spamQuestion, spamAnswer);
	}

	/**
	 * reset the status of a post to not a spam
	 */
	public static void unspamPost(Long id) {
		Post post = Post.findById(id);
		post.unspamPost();
		Admin.showSpams();
	}

	/**
	 * search all members of A4Q who are marked as spamer
	 */
	public static void showSpamer() {
		List<User> userList = User.find("isSpam is true or spamLock is true")
				.fetch();
		render(userList);
	}

	/**
	 * reset the status of a user to not a spamer
	 */
	public static void unspamUser(Long id) {
		User user = User.findById(id);
		user.unspamUser();
		List<Post> spams = Post.find("author.id is ? and isSpam is true", id)
				.fetch();
		for (Post p : spams) {
			p.unspamPost();
		}
		Admin.showSpamer();
	}

	/**
	 * reset the reputation of a user to zero, but not his badges
	 */
	public static void clearReputation(Long id) {
		User user = User.findById(id);
		user.clearWholeReputation();
		Admin.showSpamer();
	}

	/**
	 * lock a user (he will be a spamer directly, even he has not enough
	 * spamposts)
	 */
	public static void lockUser(long id) throws IOException {
		User user = User.findById(id);
		user.lockUser();
		Admin.showSpamer();
	}

	/**
	 * unlock user but not his posts (so maybe he is still a spamer)
	 */
	public static void unlockUser(long id) throws IOException {
		User user = User.findById(id);
		user.unlockUser();
		Admin.showSpamer();
	}

	/**
	 * render the main admin-page
	 */
	public static void index() {
		render("CRUD/index.html");
	}

}
