package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
	public int voting;
	public String fullname;

	@Lob
	@Required
	@MaxSize(10000)
	public String content;

	@Required
	@ManyToOne
	public User author;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	public List<Comment> comments;

	@OneToMany
	public List<User> userVoted;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	public List<History> history;

	public Post(User author, String content) {

		this.userVoted = new ArrayList<User>();
		this.history = new LinkedList<History>();
		this.comments = new ArrayList<Comment>();
		History hist = new History(this, "", content).save();
		this.history.add(0, hist);
		this.author = author;
		this.content = content;
		this.timestamp = new Date(System.currentTimeMillis());
		this.voting = 0;
		author.recentPosts.add(this);

	}

	public String toString() {
		return content;
	}

	public void voteUp(User user) {
		voting++;
		this.userVoted.add(user);
		this.save();
	}

	public void voteDown(User user) {
		voting--;
		this.userVoted.add(user);
		this.save();
	}

	public boolean hasVoted(User user) {
		if (userVoted != null) {
			for (User comuser : userVoted) {
				if (user.email.equals(comuser.email)) {
					return true;
				}
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
	public void addToHistory(String title, String content) {
		History hist = new History(this, title, content).save();
		history.add(0, hist);
		this.save();

	}

}