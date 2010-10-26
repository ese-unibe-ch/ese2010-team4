package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Vote extends Model {

	boolean result;

	@ManyToOne
	User user;

	public Vote(User user, boolean result) {
		this.result = result;
		this.user = user;
		user.votes.add(this);
	}

}
