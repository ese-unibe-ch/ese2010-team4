package models;

import javax.persistence.Entity;

/**
 * A question with content, timestamp, owner and voting.
 * 
 * @author dwettstein
 * 
 */
@Entity
public class Question extends Post {

	public Question(User author, String content) {
		super(author, content);

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

}
