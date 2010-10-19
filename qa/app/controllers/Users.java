package controllers;

import java.util.Date;
import java.util.List;

import models.Answer;
import models.Comment;
import models.Post;
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
			@Required String title, String content) {

		if (validation.hasErrors()) {
			render("Users/index.html");
		}

		User user = User.find("byFullname", author).first();

		new Question(user, title, content).save();
		flash.success("Thanks for ask a new question %s!", author);
		Users.myQuestions();
	}

	public static void writeComment(Long id, Long questionid) {

		Post post = Post.find("byId", id).first();

		render(post, questionid);
	}

	public static void createComment(Long postid, Long questionid,
			@Required String author, @Required String content) {
		// JW: create comments

		if (validation.hasErrors()) {
			render("Users/index.html");
		}

		User user = User.find("byEmail", Security.connected()).first();
		Post post = Post.find("byId", postid).first();

		new Comment(user, post, content).save();

		Application.show(questionid);
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

		if (!question.hasVoted(user)
				&& !question.author.email.equals(user.email)) {

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
		Question question = Question.find("byId", questionId).first();

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
		render("Users/profile.html");
	}

	public static void showEdit(Long questionId, int editionIndex) {
		Post post = Post.findById(questionId);
		render(post, editionIndex);
	}

	public static void editPost(Long id, @Required String content) {
		Post post = Post.findById(id);
		post.content = content;
		post.history.addFirst(content);
		post.save();
		if (post.getClass().getName().equals("models.Question")) {
			Users.myQuestions();
		} else
			Users.myAnswers();
	}

	public static void deletePost(Long id) {
		Post post = Post.findById(id);
		post.delete();
		if (post.getClass().getName().equals("models.Question")) {
			Users.myQuestions();
		} else
			Users.myAnswers();
	}

	public static void nextEdition(Long id, int index) {
		if (index > 0) {
			index--;
		}
		Users.showEdit(id, index);
	}

	public static void previousEdition(Long id, int index) {
		Post post = Post.findById(id);
		if (index < post.history.size() - 1) {
			index++;
		}
		Users.showEdit(id, index);
	}

	public static void chooseBestAnswer(Long answerid) {
		// JW

		Answer answer = Answer.find("byId", answerid).first();
		answer.question.setAllAnswersFalse();
		answer.question.save();
		answer.best = true;
		Date date = new Date();
		answer.question.validity = date.getTime() + 10000;
		answer.question.save();
		answer.save();
		Application.show(answer.question.id);
	}

	public static void changeProfile(String website, String work,
			String languages, String aboutMe, String avatarURL) {
		User user = User.find("byEmail", Security.connected()).first();
		user.website = website;
		user.work = work;
		user.favoriteLanguages = languages;
		user.aboutMe = aboutMe;
		user.avatarURL = avatarURL;
		user.save();
		Users.myProfile();
	}
}
