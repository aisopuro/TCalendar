package calendar;

/**
 * Represents an exception that represents a corrupted .ics file. If critical
 * fields are missing or do not appear as they should, a
 * CorruptedCalendarFileException can be thrown.
 * 
 * @author aisopuro@tkk
 * 
 */

public class CorruptedCalendarFileException extends Exception {

	private static final long serialVersionUID = 1L;

	public CorruptedCalendarFileException() {
		// TODO Auto-generated constructor stub
	}

	public CorruptedCalendarFileException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CorruptedCalendarFileException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public CorruptedCalendarFileException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
