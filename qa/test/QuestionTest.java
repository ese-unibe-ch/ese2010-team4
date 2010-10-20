import java.util.List;

import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class QuestionTest extends UnitTest {

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
	public void shouldCreateAndRetrieveQuestion() {
	    assertEquals(1, Question.count());	    
	    // Retrieve all questions created by Hans
	    List<Question> hansQuestions = Question.find("byAuthor", hans).fetch();

	    assertEquals(1, hansQuestions.size());
	    firstQuestion= hansQuestions.get(0);
	    assertNotNull(firstQuestion);
	    assertEquals(hans, firstQuestion.author);
	    assertEquals("What is hot and shines brightly?", firstQuestion.content);
	    assertNotNull(firstQuestion.timestamp);
	    assertEquals("It is the sun.", firstQuestion.answers.get(0).content);
	}
	
	
	@Test
	public void shouldSetAllAnswerFalse(){
		
		firstAnswer.best = true;
		firstAnswer.save();
		Answer secondAnswer = new Answer(firstQuestion, sepp, "this is the moon").save();
		secondAnswer.best = true;
		
		firstQuestion.setAllAnswersFalse();
		
		assertFalse(firstQuestion.hasChosen());
		
	}
	
	@Test
	public void shouldHaveBestAnser(){
		
		firstQuestion.addAnswer(sepp, "it's the moon");
		Answer secondAnswer = Answer.find("byContent", "it's the moon").first();
		secondAnswer.best = true;
		secondAnswer.save();
		
		assertEquals(true, firstQuestion.answers.get(1).best);
		
		assertEquals(true, firstQuestion.hasChosen());
	}


	
}

