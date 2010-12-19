package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
	
	@OneToMany
	public List<PostReputation> indepentReputation;

	public ReputationfromUser(User user){
		this.user = user;
		this.reputation = 0;
		this.indepentReputation = new ArrayList<PostReputation>();
	}
	
	/**
	 * Creates a new Post with the specific reputation.
	 * 
	 * @param post which receives the reputation
	 * @param reputation which the post receives
	 * @return a PostReputation
	 */
	public ReputationfromUser createPostReputation(Post post, int rep){
		PostReputation postRep = new PostReputation(post, rep).save();
		this.indepentReputation.add(postRep);
		this.save();
		return this;
	}
}


