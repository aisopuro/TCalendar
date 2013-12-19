package calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * The DateCalc class is used by the TCalendar program to perform various
 * calculations on {@link GregorianCalendar} date objects and input strings.
 * 
 * @author aisopuro@tkk
 * 
 */

public class DateCalc {

	/**
	 * Sets the target date to the start of the specified field. For example, a
	 * {@link GregorianCalendar} representin Jan 13, 2011 at 15:14, when given
	 * with the field value of Calendar.MONTH will return Jan 01, 2011, 00:00.
	 * Note that this implementation regards Monday as the first weekday of the
	 * week. Note also that the method changes the fields of the parameter date:
	 * if you require it to remain inviolate, clone it before calling this
	 * method.
	 * 
	 * @param target
	 *            The {@link GregorianCalendar} object to be modified.
	 * @param field
	 *            The field with respect to which the target is to be modified.
	 *            For example, Calendar.WEEK_OF_YEAR sets the target to monday,
	 *            00:00.
	 * @return The modified date.
	 */
	public static GregorianCalendar startOf(GregorianCalendar target, int field) {
		switch (field) {
		case Calendar.YEAR:
			target.set(Calendar.MONTH, target.getMinimum(Calendar.MONTH));

		case Calendar.MONTH:
			target.set(Calendar.DAY_OF_MONTH, target
					.getMinimum(Calendar.DAY_OF_MONTH));

		case Calendar.DAY_OF_MONTH:
			target.set(Calendar.HOUR_OF_DAY, target
					.getMinimum(Calendar.HOUR_OF_DAY));
			target.set(Calendar.MINUTE, target.getMinimum(Calendar.MINUTE));
			target.set(Calendar.SECOND, target.getMinimum(Calendar.SECOND));
			break;

		case Calendar.WEEK_OF_YEAR:
			int offset = 0;
			if (target.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				offset = -6;
			} else if (target.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
				offset = Calendar.MONDAY - target.get(Calendar.DAY_OF_WEEK);
			}
			target.add(Calendar.DAY_OF_WEEK, offset);
			target.set(Calendar.HOUR_OF_DAY, target
					.getMinimum(Calendar.HOUR_OF_DAY));
			target.set(Calendar.MINUTE, target.getMinimum(Calendar.MINUTE));
			target.set(Calendar.SECOND, target.getMinimum(Calendar.SECOND));

		}
		return target;
	}

	/**
	 * Sets the target date to the end of the specified field. For specifics,
	 * see startOf(target, field).
	 * 
	 * @param target
	 *            The date to be changed.
	 * @param field
	 *            The field with respect to which the date is to be changed.
	 * @return The modified date.
	 */
	public static GregorianCalendar endOf(GregorianCalendar target, int field) {
		switch (field) {
		case Calendar.YEAR:
			target.set(Calendar.MONTH, target.getMaximum(Calendar.MONTH));
		case Calendar.MONTH:
			target.set(Calendar.DAY_OF_MONTH, target
					.getMaximum(Calendar.DAY_OF_MONTH));
		case Calendar.DAY_OF_MONTH:
			target.set(Calendar.HOUR_OF_DAY, target
					.getMaximum(Calendar.HOUR_OF_DAY));
			target.set(Calendar.MINUTE, target.getMaximum(Calendar.MINUTE));
			target.set(Calendar.SECOND, target.getMaximum(Calendar.SECOND));
			break;
		case Calendar.WEEK_OF_YEAR:
			int offset = 0;
			if (target.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
				offset = 6;
			} else if (target.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				offset = target.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			}
			target.add(Calendar.DAY_OF_WEEK, offset);
			target.set(Calendar.HOUR_OF_DAY, target
					.getMaximum(Calendar.HOUR_OF_DAY));
			target.set(Calendar.MINUTE, target.getMaximum(Calendar.MINUTE));
			target.set(Calendar.SECOND, target.getMaximum(Calendar.SECOND));

		}
		return target;
	}

	/**
	 * Calculates the difference in the specified field between two dates. Jan
	 * 05 and Jan 14 of 2011, for example, are 9 days or 1 week apart. The
	 * difference is calculated roughly as toHere.field - start.field, so if
	 * toHere is before start, the method returns a negative value.
	 * 
	 * @param field
	 *            The field whose difference is to be calculated (eg
	 *            Calendar.DAY_OF_YEAR or Calendar.WEEK:OF_YEAR).
	 * @param start
	 *            The starting date.
	 * @param toHere
	 *            The ending date.
	 * @return The "distance" from start to toHere, in terms of the field value.
	 */
	public static int getFieldDifference(int field, GregorianCalendar start,
			GregorianCalendar toHere) {
		int startYear = start.get(Calendar.YEAR);
		int toYear = toHere.get(Calendar.YEAR);

		if (field == Calendar.YEAR) {
			return toYear - startYear;
		}

		if (field == Calendar.MONTH) {
			int difference = (toYear - startYear) * 12;
			difference += toHere.get(Calendar.MONTH);
			difference -= start.get(Calendar.MONTH);
			return difference;
		}

		int startDays = start.get(Calendar.DAY_OF_YEAR);
		int toDays = toHere.get(Calendar.DAY_OF_YEAR);

		if ((field == Calendar.DAY_OF_YEAR || field == Calendar.DAY_OF_MONTH)
				&& startYear == toYear) {
			return toDays - startDays;
		}
		// Calculate how many days to add/subtract for every full year thats
		// gone by.
		startYear--;
		toYear--;

		startDays += startYear * 365;
		toDays += toYear * 365;

		// Every fourth year is a leap year (exceptions follow).
		startDays += startYear / 4;
		toDays += toYear / 4;

		// Every 100th year is not a leap year.
		startDays -= startYear / 100;
		toDays -= toYear / 100;

		// Every 400th year is a leap year.
		startDays += startYear / 400;
		toDays += toYear / 400;
		if (field == Calendar.WEEK_OF_YEAR) {
			return (toDays - startDays) / 7;
		}
		if (field == Calendar.DAY_OF_YEAR || field == Calendar.DAY_OF_MONTH) {
			return toDays - startDays;
		}

		return 0;
	}

}
