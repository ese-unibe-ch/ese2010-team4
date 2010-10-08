import play.*;
import play.jobs.*;
import play.test.*;
 
import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
 
	User hans;
	User admin;
	Question firstQuestion;
	Question secondQuestion;
	Question thirdQuestion;
	Question fourthQuestion;
	Answer firstAnswer;
	
	
    public void doJob() {
	    hans = new User("Muster Hans", "hans@gmail.com", "keyword").save();
	    admin = new User("admin", "admin@gmail.com", "admin").save();
	    admin.isAdmin = true;
	    secondQuestion = new Question(hans, "Why this question hasn't been answered?").save();
	    firstQuestion = new Question(hans, "What is hot and shines brightly?").save();
		firstAnswer = new Answer(firstQuestion, hans, "It is the sun.").save();
		firstQuestion.answers.add(firstAnswer);
		secondQuestion.addAnswer(hans, "I don't know.");
		thirdQuestion = new Question(hans, "Why is programming so  frustrating?").save();
		fourthQuestion = new Question(admin, "Is this a question?").save();
    }
 
}
