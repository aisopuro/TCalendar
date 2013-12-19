package calendar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TreeMap;

/**
 * The class TCalendar represents a calendar which can sort, save, load and
 * organize Event-type objects. It is compatible with certain iCal-functions
 * like event priority and repeating events. NOTE: The load and save functions
 * are not fully iCal-compatible: loading and saving a preexisting .ics file
 * from another application will almost certainly incur data loss.
 * 
 * @author aisopuro@tkk
 * 
 */
public class TCalendar {

	public static String UID = "TCalendar@aisopuro.tkk";

	private TreeMap<GregorianCalendar, Event> nonRepeaters; // Keys are
	// startDate.
	private ArrayList<Event> repeaters;

	/**
	 * Creates an empty TCalendar object with no Events.
	 */
	public TCalendar() {
		this.repeaters = new ArrayList<Event>();
		this.nonRepeaters = new TreeMap<GregorianCalendar, Event>();
	}

	/**
	 * Creates a TCalendar object with Events automatically loaded from the
	 * parameter File.
	 * 
	 * @param fileToRead
	 *            The File to be read. If the file is not of an .ics format, the
	 *            empty constructor is used instead.
	 * @throws CorruptedCalendarFileException
	 *             If one of the necessary fields of the .ics file is corrupted.
	 * @throws IOException
	 *             If an IO exception occurrs during the reading of the file.
	 * @throws FileNotFoundException
	 *             If the parameter File is not found.
	 */
	public TCalendar(File fileToRead) throws FileNotFoundException,
			IOException, CorruptedCalendarFileException {
		this.repeaters = new ArrayList<Event>();
		this.nonRepeaters = new TreeMap<GregorianCalendar, Event>();
		if (fileToRead.canRead()) {
			this.loadCalendar(fileToRead);
		} else {
			new TCalendar();
		}
	}

	/**
	 * Returns an ArrayList containing a day's events, starting from the time of
	 * date and ending 24h later.
	 * 
	 * @param date
	 *            The date at which to start looking.
	 * @return An ArrayList containing the day's events. Note that the events
	 *         will not necessarily be in order. If there are no events that
	 *         day, the ArrayList will be empty.
	 */
	public ArrayList<Event> getDay(GregorianCalendar date) {
		ArrayList<Event> daysEvents = new ArrayList<Event>();
		GregorianCalendar end = (GregorianCalendar) date.clone();
		end = DateCalc.endOf(end, Calendar.DAY_OF_MONTH);
		if (!this.nonRepeaters.isEmpty()) {
			daysEvents.addAll(this.nonRepeaters.subMap(date, end).values());
		}

		if (!this.repeaters.isEmpty()) {
			for (Event currentEvent : this.repeaters) {
				if (currentEvent.isValid(date, end)) {
					daysEvents.add(currentEvent);
				}
			}
		}
		return daysEvents;
	}

	/**
	 * Returns the events that have a high priority in a given month.
	 * 
	 * @param date
	 *            A date that is included in the month to be searched. Any
	 *            date-time in February, for example, will return a list of all
	 *            the high priority events starting february 1st to february
	 *            last.
	 * @return An {@link ArrayList} containing all the events that have a high
	 *         priority that month. Note, the events are not necessarily in
	 *         order of occurrence.
	 */
	public ArrayList<Event> getMonthsEvents(GregorianCalendar date) {
		ArrayList<Event> monthsEvents = new ArrayList<Event>();
		GregorianCalendar end = (GregorianCalendar) date.clone();
		date = DateCalc.startOf(date, Calendar.MONTH);
		end = DateCalc.endOf(end, Calendar.MONTH);
		if (!this.nonRepeaters.isEmpty()) {
			Collection<Event> monthsNonRepeaters = this.nonRepeaters.subMap(
					date, end).values();
			for (Event currentEvent : monthsNonRepeaters) {
				if (currentEvent.isHighPriority()) {
					monthsEvents.add(currentEvent);
				}
			}
		}

		if (!this.repeaters.isEmpty()) {
			for (Event currentEvent : this.repeaters) {
				if (currentEvent.isHighPriority()
						&& currentEvent.isValid(date, end)) {
					monthsEvents.add(currentEvent);
				}
			}
		}
		return monthsEvents;
	}

	/**
	 * Adds an {@link Event} to this calendar.
	 * 
	 * @param event
	 *            The {@link Event} to be added.
	 */
	public void addEvent(Event event) {
		if (event.isRepeating()) {
			this.repeaters.add(event);
		} else {
			this.nonRepeaters.put(event.getStart(), event);
		}
	}

	/**
	 * Removes an {@link Event} from this calendar.
	 * 
	 * @param target
	 */
	public void removeEvent(Event target) {
		if (target.isRepeating()) {
			this.repeaters.remove(target);
		} else {
			this.nonRepeaters.remove(target);
		}
	}

