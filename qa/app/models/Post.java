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

	public ArrayList<User> userVoted;

	public LinkedList<String> history;

	public Post(User author, String content) {

		this.userVoted = new ArrayList<User>();
		this.history = new LinkedList<String>();
		this.comments = new ArrayList<Comment>();
		this.history.addFirst(content);
		this.author = author;
		this.content = content;
		this.timestamp = new Date(System.currentTimeMillis());
		this.voting = 0;
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
}