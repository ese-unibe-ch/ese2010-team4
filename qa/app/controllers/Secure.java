package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import models.User;
import play.Play;
import play.data.validation.Required;
import play.i18n.Lang;
import play.libs.Crypto;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.utils.Java;

public class Secure extends Controller {

	@Before(unless = { "login", "authenticate", "logout" })
	static void checkAccess() throws Throwable {
		// Authent
		if (!session.contains("username")) {
			flash.put("url", request.method == "GET" ? request.url : "/");
			login("");
		}
		// Checks
		Check check = getActionAnnotation(Check.class);
		if (check != null) {
			check(check);
		}
		check = getControllerInheritedAnnotation(Check.class);
		if (check != null) {
			check(check);
		}
	}

	private static void check(Check check) throws Throwable {
		for (String profile : check.value()) {
			boolean hasProfile = (Boolean) Security.invoke("check", profile);
			if (!hasProfile) {
				Security.invoke("onCheckFailed", profile);
			}
		}
	}

	// ~~~ Login
	public static void login(String message) throws Throwable {
		Http.Cookie remember = request.cookies.get("rememberme");
		if (remember != null && remember.value.indexOf("-") > 0) {
			String sign = remember.value.substring(0, remember.value
					.indexOf("-"));
			String username = remember.value.substring(remember.value
					.indexOf("-") + 1);
			if (Crypto.sign(username).equals(sign)) {
				session.put("username", username);
				changeLanguage();
				redirectToOriginalURL();
			}
		}
		flash.keep("url");
		redirectToOriginalURL();
	}

	private static void changeLanguage() {
		User user = User.find("byUsername", Secure.Security.connected())
				.first();
		Lang.change(user.language);
	}

	public static void authenticate(@Required String username, String password,
			boolean remember) throws Throwable {
		// Check tokens
		Boolean allowed = false;
		try {
			// This is the deprecated method name
			allowed = (Boolean) Security.invoke("authentify", username,
					password);
		} catch (UnsupportedOperationException e) {
			// This is the official method name
			allowed = (Boolean) Security.invoke("authenticate", username,
					password);
		}
		if (validation.hasErrors() || !allowed) {
			flash.keep("url");
			flash.error("secure.error");
			params.flash();
			login("");
		}
		// Mark user as connected
		session.put("username", username);
		flash.success("secure.login");
		// Remember if needed
		if (remember) {
			response.setCookie("rememberme", Crypto.sign(username) + "-"
					+ username, "30d");
		}
		// Redirect to the original URL (or /)
		redirectToOriginalURL();
	}

	public static void logout() throws Throwable {
		User user = User.find("byUsername", Security.connected()).first(); // SS
		user.lastLogOff = new Date(System.currentTimeMillis()); // SS
		user.save(); // SS
		session.clear();
		response.removeCookie("rememberme");
		Security.invoke("onDisconnected");
		flash.success("secure.logout");
		login("");
	}

	// ~~~ Utils
	static void redirectToOriginalURL() throws Throwable {
		Security.invoke("onAuthenticated");
		String url = flash.get("url");
		flash.keep();
		if (url == null) {
			url = "/";
		}
		redirect(url);
	}

	public static class Security extends Controller {

		/**
		 * @Deprecated
		 * 
		 * @param username
		 * @param password
		 * @return
		 */
		static boolean authentify(String username, String password) {
			throw new UnsupportedOperationException();
		}

		/**
		 * This method is called during the authentication process. This is
		 * where you check if the user is allowed to log in into the system.
		 * This is the actual authentication process against a third party
		 * system (most of the time a DB).
		 * 
		 * @param username
		 * @param password
		 * @return true if the authentication process succeeded
		 */
		static boolean authenticate(String username, String password) {
			return User.login(username, password) != null;
		}

		/**
		 * This method checks that a profile is allowed to view this
		 * page/method. This method is called prior to the method's controller
		 * annotated with the @Check method.
		 * 
		 * @param profile
		 * @return true if you are allowed to execute this controller method.
		 */
		static boolean check(String profile) {
			if ("user".equals(profile)) {
				return (User.find("byUsername", connected()).<User> first() != null);
			}
			if ("admin".equals(profile)) {
				return User.find("byUsername", connected()).<User> first().isAdmin;
			}
			return false;
		}

		/**
		 * This method returns the current connected username
		 * 
		 * @return
		 */
		public static String connected() {
			return session.get("username");
		}

		/**
		 * Indicate if a user is currently connected
		 * 
		 * @return true if the user is connected
		 */
		public static boolean isConnected() {
			return session.contains("username");
		}

		/**
		 * This method is called after a successful authentication. You need to
		 * override this method if you with to perform specific actions (eg.
		 * Record the time the user signed in)
		 */
		static void onAuthenticated() {
		}

		/**
		 * This method is called after a successful sign off. You need to
		 * override this method if you with to perform specific actions (eg.
		 * Record the time the user signed off)
		 */
		static void onDisconnected() {
			Application.index();
		}

		/**
		 * This method is called if a check does not succeed. By default it
		 * shows the not allowed page (the controller forbidden method).
		 * 
		 * @param profile
		 */
		static void onCheckFailed(String profile) {
			forbidden();
		}

		private static Object invoke(String m, Object... args) throws Throwable {
			Class security = null;
			List<Class> classes = Play.classloader
					.getAssignableClasses(Security.class);
			if (classes.size() == 0) {
				security = Security.class;
			} else {
				security = classes.get(0);
			}
			try {
				return Java.invokeStaticOrParent(security, m, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

	}

}