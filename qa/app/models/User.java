package models;

import javax.persistence.Entity;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * A user with name and first-name.
 * 
 * @author dwettstein
 * 
 */
@Entity
public class User extends Model {

	public String website = "";
	public String work = "";
	public String aboutMe = "";
	public String favoriteLanguages;
	public String avatarURL = "http://imgur.com/nmFX3.jpg";
	@Email
	@Required
	public String email;

	@Required
	public String password;

	@Required
	public String fullname;

	@Required
	public boolean isAdmin;

	public User(String fullname, String email, String password) {
		this.fullname = fullname;
		this.email = email;
		this.password = password;
		this.isAdmin = false;
	}

	public static User login(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}

	public String toString() {
		return fullname;
	}

}
