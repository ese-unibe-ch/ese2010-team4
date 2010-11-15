package jobs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import models.User;
import models.importer.XMLParser;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {

		// Check if the database is empty
		if (User.count() == 0) {
			Fixtures.load("initial-data.yml");
		}
		XMLParser parser = new XMLParser();
		URL url;

		try {
			url = new File("qa/public/QA3.xml").toURI().toURL();
			parser.processURL(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
