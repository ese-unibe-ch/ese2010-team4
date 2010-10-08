package models;

import javax.persistence.*;
import javax.swing.ImageIcon;

import play.db.jpa.*;
import play.data.validation.*;


/**A user with name and first-name.
 * 
 * @author dwettstein
 * 
 */
@Entity
public class User extends Model {

	@Required
	public String name;
	
    @Email
    @Required
	public String email;
   
    @Required
	public String password;
	public boolean isAdmin;

	public String website = "";
	public String work = "";
	public String aboutMe = "";
	public ImageIcon avatar = new ImageIcon("empty-avatar_l.jpg");
	public String favoriteLanguages;

	
	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.isAdmin = false;
	}
	
	public static User login(String email, String password) {
	    return find("byEmailAndPassword", email, password).first();
	}

	public String toString() {
		return name;
	}
	
}
