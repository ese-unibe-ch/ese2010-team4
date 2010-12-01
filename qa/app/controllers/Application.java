package controllers;

import java.util.List;

import models.Post;
import models.Question;
import models.Tag;
import models.User;
import models.helper.ValidationHelper;
import play.data.validation.Required;
import play.i18n.Lang;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * A controller for the index.
 * 
 */
public class Application extends Controller {

	private static ValidationHelper helper = new ValidationHelper();

	@Before
	static void setConnectedUser() {
		if (Secure.Security.isConnected()) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			renderArgs.put("user", user);
		}
	}

	/**
	 * Index.
	 */
	public static void index() {
		Post lastActivity = Post.find("order by timestamp desc").first();
		List<Question> questions = Question.find("order by voting desc")
				.fetch();
		String lastAnswer = "";
		boolean isconnected = Secure.Security.isConnected();
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		render(lastActivity, questions, lastAnswer, isconnected, user);
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
			hasTimeToChange = user.hasTimeToChange(id);
			isfollowing = user.isFollowing(question);

			render(question, hasTimeToChange, abletovote, isfollowing,
					lastActivity);
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
	 */
	public static void addUser(@Required String newusername,
			@Required String email, @Required String password,
			@Required String password2) {
		validation.isTrue(helper.ckeck(newusername, "Username"));
		validation.isTrue(helper.ckeck(email, "Email"));
		validation.equals(password, password2);
		validation.isTrue(password.length() > 6);

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

	public static void selectLanguage(@Required String langId) {
		if (langId != null) {
			Lang.change(langId);
			if (!Lang.get().equals(langId))
				flash.error("Unknown language %s!", langId);
		} else
			flash.error("No language choosen");
		try {
			Secure.redirectToOriginalURL();
		} catch (Throwable e) {
			System.out.println("could not redirect back to original URL");
			e.printStackTrace();
		}
	}

}