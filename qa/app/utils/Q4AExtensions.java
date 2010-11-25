package utils;

import models.Post;
import models.User;
import play.templates.JavaExtensions;
import controllers.Secure;

public class Q4AExtensions extends JavaExtensions {

	public static boolean isOwnPost(Post post) {
		if (post != null && Secure.Security.isConnected()) {
			User connectedUser = User.find("byUsername",
					Secure.Security.connected()).first();
			if (connectedUser != null) {
				return connectedUser.equals(post.author);
			}

		}

		return false;
	}
}
