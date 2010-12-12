package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class ReputationPoint extends Model {
	
	public long timestamp;
	public int repvalue;

	public ReputationPoint(int repvalue) {
		this.repvalue = repvalue;
		this.timestamp = new Date().getTime();
	}

	// bla
	public ReputationPoint(int i, long timestamp2) {
		this.repvalue = i;
		this.timestamp = timestamp2;
	}
}