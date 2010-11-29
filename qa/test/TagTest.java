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

	User bob, jeff;
	Question bobQuestion1, jeffQuestion2;
	Answer bobAnswer1, jeffAnswer1;

	@Before
	public void setup() throws Exception {
		Fixtures.deleteAll();
		Fixtures.load("data.yml");

		bob = User.find("byEmail", "bob@bob.ch").first();
		bobQuestion1 = (Question) bob.posts.get(0);
		bobAnswer1 = bobQuestion1.answers.get(0);
		jeffAnswer1 = bobQuestion1.answers.get(1);

		jeff = User.find("byEmail", "jeff@jeff.ch").first();
		jeffQuestion2 = (Question) jeff.posts.get(0);
	}

	@Test
	public void testTags() {
		assertEquals(0, Tag.count());

		bobQuestion1.tagItWith("Red").tagItWith("Blue").save();
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

	@Test
	public void testSimilarPosts() {
		bobQuestion1.tagItWith("Red").tagItWith("Blue").save();
		jeffQuestion2.tagItWith("Red").tagItWith("Yellow").save();

		assertEquals(1, bobQuestion1.getSimilarPosts(1).size());
		assertEquals(0, bobQuestion1.getSimilarPosts(2).size());

		jeffQuestion2.tagItWith("Blue").save();
		assertEquals(1, bobQuestion1.getSimilarPosts(2).size());
	}

}
