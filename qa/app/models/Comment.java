package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Comment extends Post {

	@ManyToOne
	public Post post;

	public Comment(User author, Post post, String content) {
		super(author, content);
		this.post = post;
		post.addComment(this);
		author.addPost(this);
	}

	@Override
	public Post addHistory(Post post, String title, String content) {
		return null;
	}

	@Override
	public Post vote(User user, boolean result) {
		return null;
	}

}