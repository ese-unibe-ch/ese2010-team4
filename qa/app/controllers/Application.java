package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.Post;
import models.Question;
import models.Tag;
import models.User;
import models.helper.ValidationHelper;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * A controller for the index.
 * 
 */
public class Application extends Controller {

	private static ValidationHelper helper = new ValidationHelper();
	private final static int MINIMUM_TAGS = 1;

	@Before
	static void setConnectedUser() {
		if (Secure.Security.isConnected()) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			renderArgs.put("user", user);
		}
	}

	@Before
	static void getSameQuestions() {

		if (Secure.Security.isConnected()) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			Random rnd = new Random();
			List<Question> questions = Question.find("order by voting desc")
					.fetch();
			int value = rnd.nextInt(questions.size());
			List<Post> sameQuestions = questions.get(value)
					.getNotAnsweredSimilarPosts(MINIMUM_TAGS, user.badgetags,
							user);
			renderArgs.put("sameQuestions", sameQuestions);
		}
	}

	@Before
	static void spam() {

		if (Secure.Security.isConnected()) {

			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			boolean isSpam = user.isSpam();
			renderArgs.put("isSpam", isSpam);

		}
	}

	@Before
	static void canPost() {

		if (Secure.Security.isConnected()) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			renderArgs.put("canPost", user.canPost());
		}

	}

	/**
	 * Index.
	 */
	public static void index() {
		String randomID = Codec.UUID();
		Post lastActivity = Post.find("order by timestamp desc").first();
		List<Question> questions = Question.find("order by voting desc")
				.fetch();

		String lastAnswer = "";
		boolean isconnected = Secure.Security.isConnected();
		User user = User.find("byUsername", Secure.Security.connected())
				.first();

		render(lastActivity, questions, lastAnswer, isconnected, user, randomID);
	}

	/**
	 * Show.
	 * 
	 * @param id
	 *            the id
	 */
	public static void show(Long id) {

		Question question = Question.find("byId", id).first();
		Post lastActivity = Post.find("order by timestamp desc").first();
		boolean abletovote = false;
		boolean hasTimeToChange = false;
		boolean isfollowing = false;

		if (!Secure.Security.isConnected()) {
			render(question, hasTimeToChange, abletovote, isfollowing);
		}

		else {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			abletovote = user.isAbleToVote(id);
			System.out.println("Frage datum und hatbesteAntwort"+question.validity +" " +question.hasBestAnswer);
			hasTimeToChange = user.hasTimeToChange(id);
			System.out.println("wert: "+hasTimeToChange);
			isfollowing = user.isFollowing(question);
			ArrayList<Post> sameAnswerQuestions = question
					.getNotAnsweredSimilarPosts(MINIMUM_TAGS, user.badgetags,
							user);

			render(question, hasTimeToChange, abletovote, isfollowing,
					lastActivity, sameAnswerQuestions);
		}
	}

	/**
	 * Adds the user.
	 * 
	 * @param fullname
	 *            the fullname
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 * @param password2
	 *            the password2
	 * @throws Throwable
	 */
	public static void addUser(@Required String newusername,
			@Required String email, @Required String password,
			@Required String password2, @Required String code, String randomID)
			throws Throwable {

		// They do not work imo
		validation.isTrue(helper.ckeck(newusername, "Username")).message(
				"Username already excists");

		validation.isTrue(helper.ckeck(email, "Email")).message(
				"Email not valid");
		validation.equals(password, password2).message("Password not equals");
		validation.isTrue(password.length() >= 6).message("Password to short");
		validation.equals(code, Cache.get(randomID)).message(
				"Invalid code. Please type it again");
		if (validation.hasErrors()) {
			flash.error(validation.errors().get(0).message());
			Secure.redirectToOriginalURL();
		}

		String message = User.createUser(newusername, email, password,
				password2);
		flash.success(message);

		try {
			Secure.login(message);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void tagged(Tag tag) {
		boolean isconnected = !Secure.Security.isConnected();
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		List<Post> taggedPosts = Post.findTaggedWith(tag.name);
		render(taggedPosts, isconnected, user);
	}

	public static void userExists(String username) {
		User user = User.find("byUsername", username).first();
		renderJSON(user != null);
	}

	public static void captcha(String id) {
		Images.Captcha captcha = Images.captcha();
		String code = captcha.getText("#E4EAFD");
		Cache.set(id, code, "10mn");
		renderBinary(captcha);
	}

}