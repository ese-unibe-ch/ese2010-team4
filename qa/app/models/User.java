package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import controllers.Secure.Security;

import models.annotations.ForTestingOnly;
import models.helper.DateFormatter;
import models.helper.PostActivityComperator;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

// TODO: Auto-generated Javadoc
/**
 * A user with name and first-name. Question
 */
@Entity
public class User extends Model {

	/** The Constant SPAM_REPORT. */
	private static final int SPAM_REPORT = 2;

	/** The Constant POST_DELAY. */
	private static final int POST_DELAY = 30000;

	private static final int SEARCH_DELAY = 10000;

	/** The postdate. */
	public long postdate;

	public long searchdate;

	/** The counter. */
	public int counter;

	/** The birthday. */
	protected Date birthday;

	/** The fullname. */
	public String fullname = "";

	/** The website. */
	public String website = "";

	/** The work. */
	public String work = "";

	/** The about me. */
	public String aboutMe = "";

	/** The favorite languages. */
	public String favoriteLanguages;

	/** The quoted content. */
	public String quotedContent = "";

	/** The timestamp. */
	public Date timestamp;

	/** The avatar path. */
	@Required
	public String avatarPath = "/public/uploads/standardAvatar.png";

	/** The last log off. */
	public Date lastLogOff;

	/** The rating. */
	@OneToOne
	public Reputation rating;

	/** The email. */
	@Email
	@Required
	public String email;

	/** The password. */
	@Required
	public String password;

	/** The username. */
	@Required
	public String username;

	/** The is admin. */
	@Required
	public boolean isAdmin;

	/** The follow q. */
	@OneToMany
	public List<Question> followQ;

	/** The follow u. */
	@OneToMany
	public List<User> followU;

	/** The votes. */
	@OneToMany(mappedBy = "user", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;

	/** The posts. */
	@OneToMany(mappedBy = "author", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Post> posts;

	/** The spamreport. */

	public HashSet<Post> spamreport;

	/** The badgetags. */
	public TreeSet<Tag> badgetags;

	/** The language. */
	public String language = "en";

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
	 * Login.
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

	/**
	 * (non-Javadoc).
	 * 
	 * @return the string
	 * @see play.db.jpa.JPASupport#toString()
	 */
	public String toString() {
		return email;
	}

	/**
	 * checks whether the user can choose the best answer for an question.
	 * 
	 * @param id
	 *            the id
	 * @return true if he is able
	 */

	public boolean isAbleToChoose(Long id) {
		Question question = Question.findById(id);
		if (question.author.email.equals(this.email)) {
			return true;
		}
		return false;
	}

	/**
	 * checks whether the user can vote.
	 * 
	 * @param id
	 *            the id
	 * @return true if he is able to vote
	 */
	public boolean isAbleToVote(Long id) {
		Question question = Question.findById(id);
		return (!question.hasVoted(this) && !question.isOwnPost(this));
	}

	/**
	 * checks whether the user already likes the comment.
	 * 
	 * @param id
	 *            The ID of the comment.
	 * 
	 * @return true if he is able to like the comment
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

		if (question.giveValidity() == 0 || milidate < question.giveValidity()) {
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
	 * Calculates the age of the <code>User</code> in years.
	 * 
	 * @return age of the <code>User</code>
	 */
	private int age() {
		Date now = new Date();
		if (birthday != null) {
			long age = now.getTime() - birthday.getTime();
			return (int) (age / ((long) 1000 * 3600 * 24 * 365));
		} else
			return (0);
	}

	/**
	 * Gets the birthday.
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
			System.out
					.println("Sorry wrong Date_Format it's " + DateFormatter.DATE_FORMAT_de);
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
	 * Delete follow question
	 * 
	 * @param question
	 *            the question
	 */
	public void deleteFollowQ(Question question) {
		int index = this.followQ.indexOf(question);
		this.followQ.remove(index);
		this.save();
	}

	/**
	 * Delete follow user
	 * 
	 * @param user
	 *            the user
	 */
	public void deleteFollowU(User user) {
		int index = this.followU.indexOf(user);
		this.followU.remove(index);
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
	 * Adds the vote.
	 * 
	 * @param vote
	 *            the vote
	 * @return the user
	 */
	public User addVote(Vote vote) {
		votes.add(vote);
		save();
		return this;
	}

	/**
	 * Adds the answer.
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
	 * Adds the question.
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
	 * Activities.
	 * 
	 * @return the list
	 */
	public List<VotablePost> activities() {
		return VotablePost.find("author like ? order by timestamp desc", this).fetch();
	}

	/**
	 * Follow acitvities.
	 * 
	 * @param number
	 *            the number
	 * @return the list
	 */
	public List<VotablePost> followAcitvities(int number) {
		List<VotablePost> activities = new ArrayList<VotablePost>();

		for (User user : this.followU) {
			List<VotablePost> postList = VotablePost.find(
					"author like ? order by timestamp desc", user).fetch();
			for (VotablePost post : postList) {
				activities.add(post);
			}
		}

		for (Question question : this.followQ) {
			List<VotablePost> postList = VotablePost.find(
					"post like ? order by timestamp desc", question).fetch();
			for (VotablePost post : postList) {
				activities.add(post);
			}
		}

		PostActivityComperator comp = new PostActivityComperator();

		Collections.sort(activities, comp);

		while (activities.size() > number) {
			activities.remove(activities.size() - 1);
		}

		return activities;
	}

	/**
	 * Gets the reputation points.
	 * 
	 * @return the points for the Reputation graph
	 */
	public List<ReputationPoint> getReputationPoints() {
		return rating.totalRepPoint;
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
		Iterator<ReputationPoint> it = points.iterator();

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
	 * Quote content.
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
	 * Spam.
	 * 
	 * @param post
	 *            spam post
	 */
	public void spam(Post post) {
		this.spamreport.add((VotablePost) post);
		this.save();
	}

	/**
	 * Checks if is spam.
	 * 
	 * @return true, if is spam
	 */
	public boolean isSpam() {
		return (this.spamreport.size() >= SPAM_REPORT);
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
}
