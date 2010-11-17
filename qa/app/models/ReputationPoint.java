package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class ReputationPoint extends Model {
	public static int counter = 0;
	public int timestamp;
	public int repvalue;

	public ReputationPoint(int repvalue) {
		this.repvalue = repvalue;
		this.timestamp = counter;
		this.counter++;
	}

	public ReputationPoint(int i, int timestamp2) {
		this.repvalue = i;
		this.timestamp = timestamp2;
	}

	public String toString() {
		return "[" + timestamp + ",	" + repvalue + "]";
	}
}