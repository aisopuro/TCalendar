package calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * The class AbstractEvent describes calendar events, such as meetings, tests,
 * classes and todo-items.
 * 
 * @author aisopuro@tkk
 * 
 */

public class MeetingEvent implements Event {

	private String category;
	private String UID;
	private GregorianCalendar dateStamp;
	private GregorianCalendar start;
	private GregorianCalendar end;
	private GregorianCalendar expiration;
	private int priority;
	private boolean isRepeating;
	private int repeatFieldValue; // As specified in Calendar (WEEK,
	// DAY_OF_MONTH etc).
	private int interval;

	/**
	 * Creates a new Event.
	 * 
	 * @param start
	 *            The first time at which an event starts.
	 * @param end
	 *            The first time at which an event ends.
	 * @param repeatInterval
	 *            The repeat interval in milliseconds.
	 */

	public MeetingEvent(GregorianCalendar start, GregorianCalendar end) {
		this.category = "Event";
		this.dateStamp = new GregorianCalendar();
		this.start = start;
		this.end = end;
		this.expiration = end; // By default, the expiration date is the same as
		// the end date.
		this.priority = 0; // Default value, undefined priority.
		this.isRepeating = false;
	}

	public void setCategory(String type) {
		this.category = type;
	}

	public void setUID(String UID) {
		this.UID = this.dateTime(this.dateStamp) + " - " + UID;
	}

	public void setDateStamp(GregorianCalendar dateStamp) {
		this.dateStamp = dateStamp;
	}

	public void setRepeat(int repeatFieldValue, int interval,
			GregorianCalendar expiration) {
		this.isRepeating = true;
		this.repeatFieldValue = repeatFieldValue;
		this.interval = interval;
		this.expiration = expiration;
	}

	public void endRepeat() {
		this.isRepeating = false;
		this.repeatFieldValue = 0;
		this.interval = 0;
		this.expiration = this.end;

	}

	public GregorianCalendar getEnd() {
		return this.end;
	}

	public void setExpiration(GregorianCalendar expirationDate) {
		this.expiration = expirationDate;
	}

	public GregorianCalendar getExpiration() {
		return this.expiration;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return this.priority;
	}

	public long getDuration() {
		return this.end.getTimeInMillis() - this.start.getTimeInMillis();
	}

	public StringBuilder getSerialization() {
		StringBuilder serial = new StringBuilder();

		serial.append("BEGIN:VEVENT\n");
		serial.append("DTSTAMP:" + this.dateTime(this.dateStamp) + "\n");
		serial.append("UID:" + this.UID + "\n");
		serial.append("DTSTART:" + this.dateTime(this.start) + "\n");
		serial.append("DTEND:" + this.dateTime(this.end) + "\n");
		serial.append("CATEGORIES:" + this.category + "\n");

		if (this.priority > 0) {
			serial.append("PRIORITY:" + this.priority + "\n");
		}

		if (this.isRepeating) {
			StringBuilder currentLine = new StringBuilder(75);
			currentLine.append("RRULE:FREQ=");
			switch (this.repeatFieldValue) {
			case Calendar.WEEK_OF_YEAR:
				currentLine.append("WEEKLY");
				break;
			case Calendar.DAY_OF_YEAR:
				currentLine.append("DAILY");
				break;
			case Calendar.MONTH:
				currentLine.append("MONTHLY");
				break;
			case Calendar.YEAR:
				currentLine.append("YEARLY");
				break;
			}
			if (this.interval != 1) {
				currentLine.append(";INTERVAL=" + this.interval);
			}
			if (this.expiration != null) {
				currentLine.append(";UNTIL=" + this.dateTime(this.expiration));
			}
			serial.append(currentLine + "\n");
		}
		serial.append("END:VEVENT\n\n");

		return serial;
	}

