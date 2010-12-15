import models.History;
import models.Question;
import models.User;

import org.junit.Test;

import play.test.UnitTest;

public class HistoryTest extends UnitTest {
	public User user = new User("Bob", "bob@bob.ch", "hallo");
	public Question question = new Question(null, "TestTitle",
			"This is for testing only");
	public History history = new History(question, question.title,
			question.content);

	@Test
	public void historyTest() {
		assertEquals(history.post, question);
		assertEquals(history.title, question.title);
		assertEquals(history.content, question.content);
	}
}
