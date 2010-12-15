package models.helper;

import models.User;
import models.annotations.ForTestingOnly;

public class ValidationHelper {

	public boolean ckeck(String data, String string) {

		if (string.equals("Username")) {
			return checkUsername(data);

		} else if (string.equals("Email")) {
			return checkEmail(data);
		} else
			return false;

	}

	@ForTestingOnly
	public boolean checkUsername(String data) {
		User user = User.find("byUsername", data).first();
		return user == null;
	}

	@ForTestingOnly
	public boolean checkEmail(String data) {
		return data.matches("\\S+@(?:[A-Za-z0-9-]+\\.)+\\w{2,4}");
	}
}
