package controllers;

import java.util.List;

import models.Question;
import models.User;
import play.mvc.Controller;

/**
 * A controller for the index.
 * 
 */
public class Application extends Controller {

	/**
	 * Index.
	 */
	public static void index() {
		Question lastQuestion = Question.find("order by timestamp desc")
				.first();
		List<Question> questions = Question.find("order by voting desc")
				.fetch();
		String lastAnswer = "";

		if (lastQuestion != null && lastQuestion.answers.size() != 0) {
			lastAnswer = lastQuestion.answers
					.get(lastQuestion.answers.size() - 1).author.fullname;
		}

		render(lastQuestion, questions, lastAnswer);
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

		if (!Security.isConnected()) {

			render(question, isvalid, abletochoose, abletovote);
		}

		else {
			User user = User.find("byEmail", Security.connected()).first();

			abletochoose = user.isAbleToChoose(id);
			abletovote = user.isAbleToVote(id);
			isvalid = user.hasTimeToChange(id);

			render(question, isvalid, abletochoose, abletovote);
		}
	}

	/**
	 * Creates the user.
	 * 
	 * @param message
	 *            the message
	 */
	public static void createUser(String message) {

		render(message);

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
		createUser(message);
	}
}