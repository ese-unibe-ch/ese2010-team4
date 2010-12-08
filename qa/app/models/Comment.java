package models;

import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Comment extends VotablePost {

	@ManyToOne
	public VotablePost post;	
	
	public HashSet<User> likers;

	public Comment(User author, VotablePost post, String content) {
		super(author, content);
		this.post = post;
		post.addComment(this);
		author.addPost(this);
		likers = new HashSet();
	}
	
	/**
	 * Returns the number of Users who like the post.
	 * 
	 * @return count of likers
	 */
	public int numberOfLikers() {
		return likers.size();
	}

	/**
	 * Add the User to the list of Users who like the post.
	 * 
	 * @param user
	 *            The user which will be added to the likers list.
	 * 
	 * @return true if the user was not already in the list.
	 */
	public boolean addLiker(User user) {
		boolean out = likers.add(user);
		save();
		return out;
	}

	/**
	 * Remove a user from the list of Users who like the post.
	 * 
	 * @param user
	 *            The user which will be removed from the likers list.
	 * 
	 * @return true if the list contained the user.
	 */
	public boolean removeLiker(User user) {
		boolean out = likers.remove(user);
		save();
		return out;
	}

	/**
	 * Check if a user likes the post.
	 * 
	 * @return true if the the user likes the post
	 */
	public boolean userLikePost(User user) {
		return likers.contains(user);
	}
	
	@Override
	public Post addHistory(Post post, String title, String content) {
		return null;
	}

}