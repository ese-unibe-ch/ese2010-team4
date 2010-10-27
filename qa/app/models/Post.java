package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * The Class Post.
 */
@Entity
public abstract class Post extends Model {

	public Date timestamp;
	public String fullname;
	public int voting;

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
	 * @return the votet post
	 */
	public abstract Post vote(User user, boolean result);

	public Post(User author, String content) {

		this.votes = new ArrayList<Vote>();
		this.historys = new ArrayList<History>();
		this.comments = new ArrayList<Comment>();
		this.tags = new TreeSet<Tag>();
		this.author = author;
		this.content = content;
		this.timestamp = new Date(System.currentTimeMillis());
		this.voting = 0;

		// JW author.recentPosts.add(this);
	}

	public String toString() {
		return content;
	}

	public boolean hasVoted(User comuser) {

		for (Vote vote : votes) {
			if (vote.user.equals(comuser)) {
				return true;
			}
		}

		return false;
	}

	public List<Post> lastChanges(Long questionId, Long userId) {
		Question question = Question.findById(questionId);
		User user = User.findById(userId);
		List<Post> news = new ArrayList<Post>();

		Iterator<Answer> iterA = question.answers.iterator();
		while (iterA.hasNext()) {
			if (iterA.next().timestamp.after(user.lastLogOff)) {
				news.add(iterA.next());
			}
		}

		Iterator<Comment> iterC = question.comments.iterator();
		while (iterC.hasNext()) {
			if (iterC.next().timestamp.after(user.lastLogOff)) {
				news.add(iterC.next());
			}
		}

		return news;
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
		tags.add(Tag.findOrCreateByName(name));
		return this;
	}

	public static List<Post> findTaggedWith(String tag) {
		return Post
				.find(
						"select distinct p from Post p join p.tags as t where t.name = ?",
						tag).fetch();
	}
	
	public boolean checkInstance(){
		return this instanceof Question;
	}

}