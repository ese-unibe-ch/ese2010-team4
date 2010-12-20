package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
/** An single vote for an post*/
public class Vote extends Model {

	boolean result;

	@ManyToOne
	public User user;
	@ManyToOne
	public VotablePost post;

	public Vote(User user, VotablePost post, boolean result) {
		this.result = result;
		this.user = user;
		this.post = post;
		user.addVote(this);
	}

}
