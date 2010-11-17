package models;

import java.util.Comparator;

public class PostActivityComperator implements Comparator<Post> {

	public int compare(Post x, Post y) {
		if (x.timestamp.after(y.timestamp)) {
			return -1;
		} else if (x.timestamp.before(y.timestamp)) {
			return 1;
		}
		return 0;
	}

}
