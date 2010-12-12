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
		User.createUser("Bob", "bob@gmail.com", "keyword","");
		User.createUser("Jeff", "jeff@jeff.ch", "hallo","");
		bob = User.find("byUsername", "Bob").first();
		jeff = User.find("byUsername", "Jeff").first();
		firstQuestion = new Question(jeff, "brightliy?",
		"What is hot and shines brightly?").save();
		secondQuestion = new Question(jeff, "yeah?",
		"it's because it is good").save();
		thirdQuestion = new Question(bob, "blubediblupp?",
		"it's because it is good").save();
		fourthQuestion = new Question(bob, "blub?",
		"it's because it is good").save();
		firstAnswer = new Answer(secondQuestion, jeff, "It is the sun.").save();
		secondAnswer = new Answer(firstQuestion, jeff, "blabla").save();
		thirdAnswer = new Answer(thirdQuestion, bob, "blubediblup").save();
		fourthAnswer = new Answer(fourthQuestion, bob, "schwupedidup").save();

	}
	
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

		assertEquals(5, jeff.rating.questionRep);
		assertEquals(5, jeff.rating.totalRep);
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
		
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.addValidity(0);
		jeff.hasTimeToChange(firstQuestion.id);
		
		assertEquals(50, jeff.rating.bestAnswerRep);
		assertEquals(50, jeff.rating.totalRep);
	}

	@Test
	public void shouldNotBeBestAnswer() {

		firstQuestion.addValidity(0);
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
		firstQuestion.addValidity(0);
		bob.hasTimeToChange(firstQuestion.id);

		assertEquals(65, jeff.rating.totalRep);

	}

	@Test
	public void shouldNoTBeEmpty() {
		
		firstQuestion.vote(jeff, true);
		secondAnswer.vote(jeff, true);
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.addValidity(0);
		bob.hasTimeToChange(firstQuestion.id);

		assertEquals(4, jeff.rating.totalRepPoint.size());
		assertEquals(0, jeff.rating.totalRepPoint.get(0).repvalue);
		assertEquals(5, jeff.rating.totalRepPoint.get(1).repvalue);
		assertEquals(65, jeff.rating.totalRepPoint.get(3).repvalue);
	}

	@Test
	public void shouldRetrunTheRightString(){

	
		firstQuestion.vote(jeff, true);
		secondAnswer.vote(jeff, true);
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.addValidity(0);
		jeff.hasTimeToChange(firstQuestion.id);
		
		assertEquals(false, jeff.hasTimeToChange(firstQuestion.id));

	}
	
	@Test
	public void shouldNotVoteUPUserReputation(){
		
		secondAnswer.isBestAnswer = true;
		secondAnswer.save();
		firstQuestion.addValidity(0);	
		jeff.hasTimeToChange(firstQuestion.id);
		firstQuestion.save();
		
		assertEquals(50, jeff.rating.totalRep);
		
		firstAnswer.isBestAnswer = true;
		firstAnswer.save();
		secondQuestion.addValidity(0);	
		jeff.hasTimeToChange(secondQuestion.id);
		secondQuestion.save();
		
		assertEquals(100, jeff.rating.totalRep);
		
	}
	
	@Test
	public void shouldNotVoteOverASPC(){
		thirdAnswer.isBestAnswer = true;
		thirdAnswer.save();
		thirdQuestion.addValidity(0);	
		jeff.hasTimeToChange(thirdQuestion.id);
		thirdQuestion.save();
		
		assertEquals(50, bob.rating.totalRep);
		
		fourthAnswer.isBestAnswer = true;
		fourthAnswer.save();
		fourthQuestion.addValidity(0);	
		jeff.hasTimeToChange(fourthQuestion.id);
		fourthQuestion.save();
		
		assertEquals(100, bob.rating.totalRep);
		
		thirdAnswer.vote(jeff, true);
		assertEquals(110, bob.rating.totalRep);
		fourthAnswer.vote(jeff,true);
		assertEquals(120, bob.rating.totalRep);
		thirdQuestion.vote(jeff,true);
		assertEquals(125, bob.rating.totalRep);
		thirdAnswer.vote(jeff, true);
		assertEquals(125, bob.rating.totalRep);
	}

}
