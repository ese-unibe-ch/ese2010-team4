import models.helper.ValidationHelper;

import org.junit.Test;

import play.test.UnitTest;

public class HelperTests extends UnitTest {
	public ValidationHelper helper = new ValidationHelper();

	@Test
	public void usernameCheck() {
		// Bob is in the StartUp XML ^^
		String newusername = "Bob";
		assertFalse(helper.ckeck(newusername, "Username"));
		newusername = "D3orn";
		assertTrue(helper.ckeck(newusername, "Username"));
	}

	@Test
	public void emailCheck() {
		String email = "dd.ch";
		assertFalse(helper.ckeck(email, "Email"));
		email = "dominique.rahm@gmx.ch";
		assertTrue(helper.ckeck(email, "Email"));
	}
	/*
	 * (password, password2); (password.length() >= 6);
	 */
}