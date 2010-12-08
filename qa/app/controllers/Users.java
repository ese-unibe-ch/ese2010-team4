package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import models.Answer;
import models.Badge;
import models.Comment;
import models.VotablePost;
import models.Question;
import models.Tag;
import models.User;
import models.Post;
import play.data.validation.Required;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import controllers.Secure.Security;

/**
 * A controller for the administration.
 * 
 */
@With(Secure.class)
public class Users extends CRUD {

	private static Uploader uploader = new Uploader("qa/public/uploads/");
	private final static int MINIMUM_TAGS = 1;

	@Before
	static void setConnectedUser() {
		if (Secure.Security.isConnected()) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			renderArgs.put("user", user);
		}
	}

	@Before
	static void getSameQuestions() {
		Application.getSameQuestions();
	}

	@Before
	static void canPost() {

		if (Secure.Security.isConnected()) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			renderArgs.put("canPost", user.canPost());
		}

	}
	
	@Before
	static void spam(){
		Application.spam();
	}

	/**
	 * Index.
	 */
	public static void index() {
		Post lastActivity = VotablePost.find("order by timestamp desc").first();
		render(lastActivity);
	}

	/**
	 * My questions.
	 */
	public static void myQuestions() {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
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

		VotablePost post = VotablePost.findById(questionId);
		User user = post.author;

		if (editionIndex < 0) {
			editionIndex = 0;
		}

		if (post.historys.isEmpty()) {
			post.addHistory(post, post.fullname, post.content);
			editionIndex = 0;
			post.save();
		}

		if (post instanceof Question) {
			if (user.hasTimeToChange(questionId)) {
				render(post, editionIndex);
			}

			else {
				myQuestions();
			}
		}

		else if (post instanceof Answer) {

			Long id = ((Answer) post).question.id;

			if (user.hasTimeToChange(id)) {
				render(post, editionIndex);
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
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
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
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void createQuestion(@Required String author,
			@Required String title, String content, String tags, File attachment) {
		// check if the content only contains of control characters
		String test = content.replaceAll("[^a-zA-Z]", "");
		if (test.isEmpty()) {
			validation.isTrue(false);// create an error, to show on
			// Users.index()
			validation.keep();
			Users.index();
			return;
		}

		if (validation.hasErrors()) {
			render("Users/index.html");
		}

		User user = User.find("byUsername", author).first();
		Question question = user.addQuestion(title, content).save();

		if (!(tags.equals("") || tags.isEmpty() || tags.equals(null))) {
			String[] separetedTags = tags.split(",");
			for (String tag : separetedTags) {
				question.tagItWith(tag);
			}
			question.save();
		}

		if (attachment != null) {
			question.attachmentPath = uploader.upload(attachment,
					"question" + question.id).substring(2);
			question.save();
		}
		flash.success(Messages.get("newQuestionPosted", author));
		Application.show(question.getId());
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

		Post post = VotablePost.find("byId", id).first();
		Post lastActivity = VotablePost.find("order by timestamp desc").first();

		render(post, questionid, lastActivity);
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
			validation.isTrue(false);// create an error, to show on
			// Users.index()
			validation.keep();
			writeComment(postid, questionid);
		}

		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		VotablePost post = VotablePost.find("byId", postid).first();

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
			@Required String content, File attachment) {

		Question question = Question.findById(questionId);

		String test = content.replaceAll("[^a-zA-Z]", "");
		if (test.isEmpty()) {
			validation.keep();
			Application.show(questionId);
			return;
		}

		if (validation.hasErrors()) {
			render("Application/show.html", question);
		}

		Post lastActivity = VotablePost.find("order by timestamp desc").first();
		User user = User.find("byUsername", author).first();
		Answer answer = new Answer(question, user, content).save();
		ArrayList<VotablePost> sameAnswerQuestions = question
				.getNotAnsweredSimilarPosts(MINIMUM_TAGS, user.badgetags, user);

		boolean abletovote = user.isAbleToVote(questionId);
		boolean hasTimeToChange = user.hasTimeToChange(questionId);
		boolean isfollowing = user.isFollowing(question);

		if (attachment != null) {
			answer.attachmentPath = uploader.upload(attachment,
					"answer" + answer.id).substring(2);
			answer.save();
		}
		question.addNewAnswer(answer).save();
		flash.success(Messages.get("newAnswerPosted", author));
		render("Application/show.html", question, sameAnswerQuestions,
				lastActivity, abletovote, hasTimeToChange, isfollowing);
	}

	/**
	 * quotes a post.
	 * 
	 * @param postId
	 *            the post id
	 */
	public static void quote(Long postId) {
		VotablePost post = VotablePost.findById(postId);
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		user.quoteContent(post.content, post.author.email);
		user.save();
		if (post instanceof Answer)
			Application.show(((Answer) post).question.id);
		else
			Application.show(postId);
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

		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		Question question = Question.findById(questionId);

		question.vote(user, vote);
		flash.success(Messages.get("voted", user.username));

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

		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		Answer answer = Answer.find("byId", answerId).first();

		answer.vote(user, vote);

		flash.success(Messages.get("voted", user.username));
		Application.show(questionId);

	}
	
	public static void vote(long id, boolean vote){
		Post post = VotablePost.findById(id);
		User user = User.find("byUsername", Secure.Security.connected())
		.first();
		
		if(post instanceof Answer){
			
		}
		
	}

	/**
	 * Likes/Unlikes a comment.
	 * 
	 * @param commentId
	 * @param like
	 *            boolean whether the user likes the comment or not
	 */
	public static void likeComment(Long commentId, boolean like) {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		Comment comment = Comment.findById(commentId);
		Question question = comment.findQuestion();
		if (like) {
			comment.addLiker(user);
		} else {
			comment.removeLiker(user);
		}
		Application.show(question.id);
	}

	/**
	 * My profile.
	 * 
	 * @throws IOException
	 */
	public static void myProfile(Long userid) throws IOException {
		User user = User.findById(userid);
		List<Badge> badges = Badge.find("byReputation", user.rating).fetch();
		System.out.println(badges.size());
		Post lastActivity = VotablePost.find("order by timestamp desc").first();
		List<VotablePost> activities = user.activities();
		int size = user.rating.totalRepPoint.size();
		render("Users/myProfile.html", activities, user, size, lastActivity,
				badges);
	}

	public static void showProfile(Long authorid) throws IOException {
		User userToShow = User.findById(authorid);
		if (userToShow.equals(User.find("byUsername",
				Secure.Security.connected()).first())) {
			myProfile(authorid);
		} else {
			List<VotablePost> activities = userToShow.activities();
			Post lastActivity = VotablePost.find("order by timestamp desc").first();
			List<Badge> badges = Badge.find("byReputation", userToShow.rating)
					.fetch();
			render(userToShow, activities, lastActivity, badges);
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
		VotablePost post = VotablePost.findById(id);

		if (post instanceof Question) {
			post.addHistory(post, ((Question) post).title, content);
			post.save();
		}

		else {
			post.addHistory(post, "", content);
			post.save();
		}

		post.content = content;
		post.save();

		if (post instanceof Question) {
			Users.myQuestions();
		}

		else {
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
		Post post = VotablePost.findById(id);
		post.delete();
		if (post instanceof models.Question) {
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
	public static void previousEdition(Long id, int index) {

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
	public static void nextEdition(Long id, int index) {
		VotablePost post = VotablePost.findById(id);
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

		Answer answer = Answer.findById(answerid);
		Question question = answer.question;
		question.bestAnswer(answer);
		question.save();
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
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void changeProfile(String fullname, String birthday,
			String website, String work, String languages, String aboutMe)
			throws IOException {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		user.fullname = fullname;
		user.setBirthday(birthday);
		user.website = website;
		user.work = work;
		user.favoriteLanguages = languages;
		user.aboutMe = aboutMe;
		user.save();
		Users.myProfile(user.id);
	}

	/**
	 * Recent posts.
	 * 
	 * @return the list
	 */
	public static void recentPosts() {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		Post post = VotablePost.find("byAutor", user).first();
		render(post);
	}

	// JW: refactor
	public static void searchResults(String toSearch) {
		String searched = toSearch;
		toSearch = toSearch.toLowerCase();
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		boolean canSearch = user.canSearch();

		if (user.canSearch()) {
			user.setUpSearchTime();
			boolean found = false;
			Post lastActivity = VotablePost.find("order by timestamp desc").first();
			List<User> users = User
					.find("byUsernameLike", "%" + toSearch + "%").fetch();
			List<VotablePost> postscont = VotablePost.find("byContentLike",
					"%" + toSearch + "%").fetch();
			List<VotablePost> poststitl = VotablePost.find("byTitleLike",
					"%" + toSearch + "%").fetch();
			List<Tag> tags = Tag.find("byNameLike", "%" + toSearch + "%")
					.fetch();

			toSearch = searched;
			if (users.size() == 0 && postscont.size() == 0
					&& poststitl.size() == 0 && tags.isEmpty()) {
				String message = "no user found";
				render(users, message, found, lastActivity, toSearch, canSearch);
			}

			else {
				found = true;
				render(users, postscont, poststitl, tags, found, lastActivity,
						toSearch, canSearch);
			}
		}
		int timeToNextSearch = user.timeToNextSearch();
		render(canSearch, timeToNextSearch);

	}

	public static void myFollows() {

		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		Post lastActivity = VotablePost.find("order by timestamp desc").first();
		user.removeNull();
		user.save();

		List<Question> followQ = user.followQ;
		List<VotablePost> activities = user.followAcitvities(10);
		Long userId = user.id;
		List<User> followU = user.followU;

		render(followQ, followU, userId, activities, lastActivity);
	}

	public static void followQuestion(Long id) {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		Question question = VotablePost.findById(id);
		if (!user.followQ.contains(question)) {
			user.followQ.add(question);
			user.save();
		}
		Users.myFollows();
	}

	public static void followUser(Long id) {
		User userClient = User.find("byUsername", Secure.Security.connected())
				.first();
		User userServer = User.findById(id);
		if (!userClient.followU.contains(userServer)) {
			userClient.followU.add(userServer);
			userClient.save();
		}
		Users.myFollows();
	}

	public static void unfollowQuestion(Long id) {
		Question question = Question.findById(id);
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		user.deleteFollowQ(question);
		Users.myFollows();
	}

	public static void unfollowUser(Long id) {
		User userMaster = User.find("byUsername", Secure.Security.connected())
				.first();
		User userSlave = User.findById(id);
		userMaster.deleteFollowU(userSlave);
		Users.myFollows();
	}

	public static void uploadAvatar(File avatar) throws FileNotFoundException,
			IOException {
		// File should not be null and not bigger than 10KB
		assert avatar != null && avatar.length() < 10000;
		if (avatar != null && avatar.length() < 50000) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			user.avatarPath = uploader.upload(avatar, "avatar" + user.id)
					.substring(2);
			user.save();
			Users.myProfile(user.id);
		} else
			renderText("File to big");

	}

	public static void updateAvatarPath(String URL) throws IOException {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		user.avatarPath = URL;
		user.save();
		Users.myProfile(user.id);
	}

	public static void tagQuestion(Long id, String name) {
		Question question = Question.findById(id);
		String[] tags = name.split(",");
		for (String tag : tags) {
			tag = tag.trim();
			if (!tag.isEmpty()) {
				question.tagItWith(tag);
			}
		}
		question.save();
		Users.showEdit(id, 0);
	}

	public static void proposeTag() {
		List<Tag> taglist = Tag.findAll();
		List<String> tags = new ArrayList<String>();
		for (Tag tag : taglist) {
			tags.add(tag.name);
		}
		renderJSON(tags);
	}

	public static void getGraphData(long uid) {

		User user = User.findById(uid);

		renderJSON(user.graphData());

	}

	public static void selectLanguage(@Required String langId) {
		if (langId != null) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			Lang.change(langId);
			user.language = langId;
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

	public static void isSpam(long id) {
		VotablePost post = VotablePost.findById(id);
		User user = User.find("byUsername", Security.connected()).first();
		post.spam(user);
		post.author.save();
		if (post instanceof Question) {
			Application.show(id);
		} else {
			Application.show(((Answer) post).question.id);
		}
	}
	/**
	 * Change the answer to not best answer, if it was an best answer.
	 * @param id of the answer
	 */
	public static void notBestAnswer(long id){
		Question question = Question.findById(id);
		question.setAllAnswersFalse();
		question.hasNotBestAnswer();
		question.save();
		System.out.println("validität: "+question.getValidity());
		Application.show(id);
		
	}
}
