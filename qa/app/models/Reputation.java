package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class Reputation extends Model {

	public int questionRep = 0;
	public int answerRep = 0;
	public int bestAnswerRep = 0;
	public int totalRep = 0;
	public int penalty = 0;
	
	@OneToOne
	public User user;

	public ReputationValues repVal;

	public Reputation(User user) {
		this.repVal = ReputationValues.getInstance();
		this.user = user;
	}

	public void totalRep() {
		totalRep = questionRep + answerRep + bestAnswerRep + penalty;
		

		if (totalRep < 0) {
			totalRep = 0;
		}

	}

	public void voteUPAnswer() {
		this.answerRep += repVal.voteUPAnswer;
		totalRep();

	}

	public void voteDown() {
		this.answerRep += repVal.voteDown;
		totalRep();

	}

	public void votedUPQuestion() {
		this.questionRep += repVal.voteUPQuestion;
		totalRep();

	}

	public void penalty() {
		this.penalty += repVal.penalty;
		totalRep();
	}
}
