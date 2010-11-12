package jobs;

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
			url = new URL("file:///home/d3orn/ese2010-team4/qa/public/QA3.xml");
			parser.processURL(url);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(User.findAll().toString());

	}
}
