package controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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
	static void setup() {
		if (Secure.Security.isConnected()) {

			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			List<Question> questions = Question.find("order by voting desc")
					.fetch();

			Random rnd = new Random();
			boolean isSpam = user.isSpam();
			int value = rnd.nextInt(questions.size());
			// JW refacor
			HashSet<VotablePost> sQuests = new HashSet<VotablePost>();

			if (questions.size() > 0) {
				for (int i = 0; i < questions.size(); i++) {
					sQuests.addAll(questions.get(i).getNotAnsweredSimilarPosts(
							MINIMUM_TAGS, user.badgetags, user));
				}
			}

			ArrayList<VotablePost> sameQuestions = new ArrayList<VotablePost>(
					sQuests);
			renderArgs.put("user", user);
			renderArgs.put("sameQuestions", sameQuestions);
			renderArgs.put("isSpam", isSpam);
			renderArgs.put("canPost", user.canPost());
		}
	}

	/**
	 * Index.
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
	 * Show.
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
		List<VotablePost> taggedPosts = VotablePost.findTaggedWith(tag.name);
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