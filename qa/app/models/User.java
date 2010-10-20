package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import javax.persistence.Entity;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * A user with name and first-name.
 * 
 */
@Entity
public class User extends Model {

	protected Date birthday;
	public String website = "";
	public String work = "";
	public String aboutMe = "";
	public String favoriteLanguages;
	public String avatarURL = "http://imgur.com/j2Qvy.jpg";

	public static final String DATE_FORMAT = "dd-MM-yyyy";
	public Date lastLogOff;
	public ArrayList<Question> follows;

	@Email
	@Required
	public String email;

	@Required
	public String password;

	@Required
	public String fullname;

	@Required
	public boolean isAdmin;
	private LinkedList<Post> recentPosts = new LinkedList<Post>();

	public User(String fullname, String email, String password) {
		this.fullname = fullname;
		this.email = email;
		this.password = password;
		this.isAdmin = false;
		lastLogOff = new Date(System.currentTimeMillis());
		follows = new ArrayList<Question>();
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

		Question question = this.findQuestion(id);

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

		Question question = this.findQuestion(id);

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

		Question question = this.findQuestion(id);
		// changes actual date to date in milisec
		Date actualdate = new Date();
		long milidate = actualdate.getTime();

		if (question.validity == 0 || milidate < question.validity) {
			return true;
		}
		return false;
	}

	/**
	 * Helper method
	 * 
	 * @param id
	 * @return searched question
	 */
	private Question findQuestion(Long id) {
		return Question.find("byId", id).first();
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

	// JW: refactor
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
			new User(fullname, email, password).save();
			message = "Hello, " + fullname + ", please log in";
		}

		return message;
	}

	public ArrayList<Question> removeNull() {
		int index = 0;
		while (index < this.follows.size()) {
			try {
				Long id = this.follows.get(index).getId();
				Question q = Question.findById(id);
				q.toString();
				index++;
			}

			catch (Exception e) {
				this.follows.remove(index);
			}

		}
		this.save();
		return this.follows;
	}

	public void deleteFollow(Question question) {
		int index = this.follows.indexOf(question);
		this.follows.remove(index);
		this.save();
	}
}
