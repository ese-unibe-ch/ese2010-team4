package controllers;

import java.util.List;

import models.Post;
import models.Question;
import models.Tag;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * A controller for the index.
 * 
 */
public class Application extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
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
		User user;
		boolean isconnected = Security.isConnected();

		if (isconnected) {
			user = User.find("byEmail", Security.connected()).first();
			render(lastActivity, questions, lastAnswer, isconnected, user);
		}

		else
			render(lastActivity, questions, lastAnswer, isconnected);
	}

	/**
	 * Show.
	 * 
	 * @param id
	 *            the id
	 */
	public static void show(Long id) {

		Question question = Question.find("byId", id).first();

		boolean abletochoose = false;
		boolean abletovote = false;
		boolean isvalid = false;
		boolean isfollowing = false;

		if (!Security.isConnected()) {

			render(question, isvalid, abletochoose, abletovote, isfollowing);
		}

		else {
			User user = User.find("byEmail", Security.connected()).first();

			abletochoose = user.isAbleToChoose(id);
			abletovote = user.isAbleToVote(id);
			isvalid = user.hasTimeToChange(id);
			isfollowing = user.isFollowing(question);

			render(question, isvalid, abletochoose, abletovote, isfollowing);
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
	public static void addUser(String fullname, String email, String password,
			String password2) {

		String message = User.createUser(fullname, email, password, password2);
		try {
			Secure.login(message);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void tagged(Tag tag) {
		List<Post> taggedPosts = Post.findTaggedWith(tag.name);
		render(taggedPosts);
	}

}