	/**
	 * Reads the given .ics file and constructs the events specified within it.
	 * 
	 * @param fileToRead
	 *            The .ics file to be read.
	 * @throws FileNotFoundException
	 *             If the file was not found.
	 * @throws IOException
	 *             If there is a problem while reading the file.
	 * @throws CorruptedCalendarFileException
	 *             If the .ics file is corrupted.
	 */
	public void loadCalendar(File fileToRead) throws FileNotFoundException,
			IOException, CorruptedCalendarFileException {
		FileReader reader = new FileReader(fileToRead);
		BufferedReader buffReader = new BufferedReader(reader);
		String currentLine;

		currentLine = buffReader.readLine();
		if (currentLine != null) {
			currentLine = currentLine.trim();
		}
		if (!"BEGIN:VCALENDAR".equalsIgnoreCase(currentLine)) {
			throw new CorruptedCalendarFileException(
					"VCALENDAR header is missing");
		}
		while (buffReader.ready()) {
			currentLine = buffReader.readLine().trim();

			if ("END:VCALENDAR".equalsIgnoreCase(currentLine)) {
				break;
			}
			if ("BEGIN:VEVENT".equalsIgnoreCase(currentLine)) {
				Event event = this.eventConstructor(buffReader);
				this.addEvent(event);
			}
		}
	}

	// Constructs an Event based on what buffReader contains.
	private Event eventConstructor(BufferedReader buffReader)
			throws IOException, CorruptedCalendarFileException {
		String currentLine;
		GregorianCalendar dateStamp = null;
		String UID = null;
		GregorianCalendar start = null;
		GregorianCalendar end = null;
		GregorianCalendar expiration = null;
		String category = null;
		boolean repeater = false;
		int priority = 0;
		int repeatFieldValue = 0;
		int interval = 1;

		while (buffReader.ready()) {
			currentLine = buffReader.readLine().toUpperCase().trim();
			if (currentLine == null) {
				throw new CorruptedCalendarFileException(
						"Unexpected end of file");
			}
			if ("END:VEVENT".equalsIgnoreCase(currentLine)) {
				break;
			}
			if (currentLine.contains("BEGIN")) {
				throw new CorruptedCalendarFileException(
						"Unexpected end of VEVENT section");
			}
			if (currentLine.startsWith("DTSTART")) {
				start = parseToDate(currentLine);
				continue;
			}
			if (currentLine.startsWith("UID")) {
				String[] split = currentLine.split(":");
				if (split.length != 2) {
					throw new CorruptedCalendarFileException(
							"The UID is corrupted or missing");
				}
				UID = split[1];
				continue;
			}
			if (currentLine.startsWith("DTEND")) {
				end = parseToDate(currentLine);
				continue;
			}
			if (currentLine.startsWith("CATEGORIES")) {
				category = currentLine.substring(currentLine.indexOf(":") + 1);
				continue;
			}
			if (currentLine.startsWith("PRIORITY")) {
				String prio = currentLine.split(":")[1];
				Integer rity = new Integer(prio);
				priority = rity.intValue();
				if (priority < 0 || priority > 9) {
					throw new CorruptedCalendarFileException(
							"The priority value is outside the permitted bounds (0-9)");
				}
				continue;
			}
			if (currentLine.startsWith("DURATION")) {
				Time duration = parseDURATION(currentLine);
				if (end != null) {
					throw new CorruptedCalendarFileException(
							"A VEVENT component contained both a DURATION and a DTEND-field.");
				} else {
					end = new GregorianCalendar();
					end.setTimeInMillis(start.getTimeInMillis()
							+ duration.getTime());
				}
				continue;
			}
			if (currentLine.startsWith("RRULE")) {
				repeater = true;
				if (!currentLine.contains("FREQ=")) {
					throw new IllegalArgumentException(
							"A Recurrence rule section is missing the FREQ-parameter.");
				}
				interval = 1;
				if (currentLine.contains("INTERVAL")) {
					int cursor = currentLine.indexOf("INTERVAL") + 9;
					StringBuilder value = new StringBuilder(4);
					while (cursor < currentLine.length()) {
						char current = currentLine.charAt(cursor);
						if (Character.isDigit(current)) {
							value.append(current);
							cursor++;
						} else {
							break;
						}
					}
					interval = new Integer(value.toString()).intValue();
				}
				if (currentLine.contains("WEEKLY")) {
					repeatFieldValue = Calendar.WEEK_OF_YEAR;
				} else if (currentLine.contains("DAILY")) {
					repeatFieldValue = Calendar.DAY_OF_YEAR;
				} else if (currentLine.contains("MONTHLY")) {
					repeatFieldValue = Calendar.MONTH;
				} else if (currentLine.contains("YEARLY")) {
					repeatFieldValue = Calendar.YEAR;
				} else {
					throw new CorruptedCalendarFileException(
							"The frequency of repetition is missing or corrupted");
				}
				if (currentLine.contains("UNTIL")) {
					String[] split = currentLine.split("UNTIL=");
					expiration = this.parseToDate(":" + split[1]);
				}
			}
			if (currentLine.startsWith("DTSTAMP")) {
				dateStamp = this.parseToDate(currentLine);
				continue;
			}
		}

		if (dateStamp == null || start == null || UID == null) {
			throw new CorruptedCalendarFileException(
					"A VEVENT section is missing required information");
		}
		if (end == null) {
			end = start;
		}
		MeetingEvent event = new MeetingEvent(start, end);
		event.setDateStamp(dateStamp);
		event.setPriority(priority);
		event.setCategory(category);
		event.setUID(UID);
		if (repeater) {
			event.setRepeat(repeatFieldValue, interval, expiration);
		}
		return event;
	}

