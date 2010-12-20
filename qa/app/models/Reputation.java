package models;

import java.util.ArrayList;
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

	public int reputation;

	@OneToMany
	public List<ReputationfromUser> reputationfromUser;

	@OneToMany
	public List<ReputationPoint> totalRepPoint;

	@OneToMany(mappedBy = "reputation", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public Set<Badge> badges;

	public Reputation() {
		this.totalRepPoint = new ArrayList<ReputationPoint>();
		this.badges = new TreeSet<Badge>();
		this.reputation = 0;
		this.reputationfromUser = new ArrayList<ReputationfromUser>();
	}

	/**
	 * Creates a reputationpoint for the reputationsgraph
	 */
	public void createReputationPoint() {

		if (reputation < 0) {
			reputation = 0;
		}

		if (this.totalRepPoint.size() == 0) {
			ReputationPoint p = new ReputationPoint(0).save();
			this.totalRepPoint.add(p);
		}
		ReputationPoint p = new ReputationPoint(this.reputation).save();
		this.totalRepPoint.add(p);
	}

	/**
	 * Vote up an post notifies the badges
	 * 
	 * @param post
	 *            the voted post
	 */
	public void voteUP(Post post, User user) {

		boolean hasvoted = this.hasVoted(user);
		if (!this.hasVoted(user)) {
			this.createReputationByUser(user);
		}

		ReputationfromUser repbyuser = this.findReputationfromUser(user);

		if (post instanceof Answer) {
			repbyuser.createPostReputation(post, ANSWER_REP);
			while (this.isNotOverPushSize(repbyuser, hasvoted)
					&& repbyuser.indepentReputation.size() > 0) {
				handleReputation(repbyuser);
				addToBadge((Answer) repbyuser.indepentReputation.get(0).post,
						repbyuser.indepentReputation.get(0).reputation);
				repbyuser.indepentReputation.remove(0);
				this.save();
			}
			repbyuser.save();
		} else {
			repbyuser.createPostReputation(post, QUESTION_REP);

			while (this.isNotOverPushSize(repbyuser, hasvoted)
					&& repbyuser.indepentReputation.size() > 0) {
				handleReputation(repbyuser);
				repbyuser.indepentReputation.remove(0);
			}
			repbyuser.save();
		}
		this.save();
	}

	/**
	 * Adds the reputation to the user
	 * 
	 * @param repbyuser
	 *            total reputation of an specific user
	 */
	private void handleReputation(ReputationfromUser repbyuser) {
		this.reputation += repbyuser.indepentReputation.get(0).reputation;
		repbyuser.reputation += repbyuser.indepentReputation.get(0).reputation;
		createReputationPoint();
		this.save();
	}

	/**
	 * Votes down an post.
	 * 
	 * @param post
	 *            the voted post
	 */
	public void voteDown(Post post, User user) {

		if (post instanceof Answer) {
			addToBadge((Answer) post, VOTE_DOWN_REP);
			this.reputation += VOTE_DOWN_REP;
			createReputationPoint();
		} else {
			this.reputation += VOTE_DOWN_REP;
			createReputationPoint();
		}
		this.save();
	}

	/**
	 * Reputation for the best answer form a question.
	 * 
	 * @param post
	 *            the best answer
	 */
	public void bestAnswer(Answer answer, User user) {
		boolean hasvoted = this.hasVoted(user);

		if (!this.hasVoted(user)) {
			this.createReputationByUser(user);
			this.save();
		}

		ReputationfromUser repbyuser = this.findReputationfromUser(user);
		repbyuser.createPostReputation(answer, BEST_ANSWER_REP);

		while (this.isNotOverPushSize(repbyuser, hasvoted)
				&& repbyuser.indepentReputation.size() > 0) {
			handleReputation(repbyuser);
			if (repbyuser.indepentReputation.get(0).post instanceof Answer) {
				addToBadge((Answer) repbyuser.indepentReputation.get(0).post,
						repbyuser.indepentReputation.get(0).reputation);
			}
			repbyuser.indepentReputation.remove(0);
			repbyuser.save();
			this.save();
		}
		this.save();
	}

	/**
	 * Penalty for vote down.
	 */
	public void penalty() {
		this.reputation += PENALTY;
		createReputationPoint();
	}

	/**
	 * Checks whether the "anti reputation push convetions (arpc)" are met.
	 * 
	 * @param repbyuser
	 *            the reputation of a single user.
	 * @return true if the conventions are met.
	 */
	private boolean isNotOverPushSize(ReputationfromUser repbyuser,
			boolean hasvoted) {

		if (this.reputation < START_CHECK_ARPC
				|| ((double) (repbyuser.reputation))
						/ ((double) this.reputation) < MAX_PUSH_SIZE
				|| !hasvoted) {
			return true;
		}
		return false;
	}

	/**
	 * Creates a new reputation for a user
	 * 
	 * @param user
	 *            which has voted.
	 */
	private void createReputationByUser(User user) {
		ReputationfromUser repbyuser = new ReputationfromUser(user).save();
		this.reputationfromUser.add(repbyuser);
		this.save();
	}

	/**
	 * Adds the reward form a vote to every related badge.
	 * 
	 * @param post
	 *            the post of the assessed user
	 * @param reward
	 *            of the vote
	 */
	public void addToBadge(Answer answer, int reward) {

		for (Tag tag : answer.question.tags) {

			Badge badge = Badge.find("byTagAndReputation", tag, this).first();

			if (badge == null) {
				badge = new Badge(tag, this).save();
				badge.addReputation(reward);
				badge.save();
				this.badges.add(badge);
				this.save();
			} else {
				badge.addReputation(reward);
				badge.save();
			}
		}
	}

	/**
	 * Checks if an user has already voted for this user.
	 * 
	 * @param user
	 *            which has voted
	 * @return true if user has voted this user.
	 */
	public boolean hasVoted(User user) {
		for (ReputationfromUser repbyuser : this.reputationfromUser) {
			if (repbyuser.user.equals(user)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the reputation which a specific user has given this user.
	 * 
	 * @param the
	 *            user which has given the reputation
	 * @return the Reputation for this user from the given user
	 */
	public ReputationfromUser findReputationfromUser(User user) {
		return ReputationfromUser.find("byUser", user).first();
	}

	public String toString() {
		return Integer.toString(reputation);
	}
}
