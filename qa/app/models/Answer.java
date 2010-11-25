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

	@Required
	@ManyToOne
	public Question question;

	public boolean isBestAnswer = false;

	public Answer(Question question, User author, String content) {
		super(author, content);
		this.question = question;
		// DR this is to freaky slow part....
		question.addNewAnswer(this);
		author.addAnswer(this);
	}

	@Override
	public Post addHistory(Post post, String title, String content) {
		History history = new History(this, "", content).save();
		int index = historys.size();
		this.historys.add(index, history);
		this.save();
		return this;
	}

	public boolean isAbleToVoteAnswer(User user) {

		if (this.hasVoted(user) || this.author.email.equals(user.email)) {
			return false;
		}

		else
			return true;
	}

	public Post vote(User user, boolean result) {
		Vote vote = new Vote(user, this, result).save();
		this.votes.add(vote);

		if (result) {
			this.author.rating.voteUPAnswer();
			this.author.rating.save();
			this.author.save();
		}

		else {

			this.author.rating.voteDown();
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
}
