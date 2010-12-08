package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.urlHTMLhandler.HTMLHandler;
import models.urlHTMLhandler.URLHandler;
import controllers.Secure;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public abstract class Post extends Model {

	private static final int SPAM_VALUE = 2;
	protected final int RAND_NUMBER = 3;
	public HashSet<User> spamreport;
	public Date timestamp;
	public static URLHandler uHandler = new URLHandler();
	public static HTMLHandler hHandler = new HTMLHandler();

	@Lob
	@Required
	@MaxSize(10000)
	public String content;
	
	@Required
	@ManyToOne
	public User author;
	
	@OneToMany(mappedBy = "post", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<History> historys;

	public Post(User author, String content) {
		
		this.author = author;
		this.content = uHandler.check(hHandler.check(content));
		this.historys = new ArrayList<History>();
		this.spamreport = new HashSet<User>();
		this.timestamp = new Date();
	
	}
	/**
	 * Adds an history to every post. With this history user can get older versions of their posts.
	 * @param post the one who needs a history
	 * @param title of the post
	 * @param content of the post
	 * @return old post
	 */
	public abstract Post addHistory(Post post, String title, String content);

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

	@SuppressWarnings("deprecation")
	public String getDate() {
		return this.timestamp.toLocaleString();
	
	}

	/**
	 * Reports this post as spam
	 * 
	 * @param user which has reported
	 */
	public void spam(User user) {
		this.spamreport.add(user);
		if (this.isSpam()) {
			this.author.spam(this);
		}
		this.save();
	}

	/**
	 * Checks if is spam.
	 * 
	 * @return true, if is spam
	 */
	public boolean isSpam() {
		return (this.spamreport.size() >= SPAM_VALUE);
	}

}