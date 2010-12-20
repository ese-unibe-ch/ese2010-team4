package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/**
 * The Class Badge. For every tag the user can get a badge. The Badge logic is
 * maintained in this class.
 */
@Entity
public class Badge extends Model {

	public static final int BRONZE = 50;
	public static final int SILVER = 200;
	public static final int GOLD = 2000;
	public Tag tag;
	public String kind;
	public boolean bronze;
	public boolean silver;
	public boolean gold;
	public int rating;

	@ManyToOne
	public Reputation reputation;

	public Badge(Tag tag, Reputation reputation) {
		this.tag = tag;
		this.reputation = reputation;
		this.bronze = false;
		this.silver = false;
		this.gold = false;
		this.rating = 0;
		this.kind = "";
	}

	/**
	 * Adds voted Tags to the specific user.
	 */
	private void addTagToUser() {
		User user = User.find("byRating", this.reputation).first();
		user.badgetags.add(this.tag);
		user.save();
	}

	/**
	 * Increased the rating of the given Badge, and notify the user of the new
	 * Badge.
	 * 
	 * @param reward
	 *            form the vote
	 */
	public void addReputation(int reward) {
		this.rating += reward;
		if (rating > BRONZE) {
			bronze = true;
			silver = false;
			kind = ("bronze");
			addTagToUser();
		}

		if (rating > SILVER) {
			silver = true;
			gold = false;
			kind = "silver";
		}

		if (rating > GOLD) {
			gold = true;
			kind = "gold";
		}

	}

	public String toString() {
		return tag.name;
	}

}