	// Extracts the date in the form of a "yyyymmddThhmmss" string
	private String dateTime(GregorianCalendar date) {
		StringBuilder builder = new StringBuilder(15);
		char o = '0';
		int field;
		builder.append(date.get(Calendar.YEAR));
		field = date.get(Calendar.MONTH) + 1;
		if (field < 10) {
			builder.append(o);
		}
		builder.append(field);
		field = date.get(Calendar.DAY_OF_MONTH);
		if (field < 10) {
			builder.append(o);
		}
		builder.append(field);
		builder.append('T');
		field = date.get(Calendar.HOUR_OF_DAY);
		if (field < 10) {
			builder.append(o);
		}
		builder.append(field);
		field = date.get(Calendar.MINUTE);
		if (field < 10) {
			builder.append(o);
		}
		builder.append(field);
		field = date.get(Calendar.SECOND);
		if (field < 10) {
			builder.append(o);
		}
		builder.append(field);

		return builder.toString();
	}

	public GregorianCalendar getStart() {
		return this.start;
	}

	public String getCategory() {
		return this.category;
	}

	public String getTextDuration() {
		StringBuilder summary = new StringBuilder();
		int minute;
		summary.append(this.start.get(Calendar.HOUR_OF_DAY));
		summary.append(':');
		minute = this.start.get(Calendar.MINUTE);
		if (minute < 10) {
			summary.append('0');
		}
		summary.append(minute);
		summary.append(" - ");
		summary.append(this.end.get(Calendar.HOUR_OF_DAY));
		summary.append(':');
		minute = this.end.get(Calendar.MINUTE);
		if (minute < 10) {
			summary.append('0');
		}
		summary.append(minute);

		return summary.toString();
	}

	public boolean isValid(GregorianCalendar start, GregorianCalendar end) {
		if (!this.isRepeating) {
			return this.start.after(start) || this.end.before(end);
		}
		// Depending on the interval, the repeat may skip the day/week/whatever
		// in question
		if (this.isExpired(end) || this.start.after(end)) {
			return false;
		}

		int startEndDifference = DateCalc.getFieldDifference(
				this.repeatFieldValue, this.start, start);
		// If the event skips the date that is being viewed, return false.
		if (startEndDifference % this.interval != 0) {
			return false;
		}

		GregorianCalendar futureStart = (GregorianCalendar) this.start.clone();
		futureStart.add(this.repeatFieldValue, startEndDifference);
		GregorianCalendar futureEnd = (GregorianCalendar) this.end.clone();
		futureEnd.add(this.repeatFieldValue, startEndDifference);
		// if the starting time is within the fork or the ending time is, return
		// true
		return ((futureStart.after(start) && futureStart.before(end)) || (futureEnd
				.before(end))
				&& futureEnd.after(start))
				|| futureStart.before(start) && futureEnd.after(end);
	}

	// Checks if this event has expired by the time of the parameter end
	private boolean isExpired(GregorianCalendar end) {
		if (this.expiration == null) {
			return false;
		} else {
			return this.expiration.before(end);
		}
	}

	public void setNewStartEnd(GregorianCalendar start, GregorianCalendar end) {
		this.start = start;
		this.end = end;
	}

	public boolean isHighPriority() {
		return this.priority >= 1 && this.priority <= 4;
	}

	public boolean isRepeating() {
		return this.isRepeating;
	}

	public int getRepeatField() {
		return this.repeatFieldValue;
	}

	public int getInterval() {
		return this.interval;
	}

	public long getDurationInMonth(GregorianCalendar firstOfMonth) {
		GregorianCalendar lastOfMonth = (GregorianCalendar) firstOfMonth
				.clone();
		lastOfMonth.add(Calendar.MONTH, 1);
		long duration = this.end.getTimeInMillis()
				- this.start.getTimeInMillis();

		int lastDay = firstOfMonth.getMaximum(Calendar.DAY_OF_MONTH);
		GregorianCalendar first = (GregorianCalendar) firstOfMonth.clone();
		GregorianCalendar nextDay = (GregorianCalendar) firstOfMonth.clone();
		nextDay.add(Calendar.DAY_OF_MONTH, 1);
		int hitcount = 0;
		// Iterate throught the month, one day at a time until we find the first
		// valid day
		for (int i = 1; i < lastDay; i++) {
			if (this.isValid(first, nextDay)) {
				break;
			} else {
				first.add(Calendar.DAY_OF_MONTH, 1);
				nextDay.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		while (first.before(lastOfMonth)) {
			hitcount++;
			first.add(this.repeatFieldValue, this.interval);
		}
		return duration * hitcount;
	}

}
