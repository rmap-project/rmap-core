/**
 * 
 */
package info.rmapproject.core.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author smorrissey
 *
 */
public class DateUtils {
	/**
	 * Format string for ISO-8601 date
	 */
	 public static String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	/**
	 * 
	 */
	private DateUtils() {}
	/**
	 * Parse ISO8601 date string into Date object
	 * @param dateString String containing ISO8601 formatted date
	 * @return Date object corresponding to dateString
	 * @throws ParseException
	 */
	public static Date getDateFromIsoString(String dateString) 
	throws ParseException{
		Date finalResult = null;
		DateFormat format = new SimpleDateFormat(ISO8601);
			finalResult = format.parse(dateString);		
		return finalResult;
	}
	/**
	 * 
	 * @param date
	 * @return
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public static String getIsoStringDate(Date date)
	throws NullPointerException, IllegalArgumentException {
		DateFormat format = new SimpleDateFormat(ISO8601);
		String dateString = format.format(date);
		return dateString;
	}
}
