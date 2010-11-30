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
	 * Vote up an post.
	 *
	 * @param post the voted answer
	 */
	public void voteUPAnswer(Post post) {
		int reward = 10;
		this.answerRep += reward;
		totalRep();
		addToBadge(post, reward);
	}

	/**
	 * Vote down an post.
	 *
	 * @param post the voted post
	 */
	public void voteDown(Post post) {
		int reward = -2;
		this.answerRep += reward;
		totalRep();
		
		if(post instanceof Answer)
			addToBadge(post, reward);
	}

	/**
	 * Voted up an question.
	 */
	public void votedUPQuestion() {
		this.questionRep += 5;
		totalRep();
	}

	/**
	 * Reputation for the best answer form a question.
	 *
	 * @param post the best answer
	 */
	public void bestAnswer(Post post) {
		int reward = 50;
		this.bestAnswerRep += reward;
		totalRep();
		addToBadge(post, reward);
	}

	/**
	 * Penalty for vote down.
	 */
	public void penalty() {
		this.penalty += -1;
		totalRep();
	}

	public String toString() {
		return Integer.toString(totalRep);
	}
	
	/**
	 * Adds the reward form a vote to every related badge.
	 *
	 * @param post the post of the assessed user
	 * @param reward of the vote
	 */
	public void addToBadge(Post post, int reward){
		
		for(Tag tag: ((Answer)post).question.tags){
			
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

}
