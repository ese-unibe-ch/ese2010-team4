import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest {

	User hans;
	User sepp;
	Question firstQuestion;
	Answer firstAnswer;

	@Before
	public void setup() throws Exception {
		Fixtures.deleteAll();
		hans = new User("Muster Hans", "hans@gmail.com", "keyword").save();
		sepp = new User("Sepp", "sepp@sepp.ch", "hallo").save();
		firstQuestion = new Question(hans, "brightliy?",
				"What is hot and shines brightly?").save();
		firstAnswer = new Answer(firstQuestion, hans, "It is the sun.").save();

	}

	@Test
	public void shouldCreateAndRetrieveUser() {
		assertNotNull(hans);
		assertEquals("Muster Hans", hans.username);
	}

	@Test
	public void shouldTryLoginAsUser() {
		assertNotNull(User.login("Muster Hans", "keyword"));
		// Not the right password.
		assertNull(User.login("Muster Hans", "badKey"));
		// Not the right email.
		assertNull(User.login("Muster Hand", "keyword"));
	}

	public void shouldCreateANewUserWithCreateUserMethod() {

		User.createUser("Rüedu", "ruedi@ruedi.ch", "test", "test");
		User user = User.find("byEmail", "ruedi@ruedi.ch").first();

		assertEquals(2, User.count());
		assertEquals("Rüedu", user.username);

		// Login successfully
		assertNotNull(User.login("ruedi@ruedi.ch", "test"));

		// wrong pw
		assertNull(User.login("ruedi@ruedi.ch", "testing"));
		// name instead of email
		assertNull(User.login("Rüedu", "test"));

	}

	@Test
	public void shouldReturnTheRightCreateUserMessages() {

		String message = User.createUser("Ruedi", "ruedi@ruedi.ch", "testing",
				"testing");
		User user = User.find("byUsername", "Ruedi").first();

		assertEquals(message, "Hello, " + user.username + ", please log in");

		// not everything was filled
		message = User.createUser("Ruedi", "", "", "");
		assertEquals(message, "you forgot one or more gap's");

		// invalid email
		message = User.createUser("Ruedi", "rüedu@rüedu.ch", "test", "tets");
		assertEquals(message, "you entered a invalid email address");

		// wrong password
		message = User.createUser("Ruedi", "ruedi@ruedi.ch", "test", "tets");
		assertEquals(message, "the password's aren't the same");

		// to short password
		message = User.createUser("Ruedi", "ruedi@ruedi.ch", "test", "test");
		assertEquals(message, "your password must be 6 singns or longer");
	}

	@Test
	public void shouldBeAbleToVote() {
		assertTrue(sepp.isAbleToVote(firstQuestion.id));
	}

	@Test
	public void shouldBeNotAbleToVote() {
		assertFalse(hans.isAbleToVote(firstQuestion.id));
	}

	@Test
	public void calculateAge() {
		hans.setBirthday("03-03-1990");
		assertEquals(hans.calculateAge(), 20);
	}

	@Test
	public void addQuestion() {
		String title = "Why?";
		String content = "Because of wiki";
		Question question1 = hans.addQuestion(title, content);
		Question question2 = new Question(hans, title, content);
		assertEquals(question1.author, question2.author);
		assertEquals(question1.title, question2.title);
		assertEquals(question1.content, question2.content);
	}

}
