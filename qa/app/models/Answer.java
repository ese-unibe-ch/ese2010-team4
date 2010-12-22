package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;

/**
 * A answer with content, timestamp, owner and voting.
 */
@Entity
public class Answer extends VotablePost {

	@Required
	@ManyToOne
	public Question question;

	public boolean isBestAnswer = false;

	public Answer(Question question, User author, String content) {
		super(author, content);
		this.question = question;
		question.addNewAnswer(this);
		author.addPost(this);
	}

	@Override
	public Post addHistory(Post post, String title, String content) {
		History history = new History(this, title, content).save();
		int index = historys.size();
		historys.add(index, history);
		save();
		return this;
	}

	/**
	 * Checks whether an user is allowed to vote an Answer.
	 * 
	 * @param user
	 * @return true if the user is allowed to vote
	 */
	public boolean isAbleToVoteAnswer(User user) {
		return (!hasVoted(user) && !isOwnPost(user));
	}

	/**
	 * Gives the question to the specific answer.
	 * 
	 * @return the question from the Answer
	 */
	public Question giveQuestion() {
		return this.question;
	}

	/**
	 * Formats the content of a Answer.
	 * 
	 * @return the formatted and shorted content
	 */
	public String getBegin() {
		String s = "";
		if (this.content.length() > 15) {
			s = s + this.content.substring(0, 14);
		} else {
			s = content;
		}
		return s;
	}
}
