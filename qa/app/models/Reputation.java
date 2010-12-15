package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

/**
 * The Class Reputation. This class maintained the reputation logic.
 */
@Entity
public class Reputation extends Model {
	
	private static final double MAX_PUSH_SIZE = 0.2;
	private static final int START_CHECK_ARPC = 100;
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
	public List<ReputationfromUser> reputationfromUser;

	@OneToMany
	public List<ReputationPoint> totalRepPoint;
	
	@OneToMany(mappedBy = "reputation", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH})
	public Set<Badge> badges;

	public Reputation() {
		this.totalRepPoint = new ArrayList<ReputationPoint>();
		this.badges = new TreeSet<Badge>();
		this.questionRep = 0;
		this.answerRep = 0;
		this.bestAnswerRep = 0;
		this.totalRep = 0;
		this.penalty = 0;
		this.reputationfromUser = new ArrayList<ReputationfromUser>();
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
	public void voteUP(Post post, User user){
		
		boolean hasvoted = this.hasVoted(user);
		if(!this.hasVoted(user)){
			this.createReputationByUser(user);
		}
		
		ReputationfromUser repbyuser = this.findReputationfromUser(user);		
		if(this.isNotOverPushSize(repbyuser, hasvoted)){		
				if(post instanceof Answer){						
					this.answerRep += ANSWER_REP;				
					repbyuser.reputation += ANSWER_REP;
					totalRep();
					addToBadge((Answer)post, ANSWER_REP);
				}		
				else{
					this.questionRep += QUESTION_REP;
					repbyuser.reputation += QUESTION_REP;
					totalRep();
				}				
				repbyuser.save();
		}
		this.save();
	}


	/**
	 * Vote down an post.
	 *
	 * @param post the voted post
	 */
	public void voteDown(Post post, User user) {
		
		if(post instanceof Answer){
			addToBadge((Answer)post, VOTE_DOWN_REP);
			this.answerRep += VOTE_DOWN_REP;
			totalRep();
		}
		else{
			this.questionRep += VOTE_DOWN_REP;
			totalRep();
		}
		this.save();
	}

	/**
	 * Reputation for the best answer form a question.
	 *
	 * @param post the best answer
	 */
	public void bestAnswer(Answer answer, User user) {
		boolean hasvoted = this.hasVoted(user);
		if(!this.hasVoted(user)){
			this.createReputationByUser(user);
			this.save();
		}
		ReputationfromUser repbyuser = this.findReputationfromUser(user);		

		
		if(this.isNotOverPushSize(repbyuser, hasvoted)){		
			this.bestAnswerRep += BEST_ANSWER_REP;
			repbyuser.reputation +=BEST_ANSWER_REP;
			totalRep();
			addToBadge(answer, BEST_ANSWER_REP);
			repbyuser.save();
			this.save();
		}
		this.save();		
	}

	/**
	 * Penalty for vote down.
	 */
	public void penalty() {
		this.penalty += PENALTY;
		totalRep();
	}
	
	/**
	 * Checks whether the "anti reputation push convetions (arpc)" are met.
	 * 
	 * @param repbyuser the reputation of a single user.
	 * @return true if the conventions are met.
	 */
	private boolean isNotOverPushSize(ReputationfromUser repbyuser, boolean hasvoted) {

		if(this.totalRep < START_CHECK_ARPC || ((double)(repbyuser.reputation))/((double)this.totalRep)<MAX_PUSH_SIZE || !hasvoted){
			return true;
		}
		return false;
	}

	/**
	 * Creates a new reputation by user
	 * @param user which has voted.
	 */
	private void createReputationByUser(User user) {
		ReputationfromUser repbyuser = new ReputationfromUser(user).save();
		this.reputationfromUser.add(repbyuser);
		this.save();
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
	
	/**
	 * Checks if an user has already voted for this user
	 * @param user which has voted
	 * @return true if user has voted this user.
	 */
	public boolean hasVoted(User user){
		for(ReputationfromUser repbyuser: this.reputationfromUser){
			if(repbyuser.user.equals(user)){
				return true;
			}
		}
		return false;
	}
	/**
	 * finds the reputation which gave a specific user to this.
	 * 
	 * @param the user which gave the reputation
	 * @return the reputation of the specific user
	 */
	public ReputationfromUser findReputationfromUser(User user){
		return ReputationfromUser.find("byUser", user).first();
	}
	
	public String toString() {
		return Integer.toString(totalRep);
	}

	
}
