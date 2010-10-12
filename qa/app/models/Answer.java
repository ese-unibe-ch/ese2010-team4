package models;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * A answer with content, timestamp, owner and voting.
 * 
 * @author dwettstein
 * 
 */
@Entity
public class Answer extends Model {

	public Date timestamp;
	public int voting;

	@Lob
	@Required
	@MaxSize(10000)
	public String content;

	@ManyToOne
	public User author;

	@Required
	@OneToOne
	public Question question;

	public ArrayList<User> userVoted;
	
	public Answer(Question question, User author, String content) {
		
		
		this.author = author;
		this.content = content;
		this.timestamp = new Date(System.currentTimeMillis());
		this.voting = 0;
		this.userVoted = new ArrayList<User>();
		this.question = question;
	}

	public String toString() {
		return content;
	}

	public void voteUp(User user) {
		voting++;
		this.userVoted.add(user);
		this.save();

	}

	public void voteDown(User user) {
		voting--;
		this.userVoted.add(user);
		this.save();
	
	}

	public boolean hasVoted(User user) {

		for (User compuser : userVoted) {
			if (user.email.equals(compuser.email)) {
				return true;
			}
		}

		return false;
	}
}
