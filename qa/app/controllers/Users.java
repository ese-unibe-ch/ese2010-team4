package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import models.Answer;
import models.Badge;
import models.Comment;
import models.Post;
import models.Question;
import models.Tag;
import models.User;
import models.VotablePost;
import play.Play;
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

	private static Uploader uploader = new Uploader(Play.applicationPath
			+ "/public/uploads/");
	private final static int MINIMUM_TAGS = 1;

	@Before
	/**
	 * The data which are used in the whole A4Q.
	 */
	static void setConnectedUser() {
		Application.setup();

	}

	/**
	 * Shows the indexpage
	 */
	public static void index() {
		Post lastActivity = VotablePost.find("order by timestamp desc").first();
		render(lastActivity);
	}

	/**
	 * Shows all question from a specific user.
	 */
	public static void myQuestions() {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		List<Question> questions = Question.find("byAuthor", user).fetch();
		render(questions);
	}

	/**
	 * Edits a specific post from a user.
	 * 
	 * @param questionId
	 *            the id form the editing question
	 * @param editionIndex
	 *            the edition index of the history
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
	 * Shows all answers of a specific user.
	 */
	public static void myAnswers() {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		List<Answer> answers = Answer.find("byAuthor", user).fetch();

		render(answers);
	}

	/**
	 * Creates a new question
	 * 
	 * @param author
	 *            the author form the question
	 * @param title
	 *            the title
	 * @param content
	 *            the content
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
			String path = uploader.upload(attachment, "question" + question.id);
			path = path.substring(path.indexOf("/public"));
			question.attachmentPath = path;
			question.save();
		}
		flash.success(Messages.get("newQuestionPosted", author));
		Application.show(question.getId());
	}

	/**
	 * Loads page for adding a comment to a VotablePost.
	 * 
	 * @param id
	 *            the id form the post
	 * @param questionid
	 *            the questionid
	 */
	public static void writeComment(Long id, Long questionid) {

		Post post = VotablePost.find("byId", id).first();
		Post lastActivity = VotablePost.find("order by timestamp desc").first();

		render(post, questionid, lastActivity);
	}

	/**
	 * Creates a new comment to a specific VotablePost.
	 * 
	 * @param postid
	 *            from the post which receive an comment.
	 * @param questionid
	 *            the questionid
	 * @param author
	 *            the author of the comment
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
	 * Answers question.
	 * 
	 * @param questionId
	 *            the question id
	 * @param author
	 *            the author of the answer
	 * @param content
	 *            the content of the anser
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
			String path = uploader.upload(attachment, "answer" + answer.id);
			path = path.substring(path.indexOf("/public"));
			answer.attachmentPath = path;
			answer.save();
		}
		question.addNewAnswer(answer).save();
		flash.success(Messages.get("newAnswerPosted", author));
		Application.show(questionId);
	}

	/**
	 * Quotes a post.
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
	 * Votes a post up or down
	 * 
	 * @param id
	 *            of the voted post
	 * @param vote
	 *            true if vote up.
	 */
	public static void vote(long id, boolean vote) {
		VotablePost post = VotablePost.findById(id);
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		post.vote(user, vote);
		post.save();
		if (post instanceof Answer)
			Application.show(((Answer) post).question.id);
		if (post instanceof Question)
			Application.show(id);

	}

	/**
	 * A vote from the index.html page.
	 * 
	 * @param id
	 *            of the voted question
	 * @param vote
	 *            true for vote up.
	 */
	public static void voteFromIndex(long id, boolean vote) {
		VotablePost post = VotablePost.findById(id);
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		post.vote(user, vote);
		post.save();
		Application.index();
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
		Post post = comment.post;
		Question question;
		if (post instanceof Question) {
			question = (Question) comment.post;
		} else {
			question = ((Answer) (comment.post)).question;
		}

		if (like) {
			comment.addLiker(user);
		} else {
			comment.removeLiker(user);
		}
		Application.show(question.id);
	}

	/**
	 * Shows the profile of the logged in user.
	 * 
	 * @throws IOException
	 */
	public static void myProfile(Long userid) throws IOException {
		User user = User.findById(userid);
		List<Badge> badges = Badge.find("byReputation", user.rating).fetch();
		Post lastActivity = VotablePost.find("order by timestamp desc").first();
		List<VotablePost> activities = user.activities();
		int size = user.rating.reputationPoints.size();
		render("Users/myProfile.html", activities, user, size, lastActivity,
				badges);
	}

	/**
	 * Shows the profile form a random user.
	 * 
	 * @param authorid
	 *            id from the user
	 * @throws IOException
	 */
	public static void showProfile(Long authorid) throws IOException {
		User userToShow = User.findById(authorid);
		if (userToShow.equals(User.find("byUsername",
				Secure.Security.connected()).first())) {
			myProfile(authorid);
		} else {
			List<VotablePost> activities = userToShow.activities();
			Post lastActivity = VotablePost.find("order by timestamp desc")
					.first();
			List<Badge> badges = Badge.find("byReputation", userToShow.rating)
					.fetch();
			render(userToShow, activities, lastActivity, badges);
		}
	}

	/**
	 * Edits an specific post.
	 * 
	 * @param id
	 *            the id of the post
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
	 * Deletes a specific post.
	 * 
	 * @param id
	 *            the id of the post
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
	 * Changes to next history edition of a post.
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
	 * Changes to the previous history edition of a post.
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
	 * Chooses the best answer of a post.
	 * 
	 * @param answerid
	 *            the answerid of the best answer
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
	 * Change profile
	 * 
	 * @param website
	 *            the website form the user
	 * @param work
	 *            the work form the user
	 * @param languages
	 *            the languages which a user speaks
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

	/**
	 * Renders the result for a given search string.
	 * 
	 * @param toSearch
	 */
	public static void searchResults(String toSearch) {
		String searched = toSearch;
		toSearch = toSearch.toLowerCase();
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		boolean canSearch = user.canSearch();

		if (user.canSearch()) {
			user.setUpSearchTime();

			boolean found = false;
			Post lastActivity = VotablePost.find("order by timestamp desc")
					.first();
			List<User> users = User
					.find("byUsernameLike", "%" + toSearch + "%").fetch();

			HashSet<Post> foundposts = new HashSet<Post>();
			foundposts.addAll((ArrayList) Post.find("byContentLike",
					"%" + toSearch + "%").fetch());
			foundposts.addAll((ArrayList) Post.find("byTitleLike",
					"%" + toSearch + "%").fetch());
			List<Tag> tags = Tag.find("byNameLike", "%" + toSearch + "%")
					.fetch();

			ArrayList foundedposts = new ArrayList<Post>(foundposts);

			toSearch = searched;
			if (users.isEmpty() && foundedposts.isEmpty() && tags.isEmpty()) {
				String message = "nothing found";
				render(users, message, found, lastActivity, toSearch, canSearch);
			} else {
				found = true;
				render(users, foundedposts, tags, found, lastActivity,
						toSearch, canSearch);
			}
		}
		render(canSearch, user.timeToNextSearch());

	}

	/**
	 * Shows all follows of a specific question.
	 */
	public static void myFollows() {

		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		Post lastActivity = VotablePost.find("order by timestamp desc").first();
		user.removeNull();
		user.save();

		List<Question> followQ = user.followQ;
		List<Post> activities = user.followAcitvities(10);
		Long userId = user.id;
		List<User> followU = user.followU;

		render(followQ, followU, userId, activities, lastActivity);
	}

	/**
	 * Adds a question to the followed question of a user.
	 * 
	 * @param id
	 *            of the question
	 */
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

	/**
	 * Adds a user to the follows of a user
	 * 
	 * @param id
	 *            of the user
	 */
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

	/**
	 * Deletes a question from the followlist.
	 * 
	 * @param id
	 *            of the question
	 */
	public static void unfollowQuestion(Long id) {
		Question question = Question.findById(id);
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		user.deleteFollowQ(question);
		Users.myFollows();
	}

	/**
	 * Deletes a user from the followlist.
	 * 
	 * @param id
	 *            of the user
	 */
	public static void unfollowUser(Long id) {
		User userMaster = User.find("byUsername", Secure.Security.connected())
				.first();
		User userSlave = User.findById(id);
		userMaster.deleteFollowU(userSlave);
		Users.myFollows();
	}

	/**
	 * Uploads an avatar picture.
	 * 
	 * @param avatar
	 *            picture from the user
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void uploadAvatar(File avatar) throws FileNotFoundException,
			IOException {
		// File should not be null and not bigger than 10KB
		assert avatar != null && avatar.length() < 10000;
		if (avatar != null && avatar.length() < 50000) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			String path = uploader.upload(avatar, "avatar" + user.id);
			path = path.substring(path.indexOf("/public"));
			user.avatarPath = path;
			user.save();
			Users.myProfile(user.id);
		} else
			renderText("File to big");

	}

	/**
	 * Changes the avatarpicture of a user.
	 * 
	 * @param URL
	 * @throws IOException
	 */
	public static void updateAvatarPath(String URL) throws IOException {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		user.avatarPath = URL;
		user.save();
		Users.myProfile(user.id);
	}

	/**
	 * Adds tags to a specific Question.
	 * 
	 * @param id
	 *            of the question
	 * @param name
	 *            of the tag
	 */
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

	/**
	 * Graph data from the reputationgraph of a user.
	 * 
	 * @param uid
	 *            the userid
	 */
	public static void getGraphData(long uid) {

		User user = User.findById(uid);

		renderJSON(user.graphData());

	}

	/**
	 * Selects the specific language.
	 * 
	 * @param langId
	 *            of the language
	 */
	public static void selectLanguage(@Required String langId) {
		if (langId != null) {
			User user = User.find("byUsername", Secure.Security.connected())
					.first();
			Lang.change(langId);
			user.language = langId;
			user.save();
			if (!Lang.get().equals(langId)) {
				flash.error("Unknown language %s!", langId);
			}
			try {
				myProfile(user.id);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			flash.error("No language choosen");
		}
	}

	/**
	 * Checks if somebody is spam.
	 * 
	 * @param id
	 */
	public static void isSpam(long id) {
		VotablePost post = VotablePost.findById(id);
		User user = User.find("byUsername", Security.connected()).first();
		post.spam(user);
		post.author.save();
		if (post instanceof Question) {
			Application.show(id);
		} else if (post instanceof Answer) {
			Application.show(((Answer) post).question.id);
		} else if (post instanceof Comment
				&& ((Comment) post).post instanceof Question) {
			Application.show(((Comment) post).post.id);
		} else if (post instanceof Comment
				&& ((Comment) post).post instanceof Answer) {
			Application.show(((Answer) ((Comment) post).post).question.id);
		}
	}

	/**
	 * Change the answer to not best answer, if it was an best answer.
	 * 
	 * @param id
	 *            of the answer
	 */
	public static void notBestAnswer(long id) {
		Question question = Question.findById(id);
		question.setAllAnswersFalse();
		question.hasNotBestAnswer();
		question.save();
		Application.show(id);

	}

	/**
	 * Declares a user as spam.
	 * 
	 * @param id
	 *            of the user
	 * @throws IOException
	 */
	public static void lockUser(long id) throws IOException {
		User user = User.findById(id);
		user.lockUser();
		Users.showProfile(id);
	}

	/**
	 * Changes a user from spamstatus to the normal status.
	 * 
	 * @param id
	 *            of the user
	 * @throws IOException
	 */
	public static void unlockUser(long id) throws IOException {
		User user = User.findById(id);
		user.unlockUser();
		Users.showProfile(id);
	}
}
