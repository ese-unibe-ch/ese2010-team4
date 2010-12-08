package models.helper;

import java.util.Comparator;

import models.VotablePost;

public class PostActivityComperator implements Comparator<VotablePost> {

	public int compare(VotablePost x, VotablePost y) {
		if (x.timestamp.after(y.timestamp)) {
			return -1;
		} else if (x.timestamp.before(y.timestamp)) {
			return 1;
		}
		return 0;
	}

}
