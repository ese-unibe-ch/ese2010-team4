package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class History extends Model {

	@ManyToOne
	public Post post;
	public String title;
	public String content;

	public History(Post post, String title, String content) {

		this.post = post;
		this.title = title;
		this.content = content;

	}

}
