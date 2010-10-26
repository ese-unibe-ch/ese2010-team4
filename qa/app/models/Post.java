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
	public String fullname;

	@Lob
	@Required
	@MaxSize(10000)
	public String content;

	@Required
	@ManyToOne
	public User author;

	@OneToMany
	public List<Comment> comments;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	public List<Vote> votes;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	public List<History> history;

	public Post(User author, String content) {

		votes = new ArrayList<Vote>();
		this.history = new LinkedList<History>();
		this.comments = new ArrayList<Comment>();
		History hist = new History(this, "", content);
		this.history.add(0, hist);
		this.author = author;
		this.content = content;
		this.timestamp = new Date(System.currentTimeMillis());
		author.recentPosts.add(this);
		this.save();
		hist.save();

	}

	public abstract void voteUp(User user);

	public abstract void voteDown(User user);

	public String toString() {
		return content;
	}

	public boolean hasVoted(User user) {

		if (Vote.find("byEmail", user.email).first() != null) {
			return true;
		}

		return false;
	}

	public String lastChanges(Long questionId, Long userId) {
		Question question = Question.findById(questionId);
		User user = User.findById(userId);
		int newAnswers = 0;
		int newComments = 0;

		try {
			Iterator<Answer> iterA = question.answers.iterator();
			while (iterA.hasNext()) {
				if (iterA.next().timestamp.after(user.lastLogOff)) {
					newAnswers++;
				}
			}
		}

		catch (Exception e) {
		}

		try {
			Iterator<Comment> iterC = question.comments.iterator();
			while (iterC.hasNext()) {
				if (iterC.next().timestamp.after(user.lastLogOff)) {
					newComments++;
				}
			}
		}

		catch (Exception e) {
		}

		return "this question has " + newAnswers + " new answers and "
				+ newComments + " new comments";
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

	/**
	 * Count the positive and negative votes
	 * 
	 * @return votestatus
	 */
	public int voteStatus() {

		int status = 0;

		for (Vote vote : this.votes) {

			if (vote.result) {
				status++;
			} else {
				status--;
			}
		}

		return status;

	}

}