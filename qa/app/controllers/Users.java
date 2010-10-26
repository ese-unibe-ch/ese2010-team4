package controllers;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
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

	/**
	 * Index.
	 */
	public static void index() {
		render();
	}

	/**
	 * My questions.
	 */
	public static void myQuestions() {
		User user = User.find("byEmail", Security.connected()).first();
		List<Question> questions = Question.find("byAuthor", user).fetch();
		render(questions);
	}

	/**
	 * Show edit.
	 * 
	 * @param questionId
	 *            the question id
	 * @param editionIndex
	 *            the edition index
	 */
	public static void showEdit(Long questionId, int editionIndex) {

		Boolean sizeIsZero = false;
		Post post = Post.findById(questionId);
		User user = post.author;

		System.out.println("HistorySize: " + post.historys.size());
		if (post.historys.size() == 0) {
			sizeIsZero = true;
		}

		if (post instanceof Question) {
			if (user.hasTimeToChange(questionId)) {
				render(post, editionIndex, sizeIsZero);
			}

			else {

				myQuestions();
			}
		}

		else if (post instanceof Answer) {

			Long id = ((Answer) post).question.id;

			if (user.hasTimeToChange(id)) {
				render(post, editionIndex, sizeIsZero);
			}

			else {
				myAnswers();
			}
		}
	}

	/**
	 * My answers.
	 */
	public static void myAnswers() {
		User user = User.find("byEmail", Security.connected()).first();
		List<Answer> answers = Answer.find("byAuthor", user).fetch();

		render(answers);
	}

	/**
	 * Creates the question.
	 * 
	 * @param author
	 *            the author
	 * @param title
	 *            the title
	 * @param content
	 *            the content
	 */
	public static void createQuestion(@Required String author,
			@Required String title, String content) {

		if (validation.hasErrors()) {
			render("Users/index.html");
		}

		User user = User.find("byFullname", author).first();

		user.addQuestion(title, content).save();
		flash.success("Thanks for ask a new question %s!", author);
		Users.myQuestions();
	}

	/**
	 * Write comment.
	 * 
	 * @param id
	 *            the id
	 * @param questionid
	 *            the questionid
	 */
	public static void writeComment(Long id, Long questionid) {

		Post post = Post.find("byId", id).first();

		render(post, questionid);
	}

	/**
	 * Creates the comment.
	 * 
	 * @param postid
	 *            the postid
	 * @param questionid
	 *            the questionid
	 * @param author
	 *            the author
	 * @param content
	 *            the content
	 */
	public static void createComment(Long postid, Long questionid,
			@Required String author, @Required String content) {

		if (validation.hasErrors()) {
			render("Users/index.html");
		}

		User user = User.find("byEmail", Security.connected()).first();
		Post post = Post.find("byId", postid).first();

		new Comment(user, post, content).save();

		Application.show(questionid);
	}

	/**
	 * Answer question.
	 * 
	 * @param questionId
	 *            the question id
	 * @param author
	 *            the author
	 * @param content
	 *            the content
	 */
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

	/**
	 * Vote for question.
	 * 
	 * @param questionId
	 *            the question id
	 * @param vote
	 *            the vote
	 */
	public static void voteForQuestion(Long questionId, boolean vote) {

		User user = User.find("byEmail", Security.connected()).first();
		Question question = Question.findById(questionId);
		
		question.vote(user, vote);
		question.save();
		flash.success("Thanks for vote %s!", user.fullname);

		
		Application.show(questionId);

	}

	/**
	 * Vote for answer.
	 * 
	 * @param questionId
	 *            the question id
	 * @param answerId
	 *            the answer id
	 * @param vote
	 *            the vote
	 */
	public static void voteForAnswer(Long questionId, Long answerId,
			boolean vote) {

		User user = User.find("byEmail", Security.connected()).first();
		Answer answer = Answer.find("byId", answerId).first();

		answer.vote(user, vote);
		answer.save();

		flash.success("Thanks for vote %s!", user.fullname);
		Application.show(questionId);

	}

	/**
	 * My profile.
	 */
	public static void myProfile() {
		render("Users/profile.html");
	}

	public static void showProfile(Long authorid) {

		User user = User.findById(authorid);

		if (user.email.equals(Security.connected())) {
			myProfile();
		}

		else {
			render(user);
		}

	}

	/**
	 * Edits the post.
	 * 
	 * @param id
	 *            the id
	 * @param content
	 *            the content
	 */
	public static void editPost(Long id, @Required String content) {
		Post post = Post.findById(id);
		
		if(post instanceof Question){
			
			post.addHistory(post, ((Question) post).title, post.content);
			post.save();
			
		}
		
		else{
			post.addHistory(post, "", post.content);
			post.save();
		}
		
		post.content = content;
		post.save();
		
		if (post instanceof Question) {
			Users.myQuestions();
		} 
		
		else{
			Users.myAnswers();
		}
	}

	/**
	 * Delete post.
	 * 
	 * @param id
	 *            the id
	 */
	public static void deletePost(Long id) {
		Post post = Post.findById(id);
		post.delete();
		if (post.getClass().getName().equals("models.Question")) {
			Users.myQuestions();
		} else
			Users.myAnswers();
	}

	/**
	 * Next edition.
	 * 
	 * @param id
	 *            the id
	 * @param index
	 *            the index
	 */
	public static void nextEdition(Long id, int index) {

		if (index > 0) {
			index--;
		}

		Users.showEdit(id, index);
	}

	/**
	 * Previous edition.
	 * 
	 * @param id
	 *            the id
	 * @param index
	 *            the index
	 */
	public static void previousEdition(Long id, int index) {
		Post post = Post.findById(id);
		if (index < post.historys.size() - 1) {
			index++;
		}
		Users.showEdit(id, index);
	}

	/**
	 * Choose best answer.
	 * 
	 * @param answerid
	 *            the answerid
	 */
	public static void chooseBestAnswer(Long answerid) {

		// delay in milisec
		long delay = 10000;
		Answer answer = Answer.findById(answerid);
		Question question = answer.question;

		// necessary if user changed his mind
		question.setAllAnswersFalse();
		question.save();
		answer.best = true;

		question.setValidity(delay);
		question.save();
		answer.save();

		Application.show(answer.question.id);
	}

	// DR
	/**
	 * Change profile.
	 * 
	 * @param website
	 *            the website
	 * @param work
	 *            the work
	 * @param languages
	 *            the languages
	 * @param aboutMe
	 *            the about me
	 * @param avatarURL
	 *            the avatar url
	 * @throws ParseException
	 */
	public static void changeProfile(String birthday, String website,
			String work, String languages, String aboutMe, String avatarURL)
			throws ParseException {
		User user = User.find("byEmail", Security.connected()).first();
		user.setBirthday(birthday);
		user.website = website;
		user.work = work;
		user.favoriteLanguages = languages;
		user.aboutMe = aboutMe;
		user.avatarURL = avatarURL;
		user.save();
		Users.myProfile();
	}

	/**
	 * Recent posts.
	 * 
	 * @return the list
	 */
	public static void recentPosts() {
		User user = User.find("byEmail", Security.connected()).first();
		Post post = Post.find("byAutor", user).first();
		render(post);
	}

	// JW: trivial user search
	public static void searchResults(String toSearch) {

		boolean found = false;
		User user = User.find("byFullname", toSearch).first();

		if (user == null) {
			String message = "no user found";
			render(user, message, found);
		}

		else {
			found = true;
			render(user, found);
		}
	}

	public static void myFollows() {
		
		//JW changes
		User user = User.find("byEmail", Security.connected()).first();
		user.followQ = (ArrayList<Question>) user.removeNull();
		user.save();
		List<Question> followQ = user.followQ;
		Long userId = user.id;

		List<User> followU = user.followU;

		render(followQ, followU, userId);
	}

	public static void followQuestion(Long id) {
		User user = User.find("byEmail", Security.connected()).first();
		Question question = Post.findById(id);
		if (!user.followQ.contains(question)) {
			user.followQ.add(question);
			user.save();
		}
		Users.myFollows();
	}

	public static void followUser(Long id) {
		User userClient = User.find("byEmail", Security.connected()).first();
		User userServer = User.findById(id);
		if (!userClient.followU.contains(userServer)) {
			userClient.followU.add(userServer);
			userClient.save();
		}
		Users.myFollows();
	}

	public static void deleteFollowQuestion(Long id) {
		Question question = Question.findById(id);
		User user = User.find("byEmail", Security.connected()).first();
		user.deleteFollowQ(question);
		Users.myFollows();
	}

	public static void deleteFollowUser(Long id) {
		User userMaster = User.find("byEmail", Security.connected()).first();
		User userSlave = User.findById(id);
		userMaster.deleteFollowU(userSlave);
		Users.myFollows();
	}

	public static void uploadAvatar(String title, File avatar) {
		User user = User.find("byEmail", Security.connected()).first();
		user.avatar = avatar;
		user.avatarTitel = title;
	}

}
