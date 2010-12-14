package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;

/**
 * A answer with content, timestamp, owner and voting.
 * 
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
		History history = new History(this, "", content).save();
		int index = historys.size();
		historys.add(index, history);
		save();
		return this;
	}

	public boolean isAbleToVoteAnswer(User user) {
		return (!hasVoted(user) && !isOwnPost(user));
	}
	
	public String getTitle() {
		String s = "";
		if (this.content.length() < 15) {
			s = this.content;
		}
		else {
			s = this.content.substring(0, 15);
		}
		return s;
	}
}
