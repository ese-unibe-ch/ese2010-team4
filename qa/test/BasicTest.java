import java.util.List;

import models.Answer;
import models.Post;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class BasicTest extends UnitTest {

	User hans;
	Question firstQuestion;
	Answer firstAnswer;
	
    @Test
    public void aVeryImportantThingToTest() {
        assertEquals(2, 1 + 1);
    }

    @Before
    public void setup() throws Exception {
    	Fixtures.deleteAll();
	    new User("Muster Hans", "hans@gmail.com", "keyword").save();
	    hans = User.find("byEmail", "hans@gmail.com").first();
	    firstQuestion = new Question(hans, "What is hot and shines brightly?").save();
		firstAnswer = new Answer(firstQuestion, hans, "It is the sun.").save();
    }
    
	@Test
	public void shouldCreateAndRetrieveUser() {
	    assertNotNull(hans);
	    assertEquals("Muster Hans", hans.fullname);
	}

	@Test
	public void shouldTryLoginAsUser() {
	    assertNotNull(User.login("hans@gmail.com", "keyword"));
	    //Not the right password.
	    assertNull(User.login("hans@gmail.com", "badKey"));
	    //Not the right email.
	    assertNull(User.login("hahn@gmail.com", "keyword"));
	}

	@Test
	public void shouldCreateQuestion() {
	    assertEquals(1, Question.count());	    
	    // Retrieve all questions created by Hans
	    List<Question> hansQuestions = Question.find("byAuthor", hans).fetch();

	    assertEquals(1, hansQuestions.size());
	    Post firstQuestion = hansQuestions.get(0);
	    assertNotNull(firstQuestion);
	    assertEquals(hans, firstQuestion.author);
	    assertEquals("What is hot and shines brightly?", firstQuestion.content);
	    assertNotNull(firstQuestion.timestamp);
	}

	@Test
	public void shouldAnswerAQuestion() {
		assertNotNull(firstAnswer);
	    assertEquals(hans, firstAnswer.author);
	    assertEquals("It is the sun.", firstAnswer.content);
	    assertNotNull(firstAnswer.timestamp);
	}

	@Test
	public void shouldOnlyExistsOneAnswer() {
	    assertEquals(1, User.count());
	    assertEquals(1, Question.count());
	    assertEquals(1, Answer.count());

	    firstQuestion.answers.add(firstAnswer);
	    assertNotNull(firstQuestion.answers);
	    assertEquals(hans, firstQuestion.answers.get(0).author);
	    
	    firstQuestion.addAnswer(hans, "It is the moon.");

	    assertEquals("It is the moon.", firstQuestion.answers.get(1).content);
	   
	}

	
}
