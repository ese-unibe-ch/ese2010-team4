package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/**
 * The Class Badge. For every tag the user can get a badge. The logic is maintained in this class.
 */
@Entity
public class Badge extends Model implements Comparable<Badge>{
	
	public Tag tag;
	public String kind;
	public boolean bronze;
	public boolean silver;
	public boolean gold;
	public int rating;
	
	@ManyToOne
	public Reputation reputation;
	
	public Badge(Tag tag, Reputation reputation){
		this.tag = tag;
		this.reputation = reputation;
		this.bronze = false;
		this.silver = false;
		this.gold = false;
		this.rating = 0;
		this.kind = "";
	}

	/**
	 * Increased the rating of the given Badge
	 *
	 * @param reward form the vote
	 */
	public void addReputation(int reward){
		this.rating += reward;		
		if(rating> 20){
			bronze = true;
			silver = false;
			kind = ("bronze");
			System.out.println("Art des Badges: " + kind);
		}
		
		if(rating>200){
			silver = true;
			gold = false;
			kind = "silver";
		}
		
		if(rating>2000){
			gold = true;
			kind = "gold";
		}
		
	}
	
	public int compareTo(Badge o) {
		return this.tag.name.compareTo(o.tag.name);
	}
	
	public String toString(){
			return tag.name;
	}

}