	// Parses the DURATION field in an .ics file.
	private Time parseDURATION(String currentLine)
			throws CorruptedCalendarFileException {
		String dur = currentLine.split(":")[1];
		dur = dur.trim();
		dur.toUpperCase();
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		char currentChar = dur.charAt(0);
		if (currentChar != 'P') {
			throw new CorruptedCalendarFileException(
					"The DURATION section is corrupted.");
		} else {
			int first = 0;
			int second = 0;
			int i = 1;
			while (i < dur.length()) {
				currentChar = dur.charAt(i);
				if (Character.isDigit(currentChar)) {
					if (first != 0 || second != 0) {
						throw new CorruptedCalendarFileException(
								"The DURATION section is corrupted.");
					}
					first = Character.getNumericValue(currentChar);
					i++;
					if (Character.isDigit(dur.charAt(i))) {
						second = Character.valueOf(dur.charAt(i));
						i++;
					}
					continue;
				} else {
					if (second != 0) {
						first *= 10;
					}
					int sum = first + second;
					first = 0;
					second = 0;
					switch (currentChar) {
					case 'W':
						weeks = sum;
						continue;
					case 'D':
						days = sum;
						continue;
					case 'H':
						hours = sum;
						continue;
					case 'M':
						minutes = sum;
						continue;
					case 'S':
						seconds = sum;
						continue;
					}
					throw new CorruptedCalendarFileException(
							"A DURATION-field contained illegal characters (only 0-9, W, D, H, M and S are allowed).");
				}
			}
		}
		return new Time(weeks * 7 * 24 * 60 * 60 * 1000 + days * 24 * 60 * 60
				* 1000 + hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds
				* 1000);
	}

	// Parses a line containng yyyymmddThhmmss into a GregorianCalendar.
	private GregorianCalendar parseToDate(String parseable)
			throws CorruptedCalendarFileException {
		String[] line = parseable.split(":");
		if (line.length != 2) {
			throw new CorruptedCalendarFileException(
					"A date value is incorrect");
		}
		String time = line[1]; // Only one colon is allowed per line. (Except
		// DESCRIPTION, which is not supported)
		Integer year = new Integer(time.substring(0, 4));
		Integer month = new Integer(time.substring(4, 6)) - 1; // January is
		// month zero.
		Integer dayOfMonth = new Integer(time.substring(6, 8));

		if (time.length() == 8) {
			return new GregorianCalendar(year.intValue(), month.intValue(),
					dayOfMonth.intValue(), 0, 0, 0);
		} else if (time.length() >= 15) {
			Integer hour = new Integer(time.substring(9, 11));
			Integer minute = new Integer(time.substring(11, 13));
			Integer second = new Integer(time.substring(13, 15));
			return new GregorianCalendar(year.intValue(), month.intValue(),
					dayOfMonth.intValue(), hour.intValue(), minute.intValue(),
					second.intValue());
		} else {
			throw new CorruptedCalendarFileException(
					"A date value is incorrect");
		}
	}

	/**
	 * Writes this calendar into the specified file.
	 * 
	 * @param inHere
	 *            The File that is to be written into.
	 * @throws IOException
	 */
	public void serializeCalendar(File inHere) throws IOException {
		// First build the String to be saved.
		StringBuilder calendar = new StringBuilder();
		calendar.append("BEGIN:VCALENDAR\n");
		calendar.append("PRODID:aisopuro.TCalendar@tkk\n");
		calendar.append("VERSION:2.0\n");

		if (!this.nonRepeaters.isEmpty()) {
			Collection<Event> iterator = this.nonRepeaters.values();
			for (Event event : iterator) {
				calendar.append(event.getSerialization());
			}
		}
		if (!this.repeaters.isEmpty()) {
			for (Event event : this.repeaters) {
				calendar.append(event.getSerialization());
			}
		}

		calendar.append("END:VCALENDAR\n");

		// Write the String into an .ics file.
		String path = inHere.getAbsolutePath();
		if (!path.endsWith(".ics")) {
			path = path.concat(".ics");
			inHere.delete();
			inHere = new File(path);
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(inHere));
		writer.write(calendar.toString());
		writer.close();

	}

}
