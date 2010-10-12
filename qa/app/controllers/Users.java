package controllers;

import java.util.List;

import models.Answer;
import models.Question;
import models.User;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * A controller for the administration.
 * 
 * @author dwettstein
 * 
 */
@With(Secure.class)
public class Users extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user.fullname);
		}
	}

	public static void index() {
		render();
	}

	public static void myQuestions() {
		User user = User.find("byEmail", Security.connected()).first();
		List<Question> questions = Question.find("byAuthor", user).fetch();
		render(questions);
	}

	public static void myAnswers() {
		User user = User.find("byEmail", Security.connected()).first();
		List<Answer> answers = Answer.find("byAuthor", user).fetch();
		render(answers);
	}

	public static void createQuestion(@Required String author,
			@Required String content) {
		
		if (validation.hasErrors()) {
			render("Users/index.html");
		}

		User user = User.find("byFullname", author).first();
		
		Question question = new Question(user, content).save();
		flash.success("Thanks for ask a new question %s!", author);
		Users.myQuestions();
	}

	public static void answerQuestion(Long questionId, @Required String author,
			@Required String content) {
		Question question = Question.findById(questionId);
		
		if (validation.hasErrors()) {
			render("Application/show.html", question);
		}
		
		User user = User.find("byFullname", author).first();
		question.addAnswer(user, content).save();
		flash.success("Thanks for write the answer %s!", author);
		Application.show(questionId);
	}

	public static void voteForQuestion(Long questionId, @Required User user,
			String vote) {

		Question question = Question.findById(questionId);

		if (!question.hasVoted(user) && !question.author.email.equals(user.email)) {

			if (vote.equals("Vote up")) {
				question.voteUp(user);
				question.save();
			}

			else {
				question.voteDown(user);
				question.save();
			}

			flash.success("Thanks for vote %s!", user.fullname);
		}

		Application.show(questionId);

	}

	public static void voteForAnswer(Long questionId, Long answerId,
			@Required User user, String vote) {
		
		Answer answer = Answer.find("byId", answerId).first();
		
		System.out.println(user.fullname);
		System.out.println(answer.author.fullname);
		System.out.println(answer.voting);

		if (!answer.hasVoted(user) && !answer.author.email.equals(user.email)) {
			System.out.println("geht durch");
			if (vote.equals("Vote up")) {
				answer.voteUp(user);
				answer.save();
			}

			else {
				answer.voteDown(user);
				answer.save();
			}
		}

		flash.success("Thanks for vote %s!", user.fullname);
		Application.show(questionId);

	}

	public static void myProfile() {
		// TODO
		render("User/profile.html");
	}
	
	public static void showEdit(Long questionId) {
		Question question = Question.findById(questionId);
		render(question);
	}
		
	public static void editPost(Long id, @Required String content) {
		Question question = Question.findById(id);
		question.content = content;
		question.save();
		Users.myQuestions();
	}
}
