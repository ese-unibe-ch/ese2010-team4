package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * Helper Class for find the Reputation for every Post
 */
@Entity
public class PostReputation extends Model{
	
	public Post post;
	public int reputation;
	
	public PostReputation(Post post, int reputation){
		this.post = post;
		this.reputation = reputation;
	}
}
