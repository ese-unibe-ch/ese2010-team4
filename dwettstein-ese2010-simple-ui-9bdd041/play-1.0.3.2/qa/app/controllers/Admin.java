package controllers;
 
import play.*;
import play.data.validation.Required;
import play.mvc.*;
 
import java.util.*;
 
import models.*;
 
/**A controller for the administration.
 * 
 * @author dwettstein
 * 
 */
@With(Secure.class)
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }
 
    public static void index() {
    	render();
    }
 
    public static void myQuestions() {
    	User user = User.find("byEmail", Security.connected()).first();
    	List<Question> questions = Question.find("byAuthor", user).fetch();
        render(questions);
    }
    
    public static void myAnswers() {
    	User user = User.find("byEmail", Security.connected()).first();
    	List<Answer> answers = Answer.find("byAuthor", user).fetch();
        render(answers);
    }
    
    
    public static void createQuestion(@Required String author, @Required String content) {
    	if (validation.hasErrors()) {
            render("Admin/index.html");
    	}
        User user = User.find("byName", author).first();
        Question question = new Question(user, content).save();
        flash.success("Thanks for ask a new question %s!", author);
        Admin.myQuestions();
    }
    
    
    public static void answerQuestion(Long questionId, @Required String author, @Required String content) {
    	Question question = Question.findById(questionId);
    	if (validation.hasErrors()) {
            render("Application/show.html", question);
    	}
        User user = User.find("byName", author).first();
        question.addAnswer(user, content);
        flash.success("Thanks for write the answer %s!", author);
        Application.show(questionId);
    }
    
    
    public static void voteForQuestion(Long questionId, @Required User user, String vote) {
    	Question question = Question.findById(questionId);
    	if(vote.equals("Vote up"))
    		question.voting++;
    	else if(vote.equals("Vote down"))
    		question.voting--;
    	question.save();
        flash.success("Thanks for vote %s!", user.name);
        Application.show(questionId);
    }
    
    public static void voteForAnswer(Long questionId, Answer answer, @Required User user, String vote) {
    	if(vote.equals("Vote up"))
    		answer.voting++;
    	else if(vote.equals("Vote down"))
    		answer.voting--;
    	answer.save();
        flash.success("Thanks for vote %s!", user.name);
        Application.show(questionId);
    }
}
