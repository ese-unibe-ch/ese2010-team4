package models.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**format String to Date an Date to String **/
public class DateFormatter {
	
	/** The Constant DATE_FORMAT_de. */
	public static final String DATE_FORMAT_de = "dd-MM-yyyy";
	
	/**
	 * Turns the Date object d into a String using the format given in the
	 * constant DATE_FORMAT_de.
	 * 
	 * @param d
	 *            the d
	 * @return the string
	 */
	public static String dateToString(Date d) {
		if (d != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_de);
			return fmt.format(d);
		} else {
			return "dd-mm-yyyy";
		}
	}

	/**
	 * Turns the String object s into a Date assuming the format given in the
	 * constant DATE_FORMAT_de.
	 * 
	 * @param s
	 *            the s
	 * @return the date
	 * @throws ParseException
	 *             the parse exception
	 */
	public static Date stringToDate(String s) throws ParseException {
		if (s != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT_de);
			return fmt.parse(s);
		} else {
			return null;
		}
	}
	
}
