package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * A question with content, timestamp, ownertests and voting.
 * 
 */
@Entity
public class Question extends VotablePost {
	public static final int DELAY = 10000;
	public static long waitforlist = 0;
	public long validity;
	public String title;
	public boolean hasBestAnswer;

	@OneToMany(mappedBy = "question", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Answer> answers;

	public Question(User author, String title, String content) {
		super(author, content);
		this.answers = new ArrayList<Answer>();
		this.title = title;
	}

	/**
	 * Shows the title of a question.
	 * 
	 * @return tht tile.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Adds an answer to a specific question. Use this if you want to add a
	 * Answer to a specific question.
	 * 
	 * @param author
	 *            form the answer
	 * @param content
	 *            from the answer
	 * @return the updated question
	 */
	public Question addAnswer(User author, String content) {
		Answer newAnswer = new Answer(this, author, content).save();
		this.answers.add(newAnswer);
		this.save();
		return this;
	}

	/**
	 *Adds an new answer to a specific question. Use this method if an answer
	 * already exists.
	 * 
	 * @param answer
	 *            which must be added
	 * @return the updated question
	 */
	public Question addNewAnswer(Answer answer) {
		this.answers.add(answer);
		this.save();
		return this;
	}

	/**
	 * Checks whether a best answer is selected.
	 * 
	 * @return true if a answer is selected as best answer.
	 */
	public boolean hasChosen() {
		for (Answer answer : answers) {
			if (answer.isBestAnswer) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper method for change best answer. Sets for every answer of a question
	 * the best answer flag to false.
	 * 
	 */
	public void setAllAnswersFalse() {
		for (Answer answer : answers) {
			answer.isBestAnswer = false;
			answer.save();
		}
	}

	/**
	 * Sets a validity for a question. When the validity has expired, the
	 * question has a fix bestAnswer.
	 * 
	 * @param delay
	 *            time how long the user can change the best answer.
	 */
	public void addValidity(long delay) {
		Date date = new Date();
		this.validity = date.getTime() + delay;
		this.save();
	}

	public Post addHistory(Post post, String title, String content) {
		History history = new History(this, title, content).save();
		int index = historys.size();
		this.historys.add(index, history);
		this.save();
		return this;
	}

	/**
	 * Gives a bestAnswer to the question.
	 * 
	 * @param answer
	 *            the answer which is best.
	 * @return updated question
	 */
	public Question bestAnswer(Answer answer) {

		this.setAllAnswersFalse();
		answer.isBestAnswer = true;
		answer.save();
		this.addValidity(DELAY);
		this.save();
		return this;
	}

	/**
	 * Stops the countdown for choose a best answer.
	 * 
	 * @return the updated question
	 */
	public Question hasNotBestAnswer() {
		this.hasBestAnswer = false;
		this.validity = 0;
		this.save();
		return this;
	}
}
