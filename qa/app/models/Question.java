package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
	public boolean hasBestAnswer;

	@OneToMany(mappedBy = "question", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Answer> answers;

	public Question(User author, String title, String content) {
		super(author, content);
		this.answers = new ArrayList<Answer>();
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public Question addAnswer(User author, String content) {
		Answer newAnswer = new Answer(this, author, content).save();
		this.answers.add(newAnswer);
		this.save();
		return this;
	}

	public Question addNewAnswer(Answer answer) {
		this.answers.add(answer);
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
			if (answer.isBestAnswer) {
				return true;
			}
		}
		return false;
	}

	public void setAllAnswersFalse() {
		for (Answer answer : answers) {
			answer.isBestAnswer = false;
			answer.save();
		}
	}

	public void setValidity(long delay) {
		Date date = new Date();
		this.validity = date.getTime() + delay;
		this.save();
	}

	@Override
	public Post addHistory(Post post, String title, String content) {
		History history = new History(this, title, content).save();
		int index = historys.size();
		this.historys.add(index, history);
		this.save();
		return this;
	}

	public Post vote(User user, boolean result) {
		Vote vote = new Vote(user, this, result).save();
		this.votes.add(vote);

		if (result) {
			this.author.rating.votedUPQuestion();
			this.author.rating.save();
			this.author.save();
		}

		else {

			this.author.rating.voteDown(this);
			this.author.rating.save();
			this.author.save();
			user.rating.penalty();
			user.rating.save();
			user.save();
		}
		this.voting();
		this.save();
		return this;
	}

	public Question bestAnswer(Answer answer) {

		long delay = 10000;
		// necessary if user change his mind
		this.setAllAnswersFalse();
		answer.isBestAnswer = true;
		answer.save();
		this.setValidity(delay);
		this.save();
		return this;
	}
}
