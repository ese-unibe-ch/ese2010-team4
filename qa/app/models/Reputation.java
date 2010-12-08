package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

/**
 * The Class Reputation. This class maintained the reputation logic.
 */
@Entity
public class Reputation extends Model {

	private static final int ANSWER_REP = 10;	
	private static final int VOTE_DOWN_REP = -2;
	private static final int QUESTION_REP = 5;
	private static final int PENALTY = -1;
	private static final int BEST_ANSWER_REP = 50;
	
	public int questionRep;
	public int answerRep;
	public int bestAnswerRep;
	public int totalRep;
	public int penalty;

	@OneToMany
	public List<ReputationPoint> totalRepPoint;
	
	@OneToMany
	public Set<Badge> badges;

	public Reputation() {
		this.totalRepPoint = new ArrayList<ReputationPoint>();
		this.badges = new TreeSet<Badge>();
		this.questionRep = 0;
		this.answerRep = 0;
		this.bestAnswerRep = 0;
		this.totalRep = 0;
		this.penalty = 0;
	}

	/**
	 * Calculates after every vote the new reputation for the given user.
	 */
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

	/**
	 * Vote up an post notify the badges
	 * @param post the voted post
	 */
	public void voteUP(Post post){
		if(post instanceof Answer){
			this.answerRep += ANSWER_REP;
			totalRep();
			addToBadge((Answer)post, ANSWER_REP);
		}
		else{
			this.questionRep += QUESTION_REP;
			totalRep();
		}
		this.save();
	}

	/**
	 * Vote down an post.
	 *
	 * @param post the voted post
	 */
	public void voteDown(Post post) {
		
		if(post instanceof Answer){
			addToBadge((Answer)post, VOTE_DOWN_REP);
			this.answerRep += VOTE_DOWN_REP;
			totalRep();
		}
		else{
			this.questionRep += VOTE_DOWN_REP;
			totalRep();
		}
	}

	/**
	 * Reputation for the best answer form a question.
	 *
	 * @param post the best answer
	 */
	public void bestAnswer(Answer answer) {
		this.bestAnswerRep += BEST_ANSWER_REP;
		totalRep();
		addToBadge(answer, BEST_ANSWER_REP);
	}

	/**
	 * Penalty for vote down.
	 */
	public void penalty() {
		this.penalty += PENALTY;
		totalRep();
	}
	
	/**
	 * Adds the reward form a vote to every related badge.
	 *
	 * @param post the post of the assessed user
	 * @param reward of the vote
	 */
	public void addToBadge(Answer answer, int reward){
		
		for(Tag tag: answer.question.tags){
			
			Badge badge = Badge.find("byTagAndReputation", tag, this).first();
			
			if(badge == null){
				badge = new Badge(tag, this).save();
				badge.addReputation(reward);
				badge.save();
				this.badges.add(badge);
				this.save();
			}
			else{
				badge.addReputation(reward);
				badge.save();
			}
		}		
	}
	
	public String toString() {
		return Integer.toString(totalRep);
	}
	

}
