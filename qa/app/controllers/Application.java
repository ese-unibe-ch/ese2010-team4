package controllers;

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
		 render(lastQuestion, questions);
		 }

		 public static void show(Long id) {
		 Question question = Question.findById(id);
		 render(question);
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