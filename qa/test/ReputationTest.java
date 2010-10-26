import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ReputationTest extends UnitTest {

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
	public void shouldHaveDefaultRepValues() {

		assertEquals(-1, hans.rating.repVal.penalty);
		assertEquals(-2, hans.rating.repVal.voteDown);
		assertEquals(5, hans.rating.repVal.voteUPQuestion);
		assertEquals(10, hans.rating.repVal.voteUPAnswer);
		assertEquals(50, hans.rating.repVal.bestAnswer);

		assertEquals(-1, sepp.rating.repVal.penalty);
		assertEquals(-2, sepp.rating.repVal.voteDown);
		assertEquals(5, sepp.rating.repVal.voteUPQuestion);
		assertEquals(10, sepp.rating.repVal.voteUPAnswer);
		assertEquals(50, sepp.rating.repVal.bestAnswer);
	}

	@Test
	public void shouldHaveDefaultReputation() {

		assertEquals(0, hans.rating.bestAnswerRep);
		assertEquals(0, hans.rating.answerRep);
		assertEquals(0, hans.rating.questionRep);
		assertEquals(0, hans.rating.penalty);
		assertEquals(0, hans.rating.totalRep);

		assertEquals(0, sepp.rating.bestAnswerRep);
		assertEquals(0, sepp.rating.answerRep);
		assertEquals(0, sepp.rating.questionRep);
		assertEquals(0, sepp.rating.penalty);
		assertEquals(0, sepp.rating.totalRep);
	}

	@Test
	public void shouldIncreaseAnswerRep() {

	}

	@Test
	public void shouldIncreaseQuestionRep() {

	}

}
