package models;

import play.db.jpa.Model;

//@Entity
public class Reputation extends Model {

	public int questionRep = 0;
	public int answerRep = 0;
	public int bestAnswerRep = 0;
	public int totalRep = 0;
	public int penalty = 0;

	public ReputationValues repVal;

	public Reputation() {

	}

	public void totalRep() {
		totalRep = questionRep + answerRep + bestAnswerRep + penalty;
		this.repVal = ReputationValues.getInstance();

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
