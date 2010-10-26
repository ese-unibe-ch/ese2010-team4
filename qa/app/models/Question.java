package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * A question with content, timestamp, owner and voting.
 * 
 */
@Entity
public class Question extends Post {

	public long validity;
	public String title;
	public File attachment;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	public List<Answer> answers;

	public Question(User author, String title, String content) {
		super(author, content);
		this.answers = new ArrayList<Answer>();
		this.title = title;
	}

	public Question addAnswer(User author, String content) {
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

	public boolean hasChosen() {
		for (Answer answer : answers) {
			if (answer.best) {
				return true;
			}
		}
		return false;
	}

	public void setAllAnswersFalse() {
		for (Answer answer : answers) {
			answer.best = false;
		}

	}

	public void setValidity(long delay) {
		Date date = new Date();
		this.validity = date.getTime() + delay;
		this.save();

	}

}
