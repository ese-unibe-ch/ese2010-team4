package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Vote extends Model {

	boolean result;

	@ManyToOne
	public User user;
	@ManyToOne
	public Post post;

	public Vote(User user, Post post, boolean result) {
		this.result = result;
		this.user = user;
		this.post = post;
		user.addVote(this);
	}

}
