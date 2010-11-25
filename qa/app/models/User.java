package models;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * A user with name and first-name. Question
 */
@Entity
public class User extends Model {

	public int counter;
	protected Date birthday;
	public String fullname = "";
	public String website = "";

	public String work = "";
	public String aboutMe = "";
	public String favoriteLanguages;
	public String quotedContent = "";

	@Required
	public String avatarPath = "/public/uploads/standardAvatar.png";

	public static final String DATE_FORMAT_de = "dd-MM-yyyy";
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

	public User() {
		this.votes = new ArrayList<Vote>();
		this.posts = new ArrayList<Post>();
		this.isAdmin = false;
		lastLogOff = new Date(System.currentTimeMillis());
		this.followQ = new ArrayList<Question>();
		this.followU = new ArrayList<User>();
		this.counter = 0;
	}

	public User(String username, String email, String password) {
		this.votes = new ArrayList<Vote>();
		this.posts = new ArrayList<Post>();
		this.username = username;
		this.email = email;
		this.password = password;
		this.isAdmin = false;
		lastLogOff = new Date(System.currentTimeMillis());
		this.followQ = new ArrayList<Question>();
		this.followU = new ArrayList<User>();
		this.counter = 0;
	}

	public static User login(String username, String password) {
		return find("byUsernameAndPassword", username, password).first();
	}

	/**
	 * Checks whether the user can vote a question or not.
	 * 
	 * @param id
	 *            The ID of the question.
	 * @return true if he is able to vote
	 */
	public boolean isAbleToVote(Long id) {
		Question question = Question.findById(id);

		return (!question.hasVoted(this) && !question.isOwnPost(this));
	}

	/**
	 * checks whether the user already likes the comment
	 * 
	 * @param id
	 *            The ID of the comment.
	 * @return true if he is able to like the comment
	 */
	public boolean alreadyLikesComment(Long id) {
		Comment comment = Comment.findById(id);

		return comment.userLikePost(this);
	}

	/**
	 * Check if the validation time form a question is not over.
	 * 
	 * 
	 * @param id
	 *            the ID of the question to check.
	 * @return true if the user can change the question.
	 */
	public boolean hasTimeToChange(Long id) {
		Question question = Question.findById(id);
		// changes actual date to date in milisec
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
					answer.author.rating.bestAnswer();
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
	 * Turns the Date object d into a String using the format given in the
	 * constant DATE_FORMAT_de.
	 */
	private String dateToString(Date d) {
		if (d != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_de);
			return fmt.format(d);
		} else {
			return "dd-mm-yyyy";
		}
	}

	/**
	 * Turns the String object s into a Date assuming the format given in the
	 * constant DATE_FORMAT_de
	 * 
	 * @throws ParseException
	 */
	private Date stringToDate(String s) throws ParseException {
		if (s != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_de);
			return fmt.parse(s);
		} else {
			return null;
		}
	}

	public String getBirthday() {
		return dateToString(birthday);
	}

	public void setBirthday(String birthday) {
		try {
			this.birthday = stringToDate(birthday);
		} catch (ParseException e) {
			System.out
					.println("Sorry wrong Date_Format it's " + DATE_FORMAT_de);
		}
	}

	/**
	 * Calculates the age of the <code>User</code> in years
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
	 * Creates a new user if all requirements are met
	 * 
	 * @param fullname
	 *            the fullname
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
		// add the reputation
		newUser.rating = new Reputation().save();
		newUser.save();
		String message = username + "<br> welcome on A4Q, please log in";

		return message;
	}

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

	public void deleteFollowQ(Question question) {
		int index = this.followQ.indexOf(question);
		this.followQ.remove(index);
		this.save();
	}

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

	public User addVote(Vote vote) {
		votes.add(vote);
		save();
		return this;
	}

	public User addAnswer(Answer answer) {
		posts.add(answer);
		save();
		return this;
	}

	public Question addQuestion(String title, String content) {
		Question newQuestion = new Question(this, title, content).save();
		posts.add(newQuestion);
		save();
		return newQuestion;
	}

	public User addComment(Comment comment) {
		posts.add(comment);
		save();
		return this;

	}

	public int countQuestions() {
		return Question.find("byAuthor", this).fetch().size();
	}

	public int countAnswers() {
		return Answer.find("byAuthor", this).fetch().size();
	}

	public List<Post> activities() {
		return Post.find("author like ? order by timestamp desc", this).fetch();
	}

	public List<Post> followAcitvities(int number) {
		List<Post> activities = new ArrayList<Post>();

		for (User user : this.followU) {
			List<Post> postList = Post.find(
					"author like ? order by timestamp desc", user).fetch();
			for (Post post : postList) {
				activities.add(post);
			}
		}

		for (Question question : this.followQ) {
			List<Post> postList = Post.find(
					"post like ? order by timestamp desc", question).fetch();
			for (Post post : postList) {
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
	 * 
	 * @return the points for the Reputation graph
	 **/
	public List<ReputationPoint> getReputationPoints() {
		return rating.totalRepPoint;
	}

	public ReputationPoint getReputationPoint(int i) {
		if (i < this.getReputationPoints().size()) {
			return this.getReputationPoints().get(i);
		} else if (this.getReputationPoints().size() > 0) {
			return new ReputationPoint(
					getReputationPoints().get(getReputationPoints().size() - 1).repvalue,
					getReputationPoints().get(getReputationPoints().size() - 1).timestamp)
					.save();
		} else {
			return new ReputationPoint(0, 0);
		}
	}

	public String graphData() throws IOException {
		StringBuffer strbuffer = new StringBuffer();
		strbuffer.append("[");

		List<ReputationPoint> points = this.getReputationPoints();

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

	public void quoteContent(String content, String quoted) {
		// Deleting first <p> and last </p>
		// String qContent = content.substring(3, content.lastIndexOf("</p>") -
		// 1);
		quotedContent = "" + "\n\n\n\n\n<br>" + "Quoted: " + quoted
				+ "<blockquote>" + content + "</blockquote>";
	}

	public String getQuotedContent() {
		String content = quotedContent;
		quotedContent = "";
		save();
		return content;
	}
}
