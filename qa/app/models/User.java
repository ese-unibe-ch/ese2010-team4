package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

	protected Date birthday;
	public String website = "";

	public String work = "";
	public String aboutMe = "";
	public String favoriteLanguages;

	public String avatarPath = "http://imgur.com/FVWB9.png";

	public static final String DATE_FORMAT = "dd-MM-yyyy";
	public Date lastLogOff;

	@OneToOne
	public Reputation rating;

	@Email
	@Required
	public String email;

	@Required
	public String password;

	@Required
	public String fullname;

	@Required
	public boolean isAdmin;

	@OneToMany
	public List<Question> followQ;
	@OneToMany
	public List<User> followU;
	// @OneToMany
	public ArrayList<Post> recentPosts;

	@OneToMany(mappedBy = "user", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;

	@OneToMany(mappedBy = "author", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Post> posts;

	public User(String fullname, String email, String password) {

		votes = new ArrayList<Vote>();
		posts = new ArrayList<Post>();
		this.fullname = fullname;
		this.email = email;
		this.password = password;
		this.isAdmin = false;
		lastLogOff = new Date(System.currentTimeMillis());
		this.followQ = new ArrayList<Question>();
		this.followU = new ArrayList<User>();
		recentPosts = new ArrayList<Post>();
	}

	public static User login(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}

	public String toString() {
		return fullname;
	}

	/**
	 * checks whether the user can choose the best answer for an question.
	 * 
	 * @param id
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
	 * checks whether the user can vote
	 * 
	 * @param id
	 * @return true if he is able to vote
	 */
	public boolean isAbleToVote(Long id) {

		Question question = Question.findById(id);

		if (!question.hasVoted(this)
				&& !question.author.email.equals(this.email)) {
			return true;
		}

		return false;
	}

	/**
	 * Check if the validation time form a question is not over.
	 * 
	 * 
	 * @param id
	 * @return true if the user has more time to edit the question
	 */

	public boolean hasTimeToChange(Long id) {

		Question question = Question.findById(id);
		// changes actual date to date in milisec
		long milidate = new Date().getTime();

		if (question.validity == 0 || milidate < question.validity) {
			return true;
		}

		else {

			// Set the best Answer
			bestAnswer(question);
			return false;
		}

	}

	/**
	 * Helper method for find best answer
	 * 
	 * @param question
	 *            from the best answer
	 */
	private void bestAnswer(Question question) {

		for (Answer answer : question.answers) {
			if (answer.best) {
				answer.author.rating.bestAnswer();
				answer.author.rating.save();
				answer.author.save();
				answer.save();
				this.save();
			}
		}
	}

	/**
	 * Calculates the age of the <code>User</code> in years
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
	 * Turns the Date object d into a String using the format given in the
	 * constant DATE_FORMAT.
	 */
	private String dateToString(Date d) {
		if (d != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
			return fmt.format(d);
		} else
			return ("dd-mm-yyyy");
	}

	/**
	 * Turns the String object s into a Date assuming the format given in the
	 * constant DATE_FORMAT
	 * 
	 * @throws ParseException
	 */
	private Date stringToDate(String s) throws ParseException {
		if (s != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
			return fmt.parse(s);
		} else
			return (null);
	}

	public String getBirthday() {
		return dateToString(birthday);
	}

	public void setBirthday(String birthday) throws ParseException {
		this.birthday = stringToDate(birthday);
	}

	public int calculateAge() {
		return this.age();
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
	public static String createUser(String fullname, String email,
			String password, String password2) {

		String message = "";
		User user = User.find("byEmail", email).first();

		if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
			message = "you forgot one or more gap's";
		}

		else if (!password.equals(password2)) {
			message = "the password's aren't the same";
		}

		else if (password.length() < 6) {
			message = "your password must be 6 singns or longer";
		}

		else if (user != null && user.email.equals(email)) {

			message = "user allready exists";
		}

		else {
			User newUser = new User(fullname, email, password).save();
			// add the reputation
			newUser.rating = new Reputation().save();
			newUser.save();
			message = "Hello, " + fullname + ", please log in";
		}

		return message;
	}

	public void removeNull() {

		int index = 0;
		while (index < this.followQ.size()) {
			try {
				Long id = this.followQ.get(index).getId();
				Question q = Question.findById(id);
				q.toString();
				index++;
			}

			catch (Exception e) {
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

	public boolean isFollowing(Object o) {
		boolean follows = false;
		if (o instanceof User) {
			if (this.followU.contains((User) o)) {
				follows = true;
			}
		}

		if (o instanceof Question) {
			if (this.followQ.contains((Question) o)) {
				follows = true;
			}
		}
		return follows;
	}

	public User addVote(Vote vote) {
		this.votes.add(vote);
		this.save();
		return this;

	}

	public User addAnswer(Answer answer) {
		this.posts.add(answer);
		this.save();
		return this;
	}

	public Question addQuestion(String title, String content) {
		Question newQuestion = new Question(this, title, content).save();
		this.posts.add(newQuestion);
		this.save();
		return newQuestion;
	}

	public User addComment(Comment comment) {
		this.posts.add(comment);
		this.save();
		return this;

	}

}
