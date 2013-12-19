package calendarGUI;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * The TCalParser class is used mainly to parse input strings into dates and
 * times and vice versa.
 * 
 * @author aisopuro@tkk
 * 
 */

public class TCalParser {

	/**
	 * Parses a {@link String} into a specified form. The method accepts date
	 * strings of the form ddmm, ddmmyy, ddmmyyyy, dd.mm.yyyy, ddmm[y^x] and
	 * dd.mm.[y^x] and returns a date of the form dd.mm.yyyy.
	 * 
	 * @param input
	 *            The String to be parsed.
	 * @return A date of the form dd.mm.yyyy as a String.
	 */
	public static String inputDateParser(String input) {

		int length = input.length();
		StringBuilder builder = new StringBuilder(11);
		builder.append(input);
		GregorianCalendar current = new GregorianCalendar();

		switch (length) {
		case 4:
			builder.append(current.get(Calendar.YEAR) % 100);
		case 6:
			builder.insert(4, current.get(Calendar.YEAR) / 100);
		case 8:
			builder.insert(2, '.');
			builder.insert(5, '.');
		case 10:
			return builder.toString();
		}
		// In this case the user is going to AD 10000 and beyond.
		if (builder.length() > 10) {
			if (builder.charAt(2) != '.' || builder.charAt(5) != '.') {
				builder.insert(2, '.');
				builder.insert(5, '.');
			}
		}
		return builder.toString();
	}

	/**
	 * Parses a {@link String} representing a time into a more presentable
	 * format. Accepts strings of the form h, hh, hhmm and hh:mm.
	 * 
	 * @param input
	 *            The String to be parsed.
	 * @return A String representing the time of the form hh:mm.
	 */
	public static String inputTimeParser(String input) {
		int length = input.length();
		StringBuilder builder = new StringBuilder(5);

		switch (length) {
		case 1:
			builder.append('0');
		case 2:
			builder.append(input);
			builder.append(":00");
			break;
		case 4:
			builder.append(input);
			builder.insert(2, ':');
			break;
		case 5:
			builder.append(input);
		}
		return builder.toString();
	}

	/**
	 * Parses a date and time into a {@link GregorianCalendar}. The
	 * {@link GregorianCalendar} is lenient. The input is parsed before being
	 * processed, see inputDateParser() and inputTimeParser().
	 * 
	 * @param date
	 *            The date to be parsed.
	 * @param time
	 *            The time to be parsed.
	 * @return A {@link GregorianCalendar} representing the date and time given
	 *         as parameters.
	 */
	public static GregorianCalendar parseToDate(String date, String time)
			throws IllegalArgumentException {
		date = inputDateParser(date);
		time = inputTimeParser(time);
		if (date.length() > 0 && time.length() == 5) {

			int day = Integer.parseInt(date.substring(0, 2));
			int month = Integer.parseInt(date.substring(3, 5)) - 1;
			int year = Integer.parseInt(date.substring(6));

			int hour = Integer.parseInt(time.substring(0, 2));
			int minute = Integer.parseInt(time.substring(3));

			GregorianCalendar result = new GregorianCalendar(year, month, day,
					hour, minute);
			result.setLenient(false);
			// This method call throws an IllegalArgumentException if the user
			// inputs dates like 56th of January.
			result.get(Calendar.ERA);

			return result;
		} else {
			return null;
		}
	}

	/**
	 * Extracts the date of a {@link GregorianCalendar} as a dd.mm.yyyy
	 * {@link String}.
	 * 
	 * @param target
	 *            The {@link GregorianCalendar} whose date is to be extracted.
	 * @return A String of the form dd.mm.yyyy.
	 */
	public static String extractDate(GregorianCalendar target) {
		StringBuilder result = new StringBuilder();
		int current = target.get(Calendar.DAY_OF_MONTH);
		if (current < 10) {
			result.append('0');
		}
		result.append(current + ".");
		current = target.get(Calendar.MONTH) + 1;
		if (current < 10) {
			result.append('0');
		}
		result.append(current + ".");
		result.append(target.get(Calendar.YEAR));
		return result.toString();
	}

	/**
	 * Extracts the time of a {@link GregorianCalendar} as a hh:mm
	 * {@link String}.
	 * 
	 * @param target
	 *            The {@link GregorianCalendar} whose time is to be extracted.
	 * @return A hh:mm string representing the time. The time is displayed using
	 *         a 24h system.
	 */
	public static String extractTime(GregorianCalendar target) {
		StringBuilder result = new StringBuilder();
		int current = target.get(Calendar.HOUR_OF_DAY);
		if (current < 10) {
			result.append('0');
		}
		result.append(current + ":");
		current = target.get(Calendar.MINUTE);
		if (current < 10) {
			result.append('0');
		}
		result.append(current);
		return result.toString();
	}

	/**
	 * Creates a {@link String} representing a duration as given in
	 * milliseconds. The returned String is of the form XdYhZm.
	 * 
	 * @param duration
	 *            The duration to be presented, in milliseconds.
	 * @return A {@link String} of the form XdYhZm. Any durations less than a
	 *         minute are ignored. If a duration of less than a minute is given
	 *         as a parameter, an empty string is returned.
	 */
	public static String presentDuration(long duration) {
		long minute = 1000 * 60;
		int minutes = 0;
		long hour = 60 * minute;
		int hours = 0;
		long day = 24 * hour;
		int days = 0;

		if (duration > day) {
			days += ((int) duration / day);
			duration = duration % day;
		}
		if (duration > hour) {
			hours += ((int) duration / hour);
			duration = duration % hour;
		}
		if (duration > minute) {
			minutes += ((int) duration / minute);
		}
		StringBuilder builder = new StringBuilder();
		builder.append("");

		if (days > 0) {
			builder.append(days + "d ");
		}
		if (hours > 0) {
			builder.append(hours + "h ");
		}
		if (minutes > 0) {
			builder.append(minutes + "m");
		}

		return builder.toString();
	}

}
