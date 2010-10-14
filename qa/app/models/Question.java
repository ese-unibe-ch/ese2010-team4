package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * A question with content, timestamp, owner and voting.
 * 
 * @author dwettstein
 * 
 */
@Entity
public class Question extends Post {

	public long validity;
	
	@OneToMany(mappedBy="question", cascade = CascadeType.ALL)
	public List<Answer> answers;
	
	public Question(User author, String content) {
		super(author, content);
		this.answers = new ArrayList<Answer>();

	}

	public Question addAnswer(User author, String content) {
		Answer newAnswer = new Answer(this, author, content).save();
		this.answers.add(newAnswer);
		this.save();
		return this;
	}

	public Question previous() {
		return Question
				.find("timestamp < ? order by timestamp desc", timestamp)
				.first();
	}

	public Question next() {
		return Question.find("timestamp > ? order by timestamp asc", timestamp)
				.first();
	}

	public boolean hasChoose() {
		
		for(Answer answer: answers){
			if(answer.best){
				return true;
			}
		}
		 
		return false;
	}

	public void setAllAnswersFalse() {
		for(Answer answer: answers){
			answer.best = false;
		}
		
	}

}
