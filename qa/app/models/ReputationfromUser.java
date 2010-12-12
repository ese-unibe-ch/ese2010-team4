package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/**
 * Gives for every User an reputation form every User
 * @author juerg
 *
 */
@Entity
public class ReputationfromUser extends Model {
	

	
	public int reputation;
	@ManyToOne
	public User user;
	
	public ReputationfromUser(User user){
		this.user = user;
		this.reputation = 0;
	}
}
