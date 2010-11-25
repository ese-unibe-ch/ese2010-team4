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
		assertTrue(firstAnswer.getLikers().isEmpty());
		firstAnswer.addLiker(hans);
		assertEquals(hans, firstAnswer.getLikers().get(0));
	}

	@Test
	public void RemoveUserFromLikers() {
		firstAnswer.addLiker(hans);
		assertEquals(1, firstAnswer.getLikers().size());
		firstAnswer.removeLiker(hans);
		assertTrue(firstAnswer.getLikers().isEmpty());
	}

	@Test
	public void ClearLikersList() {
		firstAnswer.addLiker(hans);
		firstAnswer.addLiker(sepp);
		assertTrue(firstAnswer.getLikers().size() > 0);
		firstAnswer.clearLikers();
		assertTrue(firstAnswer.getLikers().isEmpty());
	}

	@Test
	public void CountLikersInComments() {
		firstComment = new Comment(hans, firstAnswer, "hallo");
		assertEquals(0, firstComment.countLikers());
		firstComment.addLiker(hans);
		assertEquals(1, firstComment.countLikers());
	}

	@Test
	public void CountLikersInAnswers() {
		assertEquals(0, firstAnswer.countLikers());
		firstAnswer.addLiker(hans);
		assertEquals(1, firstAnswer.countLikers());
	}

	@Test
	public void CountLikersInQuestions() {
		assertEquals(0, firstQuestion.countLikers());
		firstQuestion.addLiker(hans);
		assertEquals(1, firstQuestion.countLikers());
	}

}
