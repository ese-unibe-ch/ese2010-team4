package controllers;

import models.User;

/**
 * A controller for the security.
 * 
 * @author dwettstein
 * 
 */
public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		return User.login(username, password) != null;
	}

	static void onDisconnected() {
		Application.index();
	}

	static void onAuthenticated() {
		Users.index();
	}

	static boolean check(String profile) {
		if ("user".equals(profile)) {
			return (User.find("byEmail", connected()).<User> first() != null);
		}
		if ("admin".equals(profile)) {
			return User.find("byEmail", connected()).<User> first().isAdmin;
		}
		return false;
	}

}