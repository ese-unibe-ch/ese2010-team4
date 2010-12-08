import models.Answer;
import models.Comment;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class PostTest extends UnitTest {

	User hans;
	User sepp;
	Question firstQuestion;
	Answer firstAnswer;
	Comment firstComment;

	@Before
	public void setup() {
		Fixtures.deleteAll();
		User.createUser("Muster Hans", "hans@gmail.com", "keyword","");
		User.createUser("Sepp", "sepp@sepp.ch", "hallo","");
		hans = User.find("byUsername", "Muster Hans").first();
		sepp = User.find("byUsername", "Sepp").first();
		firstQuestion = new Question(hans, "brightliy?",
				"What is hot and shines brightly?").save();
		firstAnswer = new Answer(firstQuestion, hans, "It is the sun.").save();
		firstComment = new Comment(hans, firstQuestion, "bla").save();
	}

	@Test
	public void AddUserToLikers() {
		assertTrue(firstComment.likers.isEmpty());
		firstComment.addLiker(hans);
		assertEquals(hans, firstComment.likers.toArray()[0]);
	}

	@Test
	public void RemoveUserFromLikers() {
		firstComment.addLiker(hans);
		assertEquals(1, firstComment.likers.size());
		firstComment.removeLiker(hans);
		assertTrue(firstComment.likers.isEmpty());
	}

	@Test
	public void ClearLikersList() {
		firstComment.addLiker(hans);
		firstComment.addLiker(sepp);
		assertTrue(firstComment.likers.size() > 0);
		firstComment.likers.clear();
		assertTrue(firstComment.likers.isEmpty());
	}

	@Test
	public void CountLikersInComments() {
		firstComment = new Comment(hans, firstAnswer, "hallo");
		assertEquals(0, firstComment.numberOfLikers());
		firstComment.addLiker(hans);
		assertEquals(1, firstComment.numberOfLikers());
	}

	@Test
	public void CountLikers() {
		assertEquals(0, firstComment.numberOfLikers());
		firstComment.addLiker(hans);
		assertEquals(1, firstComment.numberOfLikers());
	}

	@Test
	public void isSpam() {
		firstQuestion.spam(hans);

		assertFalse(firstQuestion.isSpam());
		assertEquals(1, firstQuestion.spamreport.size());

		firstQuestion.spam(sepp);
		assertTrue(firstQuestion.isSpam());
		assertEquals(2, firstQuestion.spamreport.size());
	}

}
