package models;

import java.util.Date;

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

	public String website = "";
	public String work = "";
	public String aboutMe = "";
	public String favoriteLanguages;
	public String avatarURL = "http://imgur.com/j2Qvy.jpg";

	@Email
	@Required
	public String email;

	@Required
	public String password;

	@Required
	public String fullname;

	@Required
	public boolean isAdmin;

	public User(String fullname, String email, String password) {
		this.fullname = fullname;
		this.email = email;
		this.password = password;
		this.isAdmin = false;
	}

	public static User login(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}

	public String toString() {
		return fullname;
	}
	
	/**
	 * checks whether the user can choose the best answer for an question.
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
		//changes actual date to date in milisec
		Date actualdate = new Date();
		long milidate = actualdate.getTime();
		
		
		if (question.validity == 0 || milidate < question.validity) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Helper method
	 * @param id
	 * @return searched question
	 */
	private Question findQuestion(Long id){
		return Question.find("byId", id).first();
	}
}
