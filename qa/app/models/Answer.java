package models;

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

	@Required
	@ManyToOne
	public User author;

	@Required
	@OneToOne
	public Question question;

	public Answer(Question question, User author, String content) {
		this.question = question;
		this.author = author;
		this.content = content;
		this.timestamp = new Date(System.currentTimeMillis());
		this.voting = 0;
	}

	public String toString() {
		return content;
	}
}
