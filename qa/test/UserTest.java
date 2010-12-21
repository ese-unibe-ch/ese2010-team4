import java.util.Date;

import models.Answer;
import models.Comment;
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
		User.createUser("Muster Hans", "hans@gmail.com", "keyword", "");
		User.createUser("Sepp", "sepp@sepp.ch", "hallo", "");
		hans = User.find("byUsername", "Muster Hans").first();
		sepp = User.find("byUsername", "Sepp").first();
		firstQuestion = new Question(hans, "brightliy?",
				"What is hot and shines brightly?").save();
		firstAnswer = new Answer(firstQuestion, hans, "It is the sun.").save();
		firstAnswer.tagItWith("Java").tagItWith("Html").save();

	}

	// @Test
	public void deleteUser() {
		hans.delete();
		hans.save();
		assertEquals(null, hans);
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
	public void shouldBeAbleToVote() {
		assertTrue(sepp.isAbleToVote(firstQuestion.id));
	}

	@Test
	public void shouldBeNotAbleToVote() {
		assertFalse(hans.isAbleToVote(firstQuestion.id));
	}

	@Test
	public void hansIsAbleToChoose() {
		assertEquals(true, hans.isAbleToChoose(firstQuestion.id));
	}

	@Test
	public void seppIsNotAbleToChoose() {
		assertEquals(false, sepp.isAbleToChoose(firstQuestion.id));
	}

	@Test
	public void hansNotAlreadyLikesComment() {
		new Comment(hans, firstAnswer, "this is a comment").save();
		Comment comment = firstAnswer.comments.get(0);

		assertEquals(false, hans.alreadyLikesComment(comment.id));
	}

	@Test
	public void hansLikesComment() {
		new Comment(hans, firstAnswer, "this is a comment").save();
		Comment comment = firstAnswer.comments.get(0);
		comment.addLiker(hans);
		assertEquals(true, hans.alreadyLikesComment(comment.id));
	}

	@Test
	public void calculateAge() {
		hans.setBirthday("03-03-1990");
		assertEquals(hans.calculateAge(), 20);
		assertEquals("03-03-1990", hans.getBirthday());
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

	@Test
	public void canPost() {
		new Answer(firstQuestion, hans, "hallo").save();
		assertFalse(hans.canPost());
		hans.setUpPostTime(-40000);
		assertTrue(hans.canPost());
	}

	@Test
	public void IsFollowingAndUnfollowHansAndFirstQuestion() {
		sepp.followU.add(hans);
		sepp.followQ.add(firstQuestion);
		sepp.save();
		assertEquals(true, sepp.isFollowing(hans));
		assertEquals(true, sepp.isFollowing(firstQuestion));
	}

	@Test
	public void FollowAndUnfollowHansAndFirstQuestion() {
		sepp.followU.add(hans);
		sepp.followQ.add(firstQuestion);
		sepp.save();
		assertEquals(1, sepp.followQ.size());
		assertEquals(1, sepp.followU.size());
		sepp.deleteFollowQ(firstQuestion);
		assertEquals(0, sepp.followQ.size());
		sepp.deleteFollowU(hans);
		assertEquals(0, sepp.followU.size());
	}

	@Test
	public void graphDataForHansNotNull() {
		firstAnswer.vote(sepp, true);
		firstAnswer.vote(sepp, false);
		firstAnswer.save();
		assertNotNull(hans.graphData());
	}

	@Test
	public void unSpamHans() {
		hans.spamreport.add(firstAnswer);
		hans.spamreport.add(firstQuestion);
		hans.isSpam = true;
		hans.save();
		assertEquals(true, hans.isSpam());
		assertEquals(2, hans.spamreport.size());
		hans.unspamUser();
		hans.save();
		assertEquals(false, hans.isSpam);
		assertEquals(0, hans.spamreport.size());
	}

	@Test
	public void HanstimeToNextSearchAndPost() {

		hans.searchdate = new Date().getTime() + 10000;
		hans.postdate = new Date().getTime() + 10000;
		assertEquals(10, hans.timeToNextSearch());
		assertEquals(10, hans.timeToNextPost());
	}

	@Test
	public void deleteHansReputation() {
		firstQuestion.vote(hans, true);
		firstQuestion.save();
		assertEquals(5, hans.rating.reputation);
		hans.clearWholeReputation();
		assertEquals(0, hans.rating.reputation);
	}

	@Test
	public void findAllSimilairQuestionsForHans() {
		Question secondQuestion = new Question(sepp, "bla", "blablabla").save();
		Question thirdQuestion = new Question(sepp, "bla2", "blablabla2")
				.save();
		firstQuestion.tagItWith("Linux").tagItWith("Hallo").save();
		secondQuestion.tagItWith("Linux").tagItWith("Hallo").save();
		thirdQuestion.tagItWith("Linux").tagItWith("Hallo").save();
		hans.badgetags.addAll(firstQuestion.tags);

		assertEquals(2, hans.getSimilairQuestions(2).size());
		secondQuestion.addAnswer(hans, "bliblablo").save();
		assertEquals(1, hans.getSimilairQuestions(2).size());
	}
}
