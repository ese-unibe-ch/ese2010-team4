package controllers;

import java.util.Date;
import java.util.List;

import models.Question;
import models.User;
import play.mvc.Controller;

/**
 * A controller for the index.
 * 
 * @author dwettstein
 * 
 */
public class Application extends Controller {

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

	public static void show(Long id) {
		Question question = Question.findById(id);
		long validaty = question.validity;
		Date actualdate = new Date();
		long milidate = actualdate.getTime();
		boolean validdate;
		boolean abletochoose = false;
		boolean abletovote = false;

		if (Security.isConnected()
				&& question.author.email.equals(Security.connected())) {
			abletochoose = true;
		}

		User user = User.find("byEmail", Security.connected()).first();

		if (Security.isConnected() && !question.hasVoted(user)
				&& !question.author.fullname.equals(user.fullname)) {
			abletovote = true;
		}

		if (validaty != 0 && milidate > validaty) {
			validdate = false;
			render(question, validdate, abletochoose, abletovote);
		} else {
			validdate = true;
			render(question, validdate, abletochoose, abletovote);

		}

	}

	public static void createUser(String message) {

		render(message);

	}

	public static void addUser(String fullname, String email, String password,
			String password2) {

		String message;
		User user = User.find("byEmail", email).first();

		if (!password.equals(password2)) {
			message = "the password's aren't the same";

		}

		else if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
			message = "you fogot one or more gap's";

		}

		else if (user != null && user.email.equals(email)) {

			message = "user allready exists";
		}

		else {
			new User(fullname, email, password).save();
			message = "Hello, " + fullname + ", please log in";
		}

		createUser(message);
	}

}