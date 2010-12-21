package models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import models.annotations.ForTestingOnly;
import models.helper.DateFormatter;
import models.helper.PostActivityComperator;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * A user with name and first-name. Question
 */
@Entity
public class User extends Model {

	private static final int SPAM_REPORT = 2;
	private static final int POST_DELAY = 30000;
	private static final int SEARCH_DELAY = 10000;

	public long postdate;
	public long searchdate;
	public int counter;
	protected Date birthday;
	public String fullname = "";
	public String website = "";
	public String work = "";
	public String aboutMe = "";
	public String favoriteLanguages;
	public String quotedContent = "";
	public Date timestamp;

	public HashSet<Post> spamreport;
	public boolean isSpam;
	public boolean spamLock;
	public TreeSet<Tag> badgetags;
	public String language = "en";

	@Required
	public String avatarPath = "/public/uploads/standardAvatar.png";

	public Date lastLogOff;

	@OneToOne
	public Reputation rating;

	@Email
	@Required
	public String email;

	@Required
	public String password;

	@Required
	public String username;

	@Required
	public boolean isAdmin;

	@OneToMany
	public List<Question> followQ;

	@OneToMany
	public List<User> followU;

	@OneToMany(mappedBy = "user", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;

	@OneToMany(mappedBy = "author", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Post> posts;

	/**
	 * Instantiates a new user.
	 * 
	 * @param username
	 *            the username
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 */
	public User(String username, String email, String password) {
		this.spamreport = new HashSet<Post>();
		this.isSpam = false;
		this.spamLock = false;
		this.votes = new ArrayList<Vote>();
		this.posts = new ArrayList<Post>();
		this.badgetags = new TreeSet<Tag>();
		this.username = username;
		this.email = email;
		this.password = password;
		this.isAdmin = false;
		lastLogOff = new Date(System.currentTimeMillis());
		this.followQ = new ArrayList<Question>();
		this.followU = new ArrayList<User>();
		this.counter = 0;
		this.timestamp = new Date();
	}

	/**
	 * Creates a new user if all requirements are met.
	 * 
	 * @param username
	 *            the username
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 * @param password2
	 *            the validation password
	 * @return the message
	 */
	public static String createUser(String username, String email,
			String password, String password2) {

		User newUser = new User(username, email, password).save();
		newUser.rating = new Reputation().save();
		newUser.save();
		String message = username + "<br> &{register.subtitle}";
		return message;
	}

	/**
	 * Login to A4Q.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the user
	 */
	public static User login(String username, String password) {
		return find("byUsernameAndPassword", username, password).first();
	}

	public String toString() {
		return email;
	}

	/**
	 * Checks whether the user can choose the best answer for an question.
	 * 
	 * @param id
	 *            the id
	 * @return true if the user is able to choose
	 */

	public boolean isAbleToChoose(Long id) {
		Question question = Question.findById(id);
		if (question.author.email.equals(this.email)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the user can vote.
	 * 
	 * @param id
	 *            the id
	 * @return true if the user is able to vote
	 */
	public boolean isAbleToVote(Long id) {
		Question question = Question.findById(id);
		return (!question.hasVoted(this) && !question.isOwnPost(this));
	}

	/**
	 * Checks whether the user already likes the comment.
	 * 
	 * @param id
	 *            The ID of the comment.
	 * 
	 * @return true if the user is able to like the comment
	 */
	public boolean alreadyLikesComment(Long id) {
		Comment comment = Comment.findById(id);
		return comment.userLikePost(this);
	}

	/**
	 * Check if the validation time form a question is not over.
	 * 
	 * @param id
	 *            the ID of the question to check.
	 * @return true if the user can change the question.
	 */
	public boolean hasTimeToChange(Long id) {
		Question question = Question.findById(id);
		long milidate = new Date().getTime();

		if (question.validity == 0 || milidate < question.validity) {
			return true;
		} else {
			bestAnswer(question);
			return false;
		}
	}

	/**
	 * Helper method for find best answer.
	 * 
	 * @param question
	 *            from the best answer
	 */
	private void bestAnswer(Question question) {
		Answer bestAnswer = null;

		for (Answer answer : question.answers) {
			if (answer.isBestAnswer) {
				bestAnswer = answer;

				if (!question.hasBestAnswer) {
					answer.author.rating.bestAnswer(answer, question.author);
					answer.author.rating.save();
					answer.author.save();
					answer.save();
					save();
				}
			}
		}
		if (bestAnswer != null) {
			question.answers.remove(bestAnswer);
			question.answers.add(0, bestAnswer);
			question.hasBestAnswer = true;
			question.save();
			bestAnswer.save();
			save();
		}
	}

	/**
	 * Gets the birthday from the user.
	 * 
	 * @return the birthday
	 */
	public String getBirthday() {
		return DateFormatter.dateToString(birthday);
	}

	/**
	 * Sets the birthday.
	 * 
	 * @param birthday
	 *            the new birthday
	 */
	public void setBirthday(String birthday) {
		try {
			this.birthday = DateFormatter.stringToDate(birthday);
		} catch (ParseException e) {
			System.out.println("Sorry wrong Date_Format it's "
					+ DateFormatter.DATE_FORMAT_de);
		}
	}

	/**
	 * Calculates the age of the <code>User</code> in years.
	 * 
	 * @return age of the <code>User</code>
	 */
	public int calculateAge() {
		Date now = new Date();
		if (birthday != null) {
			long age = now.getTime() - birthday.getTime();
			return (int) (age / ((long) 1000 * 3600 * 24 * 365));
		} else {
			return 0;
		}
	}

	/**
	 * Removes the null.
	 */
	public void removeNull() {
		int index = 0;
		while (index < followQ.size()) {
			try {
				Long id = followQ.get(index).getId();
				Question question = Question.findById(id);
				question.getTitle();
				index++;
			} catch (Exception e) {
				this.followQ.remove(index);
			}
		}
		this.save();
	}

	/**
	 * Deletes the specified question from the follows.
	 * 
	 * @param question
	 *            the question which has to be deleted
	 */
	public void deleteFollowQ(Question question) {
		this.followQ.remove(question);
		this.save();
	}

	/**
	 * Deletes the specified user from the follows.
	 * 
	 * @param user
	 *            the user which has to be deleted
	 */
	public void deleteFollowU(User user) {
		this.followU.remove(user);
		this.save();
	}

	/**
	 * Checks if the user is following an object (user or question).
	 * 
	 * @param followingObject
	 *            the object to check (user or question)
	 * @return true if the user follows this object
	 */
	public boolean isFollowing(Object followingObject) {
		if (followingObject instanceof User) {
			return followU.contains((User) followingObject);
		} else if (followingObject instanceof Question) {
			return followQ.contains((Question) followingObject);
		} else {
			return false;
		}
	}

	/**
	 * Adds a vote which the user has given.
	 * 
	 * @param vote
	 *            the vote which has to be added.
	 * @return the updated user
	 */
	public User addVote(Vote vote) {
		votes.add(vote);
		save();
		return this;
	}

	/**
	 * Adds a post which the user has written.
	 * 
	 * @param answer
	 *            the answer
	 * @return the user
	 */
	public User addPost(VotablePost post) {
		if (this.canPost()) {
			this.postdate = new Date().getTime() + POST_DELAY;
			posts.add(post);
			save();
		}
		return this;
	}

	/**
	 * Checks if an user is able to post.
	 * 
	 * @return true if the last post + delay is elder then the actual date.
	 */
	public boolean canPost() {
		if (this.postdate < (new Date().getTime()))
			return true;
		else
			return false;
	}

	public boolean canSearch() {
		if (this.searchdate < (new Date().getTime()))
			return true;
		else
			return false;
	}

	/**
	 * Adds the question which the user has posted.
	 * 
	 * @param title
	 *            the title
	 * @param content
	 *            the content
	 * @return the question
	 */
	public Question addQuestion(String title, String content) {
		Question newQuestion = new Question(this, title, content).save();
		this.addPost(newQuestion);
		return newQuestion;
	}

	/**
	 * Searches the activities form this user.
	 * 
	 * @return a list of his activities.
	 */
	public List<VotablePost> activities() {
		return VotablePost.find("author like ? order by timestamp desc", this)
				.fetch();
	}

	/**
	 * Searches the activities form the follows which the user have.
	 * 
	 * @param number
	 *            the max number of matches for user and posts
	 * @return the list
	 */
	public List<Post> followAcitvities(int number) {
		HashSet<Post> activities = new HashSet<Post>();

		for (User user : this.followU) {
			List<VotablePost> postList = VotablePost.find(
					"author like ? order by timestamp desc", user)
					.fetch(number);
			activities.addAll(postList);
		}

		for (Question question : this.followQ) {
			List<Answer> postList = Answer.find(
					"question like ? order by timestamp desc", question).fetch(
					number);
			activities.addAll(postList);
			for (Answer answer : postList) {
				List<Post> commentList = Comment.find(
						"post like ? order by timestamp desc", answer).fetch(
						number);
				activities.addAll(commentList);
			}
			List<Post> comments = Comment.find(
					"post like ? order by timestamp desc", question).fetch(
					number);
			activities.addAll(comments);
		}

		List<Post> postList = new ArrayList<Post>(activities);
		PostActivityComperator comp = new PostActivityComperator();
		Collections.sort((List) postList, comp);

		return postList;
	}

	/**
	 * Gets the reputation points.
	 * 
	 * @return the points for the Reputation graph
	 */
	public List<ReputationPoint> getReputationPoints() {
		return rating.reputationPoints;
	}

	/**
	 * Generates the graph data for the JSON output.
	 * 
	 * @return the json array string
	 */
	public String graphData() {
		StringBuffer strbuffer = new StringBuffer();
		strbuffer.append("[");

		List<ReputationPoint> points = this.getReputationPoints();

		points.add(0, new ReputationPoint(0, this.timestamp.getTime()));
		points.add(new ReputationPoint(points.get(points.size() - 1).repvalue,
				new Date().getTime()));

		for (Iterator<ReputationPoint> itr = points.iterator(); itr.hasNext();) {
			ReputationPoint point = itr.next();
			strbuffer.append("{\"time\": " + point.timestamp + ", \"value\": "
					+ point.repvalue + "}");
			if (itr.hasNext()) {
				strbuffer.append(',');
			}
		}

		strbuffer.append(']');

		return strbuffer.toString();
	}

	/**
	 * Formatted the quoted content.
	 * 
	 * @param content
	 *            the content
	 * @param quoted
	 *            the quoted
	 */
	public void quoteContent(String content, String quoted) {

		quotedContent = "" + "\n\n\n\n\n<br>" + "Quoted: " + quoted
				+ "<blockquote>" + content + "</blockquote>";
	}

	/**
	 * Gets the quoted content.
	 * 
	 * @return the quoted content
	 */
	public String getQuotedContent() {
		String content = quotedContent;
		quotedContent = "";
		save();
		return content;
	}

	/**
	 * Increase the Spamrating of an user. For every post which has been
	 * reported as spam, the user gets a penaltypoint.
	 * 
	 * @param post
	 *            which is reported as spam
	 */
	public void spam(Post post) {
		this.spamreport.add((VotablePost) post);
		this.isSpam();
		this.save();
	}

	public void lockUser() {
		this.spamLock = true;
		this.save();
	}

	public void unlockUser() {
		this.spamLock = false;
		this.save();
	}

	public void unspamUser() {
		this.spamreport.clear();
		this.isSpam();
		this.save();
	}

	/**
	 * Checks if a user has enough penaltypoints for reported as spam.
	 * 
	 * @return true, if is spam
	 */
	public boolean isSpam() {
		if (this.spamreport.size() >= SPAM_REPORT || spamLock) {
			this.isSpam = true;
		} else {
			this.isSpam = false;
		}
		this.save();
		return isSpam;
	}

	/**
	 * Sets the up post time.
	 * 
	 * @param value
	 *            the new up post time
	 */
	@ForTestingOnly
	public void setUpPostTime(long value) {
		this.postdate += value;
		this.save();
	}

	/**
	 * Sets the up search time.
	 */
	public void setUpSearchTime() {
		this.searchdate = new Date().getTime() + this.SEARCH_DELAY;
		this.save();
	}

	/**
	 * Shows how long a user have to wait until he can post a new post.
	 * 
	 * @return time in seconds
	 */
	public int timeToNextSearch() {
		return ((int) ((this.searchdate) - (new Date().getTime())) / 1000);
	}

	/**
	 * Shows how long a user have to wait until he can starts a new search.
	 * 
	 * @return time in seconds
	 */
	public int timeToNextPost() {
		return ((int) ((this.postdate) - (new Date().getTime())) / 1000);
	}

	/**
	 * Deletes the whole reputation of an user
	 */
	public void clearWholeReputation() {
		this.rating.reputation = 0;
		this.rating.save();
		this.save();
	}
}
