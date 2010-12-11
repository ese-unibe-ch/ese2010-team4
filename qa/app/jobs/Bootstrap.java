package jobs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import models.User;
import models.importer.XMLParser;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {

		// Check if the database is empty and imports startxml
		if (User.count() == 0) {
			XMLParser parser = new XMLParser();

			try {
				URL url = new File(Play.applicationPath + "/conf/startUp.xml")
						.toURI().toURL();
				parser.processURL(url);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
