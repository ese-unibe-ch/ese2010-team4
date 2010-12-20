import models.Answer;
import models.Question;
import models.ReputationPoint;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ReputationTest extends UnitTest {

	User bob;
	User jeff;
	Question firstQuestion;
	Question secondQuestion;
	Question thirdQuestion;
	Question fourthQuestion;
	Answer firstAnswer;
	Answer secondAnswer;
	Answer thirdAnswer;
	Answer fourthAnswer;

	@Before
	public void setup() throws Exception {
		Fixtures.deleteAll();
		User.createUser("Bob", "bob@gmail.com", "keyword", "");
		User.createUser("Jeff", "jeff@jeff.ch", "hallo", "");
		bob = User.find("byUsername", "Bob").first();
		jeff = User.find("byUsername", "Jeff").first();
		firstQuestion = new Question(jeff, "brightliy?",
				"What is hot and shines brightly?").save();
		secondQuestion = new Question(jeff, "yeah?", "it's because it is good")
				.save();
		thirdQuestion = new Question(bob, "blubediblupp?",
				"it's because it is good").save();
		fourthQuestion = new Question(bob, "blub?", "it's because it is good")
				.save();
		firstAnswer = new Answer(secondQuestion, jeff, "It is the sun.").save();
		secondAnswer = new Answer(firstQuestion, jeff, "blabla").save();
		thirdAnswer = new Answer(thirdQuestion, bob, "blubediblup").save();
		fourthAnswer = new Answer(fourthQuestion, bob, "schwupedidup").save();

	}

	@Test
	public void shouldHaveDefaultReputation() {

		assertEquals(0, bob.rating.reputation);
		assertEquals(0, jeff.rating.reputation);
	}

	@Test
	public void shouldIncreaseAnswerReputation() {

		firstAnswer.vote(bob, true);
		firstAnswer.save();

		assertEquals(10, jeff.rating.reputation);
	}

	@Test
	public void shouldIncreaseQuestionRep() {

		firstQuestion.vote(jeff, true);
		firstQuestion.save();

		assertEquals(5, jeff.rating.reputation);
	}

	@Test
	public void shouldDecreaseRatingBobJeff() {

		firstAnswer.vote(bob, true);
		thirdAnswer.vote(jeff, true);
		firstAnswer.vote(bob, false);
		firstAnswer.save();

		assertEquals(8, jeff.rating.reputation);
		assertEquals(9, bob.rating.reputation);
	}

	@Test
	public void shouldBeBestAnswer() {

		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.addValidity(0);
		jeff.hasTimeToChange(firstQuestion.id);

		assertEquals(50, jeff.rating.reputation);
	}

	@Test
	public void shouldNotBeBestAnswer() {

		firstQuestion.addValidity(0);
		firstQuestion.save();
		bob.hasTimeToChange(firstQuestion.id);
		assertEquals(0, jeff.rating.reputation);
	}

	@Test
	public void shouldIcreaseReputation() {

		firstQuestion.vote(jeff, true);
		secondAnswer.vote(jeff, true);
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.addValidity(0);
		bob.hasTimeToChange(firstQuestion.id);

		assertEquals(65, jeff.rating.reputation);

	}

	@Test
	public void shouldNoTBeEmpty() {

		firstQuestion.vote(jeff, true);
		secondAnswer.vote(jeff, true);
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.addValidity(0);
		bob.hasTimeToChange(firstQuestion.id);

		assertEquals(4, jeff.rating.reputationPoints.size());
		assertEquals(0, jeff.rating.reputationPoints.get(0).repvalue);
		assertEquals(5, jeff.rating.reputationPoints.get(1).repvalue);
		assertEquals(65, jeff.rating.reputationPoints.get(3).repvalue);
	}

	@Test
	public void shouldRetrunTheRightString() {

		firstQuestion.vote(jeff, true);
		secondAnswer.vote(jeff, true);
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.addValidity(0);
		jeff.hasTimeToChange(firstQuestion.id);

		assertEquals(false, jeff.hasTimeToChange(firstQuestion.id));

	}

	@Test
	public void shouldNotVoteUPUserReputation() {

		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.addValidity(0);
		jeff.hasTimeToChange(firstQuestion.id);
		firstQuestion.save();

		assertEquals(50, jeff.rating.reputation);

		firstAnswer.isBestAnswer = true;
		firstAnswer.save();
		secondQuestion.addValidity(0);
		jeff.hasTimeToChange(secondQuestion.id);
		secondQuestion.save();

		assertEquals(100, jeff.rating.reputation);

	}

	@Test
	public void shouldNotVoteOverASPC() {
		thirdAnswer.isBestAnswer = true;
		thirdAnswer.save();
		thirdQuestion.addValidity(0);
		jeff.hasTimeToChange(thirdQuestion.id);
		thirdQuestion.save();

		assertEquals(50, bob.rating.reputation);

		fourthAnswer.isBestAnswer = true;
		fourthAnswer.save();
		fourthQuestion.addValidity(0);
		jeff.hasTimeToChange(fourthQuestion.id);
		fourthQuestion.save();

		assertEquals(100, bob.rating.reputation);

		thirdAnswer.vote(jeff, true);
		assertEquals(110, bob.rating.reputation);
		fourthAnswer.vote(jeff, true);
		assertEquals(120, bob.rating.reputation);
		thirdQuestion.vote(jeff, true);
		assertEquals(125, bob.rating.reputation);
		thirdAnswer.vote(jeff, true);
		assertEquals(125, bob.rating.reputation);
	}

	@Test
	public void shouldSaveAndAddTheVotes() {
		thirdAnswer.isBestAnswer = true;
		thirdAnswer.save();
		thirdQuestion.addValidity(0);
		jeff.hasTimeToChange(thirdQuestion.id);
		thirdQuestion.save();

		assertEquals(50, bob.rating.reputation);

		fourthAnswer.isBestAnswer = true;
		fourthAnswer.save();
		fourthQuestion.addValidity(0);
		jeff.hasTimeToChange(fourthQuestion.id);
		fourthQuestion.save();

		assertEquals(100, bob.rating.reputation);

		thirdAnswer.vote(jeff, true);
		assertEquals(110, bob.rating.reputation);
		fourthAnswer.vote(jeff, true);
		assertEquals(120, bob.rating.reputation);
		thirdQuestion.vote(jeff, true);
		assertEquals(125, bob.rating.reputation);
		thirdAnswer.vote(jeff, true);
		assertEquals(125, bob.rating.reputation);
		thirdAnswer.vote(jeff, true);
		assertEquals(125, bob.rating.reputation);

		bob.rating.reputation = 10000;
		bob.rating.save();
		bob.save();
		thirdAnswer.vote(jeff, true);
		assertEquals(10030, bob.rating.reputation);

	}

	@Test
	public void createNewReputationPoint() {
		ReputationPoint point = new ReputationPoint(10, 100000);
		assertEquals(10, point.repvalue);
		assertEquals(100000, point.timestamp);
	}

}
