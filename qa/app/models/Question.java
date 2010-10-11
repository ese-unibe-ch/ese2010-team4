package models;

import javax.persistence.*;
import java.util.*;

import play.db.jpa.*;
import play.data.validation.*;

/**A question with content, timestamp, owner and voting.
 * 
 * @author dwettstein
 * 
 */
@Entity
public class Question extends Model {
 
    public Date timestamp;
    public int voting;
    
    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @Required
    @ManyToOne
    public User author;
    
    public ArrayList<Answer> answers;
    
    public Question(User author, String content) {
        this.author = author;
        this.content = content;
        this.timestamp = new Date(System.currentTimeMillis());
        this.voting = 0;
        this.answers = new ArrayList<Answer>();
    }
    
    public Question addAnswer(User author, String content) {
    	assert(this.answers.isEmpty());
        Answer newAnswer = new Answer(this, author, content).save();
        this.answers.add(newAnswer);
        this.save();
        return this;
    }

    public Question previous() {
        return Question.find("timestamp < ? order by timestamp desc", timestamp).first();
    }
     
    public Question next() {
        return Question.find("timestamp > ? order by timestamp asc", timestamp).first();
    }

    public String toString() {
    	return content;
    }
 	
}
