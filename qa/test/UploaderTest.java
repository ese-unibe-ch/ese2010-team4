import org.junit.Test;

import play.mvc.Before;
import play.test.UnitTest;
import controllers.Uploader;

public class UploaderTest extends UnitTest {

	Uploader uploader;

	@Before
	public void setUp() {
		uploader = new Uploader("qa/test/test-uploads");
	}

	@Test
	public void normalUpload() {

	}
}
