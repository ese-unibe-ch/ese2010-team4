package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;

/**
 * A answer with content, timestamp, owner and voting.
 * 
 */
@Entity
public class Answer extends Post {

	public boolean best = false;

	@Required
	@ManyToOne
	public Question question;

	public Answer(Question question, User author, String content) {
		super(author, content);
		this.question = question;
		question.answers.add(this);
	}
}
