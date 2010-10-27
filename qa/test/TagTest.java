import models.Answer;
import models.Post;
import models.Question;
import models.Tag;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

/**
 * 
 */

/**
 * @author simon
 * 
 */
public class TagTest extends UnitTest {

	User bob;
	User jeff;
	Question firstQuestion;
	Answer firstAnswer;

	@Before
	public void setup() throws Exception {
		Fixtures.deleteAll();
		Fixtures.load("data.yml");
		bob = User.find("byEmail", "bob@bob.ch").first();
		jeff = User.find("byEmail", "jeff@jeff.ch").first();
		firstQuestion = (Question) bob.posts.get(0);
		firstAnswer = firstQuestion.answers.get(0);

	}

	@Test
	public void testTags() {
		assertEquals(0, Post.findTaggedWith("Red").size());

		firstQuestion.tagItWith("Red").tagItWith("Blue").save();
		Tag yellow = Tag.findOrCreateByName("Yellow");
		Tag yellowCopy = Tag.findOrCreateByName("Yellow").save();
		Tag red = Tag.findOrCreateByName("Red");

		assertNotNull(yellow);
		assertEquals("Yellow", yellow.name);
		assertEquals(3, Tag.count());

		assertEquals(1, Post.findTaggedWith("Red").size());
		assertEquals(1, Post.findTaggedWith("Blue").size());
		assertEquals(0, Post.findTaggedWith("Green").size());
		assertEquals(0, yellow.compareTo(yellowCopy));
		assertTrue(0 < yellow.compareTo(red));
	}

}
