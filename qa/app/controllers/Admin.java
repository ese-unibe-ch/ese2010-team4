package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import models.User;
import models.importer.XMLParser;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import controllers.Secure.Security;

@Check("admin")
@With(Secure.class)
public class Admin extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byUsername", Security.connected()).first();
			renderArgs.put("user", user.username);
		}
	}

	public static void importXML(File xml) throws FileNotFoundException,
			IOException {
		XMLParser parser = new XMLParser();
		URL url;
		String report = "";
		assert xml != null;

		try {
			url = xml.toURI().toURL();
			parser.processURL(url);
			report = parser.getReport();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		flash.success(parser.info());
		render(report);

	}

	public static void index() {
		render("CRUD/index.html");
	}

}
