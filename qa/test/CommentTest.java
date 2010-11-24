import models.Answer;
import models.Comment;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class CommentTest extends UnitTest {

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
    public void shouldCreateAndFindANewQuestionComment(){
    	
    	new Comment(hans, firstQuestion, "this is a comment").save();
    	Comment comment = firstQuestion.comments.get(0);
    	
    	assertEquals("this is a comment", comment.content);
    	assertEquals(hans, comment.author);
    	assertEquals(firstQuestion, comment.post);
    	assertEquals(1, Comment.count());
    }
    
    @Test
    public void shouldCreateAndFindANewAnswerComment(){
    	
    	new Comment(hans, firstAnswer, "this is a comment").save();
    	Comment comment = firstAnswer.comments.get(0);
    	
    	assertEquals("this is a comment", comment.content);
    	assertEquals(hans, comment.author);
    	assertEquals(firstAnswer, comment.post);
    	assertEquals(1, Comment.count());
    }
    
    @Test
    public void shouldCreateSeveralComments(){
    	
    	new Comment(hans, firstAnswer, "this is a comment").save();
    	new Comment(sepp, firstQuestion, "this is anoter comment").save();
    	new Comment(hans, firstAnswer, "and one more").save();
    	
    	assertEquals(3, Comment.count());
    }
	
}


