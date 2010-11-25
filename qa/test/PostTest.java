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
	public void setup() throws Exception {
		Fixtures.deleteAll();
		hans = new User("Muster Hans", "hans@gmail.com", "keyword").save();
		sepp = new User("Sepp", "sepp@sepp.ch", "hallo").save();
		firstQuestion = new Question(hans, "brightliy?",
				"What is hot and shines brightly?").save();
		firstAnswer = new Answer(firstQuestion, hans, "It is the sun.").save();
	}

	@Test
	public void AddUserToLikers() {
		assertTrue(firstAnswer.likers.isEmpty());
		firstAnswer.addLiker(hans);
		assertEquals(hans, firstAnswer.likers.toArray()[0]);
	}

	@Test
	public void RemoveUserFromLikers() {
		firstAnswer.addLiker(hans);
		assertEquals(1, firstAnswer.likers.size());
		firstAnswer.removeLiker(hans);
		assertTrue(firstAnswer.likers.isEmpty());
	}

	@Test
	public void ClearLikersList() {
		firstAnswer.addLiker(hans);
		firstAnswer.addLiker(sepp);
		assertTrue(firstAnswer.likers.size() > 0);
		firstAnswer.likers.clear();
		assertTrue(firstAnswer.likers.isEmpty());
	}

	@Test
	public void CountLikersInComments() {
		firstComment = new Comment(hans, firstAnswer, "hallo");
		assertEquals(0, firstComment.numberOfLikers());
		firstComment.addLiker(hans);
		assertEquals(1, firstComment.numberOfLikers());
	}

	@Test
	public void CountLikersInAnswers() {
		assertEquals(0, firstAnswer.numberOfLikers());
		firstAnswer.addLiker(hans);
		assertEquals(1, firstAnswer.numberOfLikers());
	}

	@Test
	public void CountLikersInQuestions() {
		assertEquals(0, firstQuestion.numberOfLikers());
		firstQuestion.addLiker(hans);
		assertEquals(1, firstQuestion.numberOfLikers());
	}

}
