import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ActivitiesTest extends UnitTest {

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
		bob.followQ.add(firstQuestion);
		bob.save();
	}

	@Test
	public void shouldHaveFollowActivities() {

		assertEquals(1, bob.followAcitvities(10).size());
		bob.followU.add(jeff);
		assertEquals(4, bob.followAcitvities(10).size());
		assertEquals(2, bob.followAcitvities(2).size());

	}

}
