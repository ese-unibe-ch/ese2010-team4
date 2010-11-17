package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Reputation extends Model {

	// all the single reputations total
	public int questionRep;
	public int answerRep;
	public int bestAnswerRep;
	public int totalRep;
	public int penalty;

	@OneToMany
	public List<ReputationPoint> totalRepPoint;

	public Reputation() {
		this.totalRepPoint = new ArrayList<ReputationPoint>();
		this.questionRep = 0;
		this.answerRep = 0;
		this.bestAnswerRep = 0;
		this.totalRep = 0;
		this.penalty = 0;
	}

	public void totalRep() {

		totalRep = questionRep + answerRep + bestAnswerRep + penalty;

		if (totalRep < 0) {
			totalRep = 0;
		}

		if (this.totalRepPoint.size() == 0) {
			ReputationPoint p = new ReputationPoint(0).save();
			this.totalRepPoint.add(p);
		}

		ReputationPoint p = new ReputationPoint(this.totalRep).save();
		this.totalRepPoint.add(p);
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
