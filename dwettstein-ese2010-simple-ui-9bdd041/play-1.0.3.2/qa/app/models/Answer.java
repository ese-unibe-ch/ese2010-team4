package models;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

/**A answer with content, timestamp, owner and voting.
 * 
 * @author dwettstein
 * 
 */
@Entity
public class Answer extends Model {
 
    public Date timestamp;
    public int voting;
     
    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @Required
    @ManyToOne
    public User author;
    
    @Required
    @OneToOne
    public Question question;
    
    
    public Answer(Question question, User author, String content) {
        this.question = question;
        this.author = author;
        this.content = content;
        this.timestamp = new Date(System.currentTimeMillis());
        this.voting = 0;
    }
 
    
    public String toString() {
    	return content;
    }
}

