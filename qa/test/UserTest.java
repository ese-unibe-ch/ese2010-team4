import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest {

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

	
	public void shouldCreateANewUserWithCreateUserMethod(){
		
		String message = User.createUser("Rüedu", "ruedi@ruedi.ch", "test", "test");
		User user = User.find("byEmail", "ruedi@ruedi.ch").first();
		
		assertEquals(2, User.count());
		assertEquals("Rüedu", user.fullname);
		
		//Login successfully
		assertNotNull(User.login("ruedi@ruedi.ch", "test"));
		
		//wrong pw
		assertNull(User.login("ruedi@ruedi.ch", "testing"));
		//name instead of email
		assertNull(User.login("Rüedu", "test"));
		
	}
	
	@Test
	public void shouldReturnTheRightCreateUserMessages(){
		
		String message = User.createUser("Rüedu", "ruedi@ruedi.ch", "testing", "testing");
		User user = User.find("byEmail", "ruedi@ruedi.ch").first();
		
		assertEquals(message, "Hello, " + user.fullname + ", please log in");
		
		//not everything was filled
		message = User.createUser("Rüedu", "", "", "");
		assertEquals(message, "you forgot one or more gap's");
		
		//wrong password
		message = User.createUser("Rüedu", "rüedu@rüedu.ch", "test", "tets");
		assertEquals(message, "the password's aren't the same");
		
		//to short password
		message = User.createUser("Rüedu", "rüedu@rüedu.ch", "test", "test");
		assertEquals(message, "your password must be 6 singns or longer");		
	}
	
	@Test
	public void shouldBeAbleToVote(){
		assertTrue(sepp.isAbleToVote(firstQuestion.id));
	}
	
	@Test
	public void shouldBeNotAbleToVote(){
		assertFalse(hans.isAbleToVote(firstQuestion.id));
	}
	
	@Test
	public void shouldBeAbleToChoose(){
		assertTrue(hans.isAbleToChoose(firstQuestion.id));
	}
	
	@Test
	public void shouldNotBeAbleToChoose(){
		assertFalse(sepp.isAbleToChoose(firstQuestion.id));
	}


	
}

