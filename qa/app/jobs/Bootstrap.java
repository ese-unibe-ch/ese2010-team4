package jobs;

import models.User;
import play.jobs.Job;
import play.test.Fixtures;

public class Bootstrap extends Job {

	public void doJob() {

		// Check if the database is empty
		if (User.count() == 0) {
			Fixtures.load("initial-data.yml");
		}
	}
}
