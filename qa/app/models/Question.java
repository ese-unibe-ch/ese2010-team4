package models;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * A question with content, timestamp, owner and voting.
 * 
 * @author dwettstein
 * 
 */
@Entity
public class Question extends Model {

	public Date timestamp;
	public int voting;

	@Lob
	@Required
	@MaxSize(10000)
	public String content;

	@Required
	@ManyToOne
	public User author;
	
	
	public ArrayList<Answer> answers;	
	public ArrayList<User> userVoted;

	public Question(User author, String content) {
		this.author = author;
		this.content = content;
		this.timestamp = new Date(System.currentTimeMillis());
		this.voting = 0;
		this.answers = new ArrayList<Answer>();
		this.userVoted = new ArrayList<User>();
	}

	public Question addAnswer(User author, String content) {
		assert (this.answers.isEmpty());
		Answer newAnswer = new Answer(this, author, content).save();
		this.answers.add(newAnswer);
		this.save();
		return this;
	}

	public Question previous() {
		return Question
				.find("timestamp < ? order by timestamp desc", timestamp)
				.first();
	}

	public Question next() {
		return Question.find("timestamp > ? order by timestamp asc", timestamp)
				.first();
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

		for (User comuser : userVoted) {
			if (user.email.equals(comuser.email)) {
				return true;
			}
		}

		return false;
	}

}
