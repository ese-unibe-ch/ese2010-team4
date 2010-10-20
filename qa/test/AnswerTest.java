import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class AnswerTest extends UnitTest {

	User hans;
	User sepp;
	Question firstQuestion;
	Answer firstAnswer;
	
	

    @Before
    public void setup() throws Exception {
    	Fixtures.deleteAll();
	    hans = new User("Muster Hans", "hans@gmail.com", "keyword").save();
	    sepp = new User("Sepp", "sepp@sepp.ch", "hallo").save();
	    firstQuestion = new Question(hans, "brightliy?", "What is hot and shines brightly?").save();
		firstAnswer = new Answer(firstQuestion, hans, "It is the sun.").save();
		
    }
    

	@Test
	public void shouldAnswerAQuestion() {
		assertNotNull(firstAnswer);
	    assertEquals(hans, firstAnswer.author);
	    assertEquals("It is the sun.", firstAnswer.content);
	    assertNotNull(firstAnswer.timestamp);
	}

	@Test
	public void shouldCreateAnQuestionWithAddAnswerMethod() {
	    assertEquals(2, User.count());
	    assertEquals(1, Question.count());
	    assertEquals(1, Answer.count());
	    assertNotNull(firstQuestion.answers);
	    assertEquals(hans, firstQuestion.answers.get(0).author);
	   
	    firstQuestion.addAnswer(hans, "It is the moon.");
	    assertEquals("It is the moon.", firstQuestion.answers.get(1).content);
	   
	}


	
}

