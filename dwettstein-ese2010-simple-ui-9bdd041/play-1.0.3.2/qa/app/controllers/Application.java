package controllers;

import java.util.List;

import play.data.validation.*;
import models.*;
import play.mvc.*;

/**A controller for the index.
 * 
 * @author dwettstein
 * 
 */
public class Application extends Controller {
	
    public static void index() {
    	Question lastQuestion = Question.find("order by timestamp desc").first();
        List<Question> questions = Question.find("order by voting desc").fetch();
        render(lastQuestion, questions);
    }
    
    public static void show(Long id) {
        Question question = Question.findById(id);
        render(question);
    }

}