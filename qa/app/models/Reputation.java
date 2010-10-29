package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Reputation extends Model {

	public int questionRep;
	public int answerRep;
	public int bestAnswerRep;
	public int totalRep;
	public int penalty;

	// public ReputationValues repVal;

	public Reputation() {
		this.questionRep = 0;
		this.answerRep = 0;
		this.bestAnswerRep = 0;
		this.totalRep = 0;
		this.penalty = 0;
		// this.repVal = ReputationValues.getInstance();
	}

	public void totalRep() {
		totalRep = questionRep + answerRep + bestAnswerRep + penalty;

		if (totalRep < 0) {
			totalRep = 0;
		}

	}

	public void voteUPAnswer() {
		this.answerRep += 10;
		totalRep();

	}

	public void voteDown() {
		this.answerRep -= 2;
		totalRep();

	}

	public void votedUPQuestion() {
		this.questionRep += 5;
		totalRep();

	}

	public void bestAnswer() {
		this.bestAnswerRep += 50;
		totalRep();
	}

	public void penalty() {
		this.penalty += -1;
		totalRep();
	}

	public String toString() {
		return Integer.toString(totalRep);
	}
}
