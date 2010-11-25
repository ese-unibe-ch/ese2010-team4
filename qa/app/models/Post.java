package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.urlHTMLhandler.HTMLHandler;
import models.urlHTMLhandler.URLHandler;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import controllers.Secure;

/**
 * The Class Post.
 */
@Entity
public abstract class Post extends Model {

	public Date timestamp;
	public String fullname;
	public int voting;
	public String attachmentPath;
	public static URLHandler uHandler = new URLHandler();
	public static HTMLHandler hHandler = new HTMLHandler();
	private HashSet<User> likers;

	@Lob
	@Required
	@MaxSize(10000)
	public String content;

	@Required
	@ManyToOne
	public User author;

	@OneToMany(mappedBy = "post", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Comment> comments;

	@OneToMany(mappedBy = "post", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;

	@OneToMany(mappedBy = "post", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<History> historys;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Tag> tags;

	public abstract Post addHistory(Post post, String title, String content);

	/**
	 * Add an vote
	 * 
	 * @param user
	 * @param result
	 * @return the voted post
	 */
	public abstract Post vote(User user, boolean result);

	public Post(User author, String content) {

		this.votes = new ArrayList<Vote>();
		this.historys = new ArrayList<History>();
		this.comments = new ArrayList<Comment>();
		this.tags = new TreeSet<Tag>();
		this.author = author;
		this.content = uHandler.check(hHandler.check(content));
		this.timestamp = new Date();
		this.voting = 0;
		likers = new HashSet();
	}

	public String toString() {
		return content;
	}

	/**
	 * Checks if the post belongs to the given user.
	 * 
	 * @param user
	 *            The user to check.
	 * @return true if the post belongs to the user.
	 */
	public boolean isOwnPost(User user) {
		return user.equals(author);
	}

	/**
	 * Checks if the post belongs to the loggedIn user.
	 * 
	 * @return true if the post belongs to the loggedIn user.
	 */
	public boolean isOwnPost() {
		if (Secure.Security.isConnected()) {
			User connectedUser = User.find("byUsername",
					Secure.Security.connected()).first();
			if (connectedUser != null) {
				return isOwnPost(connectedUser);
			}

		}

		return false;
	}

	public boolean hasVoted(User comuser) {

		for (Vote vote : votes) {
			if (vote.user.equals(comuser)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Add a new History to the post
	 * 
	 * @param title
	 *            necessary if is an question
	 * @param content
	 *            of the post
	 */

	/**
	 * Count the positive and negative votes
	 * 
	 * @return votestatus
	 */
	public int voting() {

		int status = 0;

		for (Vote vote : this.votes) {

			if (vote.result) {
				status++;
			} else {
				status--;
			}
		}

		voting = status;

		return status;

	}

	public Post addComment(Comment comment) {
		this.comments.add(comment);
		this.save();
		return this;

	}

	public Post tagItWith(String name) {
		if (!(name.equals("") || name.isEmpty() || name.equals(null))) {
			tags.add(Tag.findOrCreateByName(name));
		}
		return this;
	}

	public static List<Post> findTaggedWith(String... tags) {
		return Question
				.find("select distinct p from Question p join p.tags as t where t.name in (:tags)")
				.bind("tags", tags).fetch();
	}

	public List<Post> similarPosts() {
		List<Post> list = new ArrayList<Post>();
		if (!this.tags.isEmpty()) {
			for (Tag tag : tags) {
				List<Post> temp = findTaggedWith(tag.name);
				for (Post post : temp) {
					if (!list.contains(post)) {
						list.add(post);
					}
				}
			}
		}
		list.remove(this);
		return list;
	}

	public boolean checkInstance() {
		return this instanceof Question;
	}

	public boolean isQuestion() {
		return this instanceof Question;
	}

	public boolean isAnswer() {
		return this instanceof Answer;
	}

	public boolean isCommentAnswer() {
		if (this instanceof Comment) {
			if (((Comment) this).post instanceof Answer) {
				return true;
			}
		}
		return false;
	}

	public boolean isCommentQuestion() {
		if (this instanceof Comment) {
			if (((Comment) this).post instanceof Question) {
				return true;
			}
		}
		return false;
	}

	public Question findQuestion() {
		if (this.isQuestion())
			return (Question) this;
		if (this.isAnswer())
			return ((Answer) this).question;
		if (this.isCommentAnswer())
			return ((Answer) ((Comment) this).post).question;
		else
			return (Question) ((Comment) this).post;
	}

	@SuppressWarnings("deprecation")
	public String getDate() {
		return this.timestamp.toLocaleString();

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

	/**
	 * Clear the list of users who like the post.
	 */
	public void clearLikers() {
		likers.clear();
	}

}