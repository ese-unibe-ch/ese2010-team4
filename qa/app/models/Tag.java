/**
 * 
 */
package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * @author simon
 * 
 */
@Entity
public class Tag extends Model implements Comparable<Tag> {

	public String name;

	private Tag(String name) {
		this.name = name;
	}

	public int compareTo(Tag otherTag) {
		return name.compareTo(otherTag.name);
	}

	public static Tag findOrCreateByName(String name) {
		Tag tag = Tag.find("byName", name).first();
		if (tag == null) {
			tag = new Tag(name);
		}
		return tag;
	}

	public static String getTagString() {
		List<Tag> list = new ArrayList<Tag>();
		list = Tag.findAll();
		String s = "";
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				s = s + list.get(i).name + " ";
			}
		}
		System.out.println(s);
		return s;
	}

	public static String[] getTagArray() {
		List<Tag> list = new ArrayList<Tag>();
		list = Tag.all().fetch();
		String[] tags = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			tags[i] = list.get(i).name;
		}
		return tags;
	}
}
