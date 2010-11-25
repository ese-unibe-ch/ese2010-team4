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
		historys.add(index, history);
		save();
		return this;
	}

	public boolean isAbleToVoteAnswer(User user) {
		return (!hasVoted(user) && !isOwnPost(user));
	}

	public Post vote(User user, boolean result) {
		Vote vote = new Vote(user, this, result).save();
		votes.add(vote);

		if (result) {
			author.rating.voteUPAnswer();
			author.rating.save();
			author.save();
		} else {
			author.rating.voteDown();
			author.rating.save();
			author.save();
			user.rating.penalty();
			user.rating.save();
			user.save();
		}

		voting();
		save();

		return this;
	}
}
