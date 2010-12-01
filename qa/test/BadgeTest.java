import models.Answer;
import models.Badge;
import models.Post;
import models.Question;
import models.Tag;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.db.jpa.JPASupport.JPAQuery;
import play.test.Fixtures;
import play.test.UnitTest;

public class BadgeTest extends UnitTest {

	User hans;
	User sepp;
	Question firstQuestion;
	Answer firstAnswer;
	Answer secondAnswer;
	Tag tag1;
	Tag tag2;
	Tag tag3;
	
	

    @Before
    public void setup() {
    	Fixtures.deleteAll();
    	User.createUser("Muster Hans", "hans@gmail.com", "keyword", "keyword");
    	User.createUser("Sepp", "sepp@sepp.ch", "hallo", "keyword");
    	hans = User.find("byEmail", "hans@gmail.com").first();
    	sepp = User.find("byEmail", "sepp@sepp.ch").first();
	    firstQuestion = new Question(hans, "brightliy?", "What is hot and shines brightly?").save();
		firstAnswer = new Answer(firstQuestion, hans, "It is the sun.").save();
		secondAnswer = new Answer(firstQuestion, sepp, "yeah this is a test").save();
		tag1 = Tag.findOrCreateByName("Java").save();
		tag2 = Tag.findOrCreateByName("test").save();
		tag3 = Tag.findOrCreateByName("Html").save();
		firstQuestion.tags.add(tag1);
		firstQuestion.tags.add(tag2);
		firstQuestion.tags.add(tag3);
		firstQuestion.save();
		secondAnswer.tags.add(tag3);
		secondAnswer.save();
    }
    
    @Test
    public void chooseTheRightBadge(){
    	
		this.firstAnswer.vote(sepp, true);
		firstAnswer.save();
    	Badge badge = Badge.find("byTagAndReputation", tag3, hans.rating).first();
    	Badge badge2 = Badge.find("byTagAndReputation", tag3, sepp.rating).first();
    	assertEquals(10, badge.rating);
    	assertEquals(null, badge2);
    	
    }
    
    
    @Test
    public void beABronzeSilverGoldBadge(){
    	
    	for(int i=0;i<10;i++){	
    		this.firstAnswer.vote(sepp, true);
    		firstAnswer.save();
    	}
    
    	Badge badge = Badge.find("byTagAndReputation", tag3, hans.rating).first();    
    	
    	assertEquals(true, badge.bronze);
    	assertEquals(false, badge.silver);
    	assertEquals(false, badge.gold);
    	assertEquals("Html", badge.toString());
    	
    	for(int i=0;i<20;i++){	
    		this.firstAnswer.vote(sepp, true);
    		firstAnswer.save();
    	}
    	
    	assertEquals(true, badge.bronze);
    	assertEquals(true, badge.silver);
    	assertEquals(false, badge.gold);
    	assertEquals("Html", badge.toString());
    	
    	for(int i=0;i<200;i++){	
    		this.firstAnswer.vote(sepp, true);
    		firstAnswer.save();
    	}
    	
    	assertEquals(true, badge.bronze);
    	assertEquals(true, badge.silver);
    	assertEquals(true, badge.gold);
    	assertEquals("Html", badge.toString());
    	
    }
    
    @Test
    public void addBadgeTagsToUser(){
    	
    	firstAnswer.vote(sepp, true).save();
    	assertEquals(0, hans.badgetags.size());
    	
    	for(int i=0;i<10;i++){
    		this.firstAnswer.vote(sepp, true);
    	}
    	
    	assertEquals(3, hans.badgetags.size());
    	
    }
    
    
    @Test
    public void findSimilarQuestions(){
    	
    	Question question1 = new Question(hans, "hallo", "velo").save();
    	Question question2 = new Question(sepp, "hallo", "velo").save();
    	question1.tagItWith("Java").tagItWith("Html").save();
    	question2.tagItWith("Html").save();
    	
     	for(int i=0;i<10;i++){
    		this.firstAnswer.vote(sepp, true);
    	}
     	
     	assertEquals(3, hans.badgetags.size());
     	assertEquals(1, firstAnswer.question.getSimilarPosts(2, hans.badgetags).size());
     	assertEquals(0, firstAnswer.question.getSimilarPosts(3, hans.badgetags).size());
     	assertEquals(2, firstAnswer.question.getSimilarPosts(1, hans.badgetags).size());	
    }
}

