package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Post;
import models.Question;
import models.Tag;
import models.User;
import models.VotablePost;
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
	/**
	 * Initial data. These datas are used in every html file.
	 */
	static void setup() {
		if (Secure.Security.isConnected()) {

			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			boolean isSpam = user.isSpam();
			renderArgs.put("user", user);
			renderArgs.put("sameQuestions", user
					.getSimilairQuestions(MINIMUM_TAGS));
			renderArgs.put("isSpam", isSpam);
			renderArgs.put("canPost", user.canPost());
			renderArgs.put("logduser", user);
		}
	}

	/**
	 * Index. Load orders for the index.html
	 */
	public static void index() {
		String randomID = Codec.UUID();
		Post lastActivity = VotablePost.find("order by timestamp desc").first();
		List<Question> questions = Question.find("order by voting desc")
				.fetch();

		String lastAnswer = "";
		boolean isconnected = Secure.Security.isConnected();
		User user = User.find("byUsername", Secure.Security.connected())
				.first();

		render(lastActivity, questions, lastAnswer, isconnected, user, randomID);
	}

	/**
	 * Shows a specific question in A4Q.
	 * 
	 * @param id
	 *            the id
	 */
	public static void show(Long id) {

		Question question = Question.find("byId", id).first();
		Post lastActivity = VotablePost.find("order by timestamp desc").first();
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
			hasTimeToChange = user.hasTimeToChange(id);
			isfollowing = user.isFollowing(question);
			ArrayList<VotablePost> sameAnswerQuestions = question
					.getNotAnsweredSimilarPosts(MINIMUM_TAGS, user.badgetags,
							user);

			render(question, hasTimeToChange, abletovote, isfollowing,
					lastActivity, sameAnswerQuestions);
		}
	}

	/**
	 * Adds an new user to A4Q.
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

	/**
	 * Shows all post with a specific tag
	 * 
	 * @param tag
	 */
	public static void tagged(Tag tag) {
		boolean isconnected = !Secure.Security.isConnected();
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		List<VotablePost> taggedPosts = VotablePost.findTaggedWith(tag.name);
		render(taggedPosts, isconnected, user);
	}

	/**
	 * Render user for JSON. This is necessary for the reputationgraph.
	 * 
	 * @param username
	 */
	public static void userExists(String username) {
		User user = User.find("byUsername", username).first();
		renderJSON(user != null);
	}

	/**
	 * Adds a captcha to the userlogin.
	 * 
	 * @param id
	 *            random
	 */
	public static void captcha(String id) {
		Images.Captcha captcha = Images.captcha();
		String code = captcha.getText("#E4EAFD");
		Cache.set(id, code, "10mn");
		renderBinary(captcha);
	}

}