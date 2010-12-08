package models;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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

	private static final int SPAM_VALUE = 2;
	private final int RAND_NUMBER = 3;
	public HashSet<User> spamreport;
	public Date timestamp;
	public String fullname;
	public int voting;
	public String attachmentPath;
	public static URLHandler uHandler = new URLHandler();
	public static HTMLHandler hHandler = new HTMLHandler();
	public HashSet<User> likers;

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
		this.spamreport = new HashSet<User>();
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

	/**
	 * used in Application.java for display tagged.html
	 * 
	 * @return all questions witch matches one of the specified tags
	 */
	public static List<Post> findTaggedWith(String... tags) {
		List<Post> hits = new ArrayList<Post>();
		hits = Question
				.find("select distinct p from Question p join p.tags as t where t.name in (:tags)")
				.bind("tags", tags).fetch();
		return hits;
	}

	/**
	 * Gets similar questions. For a specific number of tags.
	 * 
	 * @param minimumtags
	 *            specifies the numbers of equal tags
	 * @param all
	 *            badgeTags form the user which wrote the answer
	 * @param user
	 *            the user which wrote the answer
	 * @return the random number of similar questions
	 */
	public ArrayList<Post> getNotAnsweredSimilarPosts(int minimumtags,
			Set<Tag> tags, User user) {

		Set<Post> posts = this.getSimilarPosts(minimumtags, tags);
		ArrayList<Post> removeposts = new ArrayList<Post>();
		for (Post post : posts) {
			for (Answer answer : ((Question) post).answers) {
				if (answer.author.equals(user)) {
					removeposts.add(post);
				}
			}
		}
		posts.removeAll(removeposts);
		ArrayList<Integer> randoms = this.getRandom(posts.size(),
				this.RAND_NUMBER, posts);

		ArrayList<Post> oldposts = new ArrayList<Post>(posts);
		ArrayList<Post> newposts = new ArrayList<Post>();

		for (int i : randoms) {
			newposts.add(oldposts.get(i));
		}

		return newposts;
	}

	/**
	 * Gets the several random numbers in a array.
	 * 
	 * @param randscope
	 *            the area of the random number
	 * @param number
	 *            how many numbers you wish
	 * @param posts
	 *            the posts
	 * @return array of randoms
	 */
	private ArrayList<Integer> getRandom(int randscope, int number,
			Set<Post> posts) {

		Set<Integer> randnumb = new HashSet();
		Random rand = new Random();

		if (randscope < number) {
			number = randscope;
		}

		while (randnumb.size() < number) {
			randnumb.add(rand.nextInt(randscope));
		}

		return new ArrayList<Integer>(randnumb);
	}

	/**
	 * @param specifies
	 *            the numbers of equal tags (minimum)
	 * @return all questions or answers with enough equal tags
	 */

	public Set<Post> getSimilarPosts(int minimumtags, Set<Tag> tags) {
		Set<Post> set = new HashSet<Post>();
		for (Tag tag : tags) {
			set.addAll(Question.findTaggedWith(tag.name));
		}
		List<Post> posts = new ArrayList<Post>();
		for (Post post : set) {

			int counter = 0;
			for (Tag tag : tags) {

				if (post.tags.contains(tag)) {
					counter++;
				}
			}
			if (counter < minimumtags) {
				posts.add(post);
			}
		}
		set.removeAll(posts);
		set.remove(this);
		return set;
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

	public String getDate() {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		DateFormat formater;
		if (user.language.equalsIgnoreCase("fr")) {
			formater = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
					DateFormat.MEDIUM, Locale.FRANCE);
		}

		return formater.format(timestamp);
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
	 * Reports this post as spam
	 * 
	 * @param user
	 *            which has reported
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

	/**
	 * Vote a Post up or Down
	 * 
	 * @param user
	 *            the user which has voted
	 * @param result
	 *            down is false up is true
	 * @return the voted Post
	 */
	public Post vote(User user, boolean result) {
		Vote vote = new Vote(user, this, result).save();
		this.votes.add(vote);

		if (result) {
			this.author.rating.voteUP(this);
			this.author.rating.save();
			this.author.save();
		}

		else {

			this.author.rating.voteDown(this);
			this.author.rating.save();
			this.author.save();
			user.rating.penalty();
			user.rating.save();
			user.save();
		}
		this.voting();
		this.save();
		return this;
	}

}