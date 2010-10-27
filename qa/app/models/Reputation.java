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
	


	public ReputationValues repVal;

	public Reputation() {
		this.questionRep = 0;
		this.answerRep = 0;
		this.bestAnswerRep = 0;
		this.totalRep = 0;
		this.penalty = 0;
		this.repVal = ReputationValues.getInstance();
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
	
	public void bestAnswer(){
		this.bestAnswerRep += repVal.bestAnswer;
		totalRep();
	}

	public void penalty() {
		this.penalty += repVal.penalty;
		totalRep();
	}
	
	public String toString(){
		return Integer.toString(totalRep);
	}
}
