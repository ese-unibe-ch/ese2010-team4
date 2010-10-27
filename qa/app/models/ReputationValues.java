package models;

import play.db.jpa.Model;

/**
 * 
 * Singelton Class for Reputation values
 * 
 */

public class ReputationValues extends Model {

	// Reputation Values
	private static ReputationValues instance;
	public static int voteUPAnswer;
	public static int voteUPQuestion;
	public static int voteDown;
	public static int penalty;
	public static int bestAnswer;

	private ReputationValues() {

		this.voteUPAnswer = 10;
		this.voteUPQuestion = 5;
		this.voteDown = -2;
		this.penalty = -1;
		this.bestAnswer = 50;
	}

	/**
	 * Create, if possible a new Reputation instance at with standard reputation
	 * values
	 * 
	 * 
	 * @param voteUP
	 *            reputation value
	 * @param voteDown
	 *            reputation value
	 * @param penalty
	 *            for vote down
	 * @param bestAnswer
	 *            reputation value
	 * @return new reputation instance
	 */
	public static synchronized ReputationValues getInstance() {
		if (ReputationValues.instance == null) {
			ReputationValues.instance = new ReputationValues();

		}
		return ReputationValues.instance;
	}
}
