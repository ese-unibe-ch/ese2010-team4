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

	/**
	 * Votes a question up and gives the reputation for the user
	 * 
	 * @param user
	 */
	@Override
	public void voteUp(User user) {
		Vote vote = new Vote(user, true).save();
		this.votes.add(vote);
		/**
		 * this.author.rating.voteUPAnswer(); this.author.rating.save();
		 * this.author.save();
		 **/
	}

	@Override
	public void voteDown(User user) {
		Vote vote = new Vote(user, false).save();
		this.votes.add(vote);
		/**
		 * this.author.rating.voteDown(); this.author.rating.save();
		 * this.author.save(); user.rating.penalty(); user.rating.save();
		 * user.save();
		 **/
	}
}
