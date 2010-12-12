import java.io.IOException;

import models.Answer;
import models.Comment;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ReputationTest extends UnitTest {

	User bob;
	User jeff;
	Question firstQuestion;
	Answer firstAnswer;
	Answer secondAnswer;

	@Before
	public void setup() throws Exception {
		Fixtures.deleteAll();
		User.createUser("Bob", "bob@gmail.com", "keyword","");
		User.createUser("Jeff", "jeff@jeff.ch", "hallo","");
		bob = User.find("byUsername", "Bob").first();
		jeff = User.find("byUsername", "Jeff").first();
		firstQuestion = new Question(bob, "brightliy?",
		"What is hot and shines brightly?").save();
		firstAnswer = new Answer(firstQuestion, jeff, "It is the sun.").save();
		secondAnswer = new Answer(firstQuestion, bob, "blabla").save();
;

	}

	/**
	 * public void shouldHaveDefaultRepValues() {
	 * 
	 * assertEquals(-1, bob.rating.repVal.penalty); assertEquals(-2,
	 * bob.rating.repVal.voteDown); assertEquals(5,
	 * bob.rating.repVal.voteUPQuestion); assertEquals(10,
	 * bob.rating.repVal.voteUPAnswer); assertEquals(50,
	 * bob.rating.repVal.bestAnswer);
	 * 
	 * assertEquals(-1, jeff.rating.repVal.penalty); assertEquals(-2,
	 * jeff.rating.repVal.voteDown); assertEquals(5,
	 * jeff.rating.repVal.voteUPQuestion); assertEquals(10,
	 * jeff.rating.repVal.voteUPAnswer); assertEquals(50,
	 * jeff.rating.repVal.bestAnswer); }
	 **/

	@Test
	public void shouldHaveDefaultReputation() {

		assertEquals(0, bob.rating.bestAnswerRep);
		assertEquals(0, bob.rating.answerRep);
		assertEquals(0, bob.rating.questionRep);
		assertEquals(0, bob.rating.penalty);
		assertEquals(0, bob.rating.totalRep);

		assertEquals(0, jeff.rating.bestAnswerRep);
		assertEquals(0, jeff.rating.answerRep);
		assertEquals(0, jeff.rating.questionRep);
		assertEquals(0, jeff.rating.penalty);
		assertEquals(0, jeff.rating.totalRep);
	}

	@Test
	public void shouldIncreaseAnswerRep() {

		firstAnswer.vote(bob, true);
		firstAnswer.save();

		assertEquals(10, jeff.rating.answerRep);
		assertEquals(10, jeff.rating.totalRep);

	}

	@Test
	public void shouldIncreaseQuestionRep() {

		firstQuestion.vote(jeff, true);
		firstQuestion.save();

		assertEquals(5, bob.rating.questionRep);
		assertEquals(5, bob.rating.totalRep);
	}

	@Test
	public void shouldDecreaseRatingBobJeff() {

		firstAnswer.vote(bob, false);
		firstAnswer.save();

		assertEquals(-2, jeff.rating.answerRep);
		assertEquals(-1, bob.rating.penalty);
	}

	@Test
	public void shouldBeBestAnswer() {

		firstAnswer.isBestAnswer = true;
		firstAnswer.save();
		firstQuestion.setValidity(0);
		firstQuestion.save();
		bob.hasTimeToChange(firstQuestion.id);
		assertEquals(50, jeff.rating.bestAnswerRep);
		assertEquals(50, jeff.rating.totalRep);

	}

	@Test
	public void shouldNotBeBestAnswer() {

		firstQuestion.setValidity(0);
		firstQuestion.save();
		bob.hasTimeToChange(firstQuestion.id);
		assertEquals(0, jeff.rating.bestAnswerRep);
		assertEquals(0, jeff.rating.totalRep);
	}

	@Test
	public void shouldIcreaseReputation() {

	
		firstQuestion.vote(jeff, true);
		secondAnswer.vote(jeff, true);
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.setValidity(0);
		bob.hasTimeToChange(firstQuestion.id);

		assertEquals(65, bob.rating.totalRep);

	}

	@Test
	public void shouldNoTBeEmpty() {

		
		firstQuestion.vote(jeff, true);
		secondAnswer.vote(jeff, true);
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.setValidity(0);
		bob.hasTimeToChange(firstQuestion.id);

		assertEquals(4, bob.rating.totalRepPoint.size());
		assertEquals(0, bob.rating.totalRepPoint.get(0).repvalue);
		assertEquals(5, bob.rating.totalRepPoint.get(1).repvalue);
		assertEquals(65, bob.rating.totalRepPoint.get(3).repvalue);
	}

	@Test
	public void shouldRetrunTheRightString() throws IOException {

	
		firstQuestion.vote(jeff, true);
		secondAnswer.vote(jeff, true);
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.setValidity(0);
		bob.hasTimeToChange(firstQuestion.id);

	}

}
