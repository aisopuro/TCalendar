package calendar;

import java.util.GregorianCalendar;

/**
 * The interface Event describes events such as meetings or lectures that have a
 * specific length, start, end and may be repeating.
 * 
 * @author aisopuro@tkk
 * 
 */

public interface Event {
	/**
	 * Returns the date and time when this Event first starts.
	 * 
	 * @return A {@link GregorianCalendar} object representing the date and time
	 *         in question.
	 */
	public GregorianCalendar getStart();

	/**
	 * Returns the end of the event.
	 * 
	 * @return A {@link GregorianCalendar} object representing the duration.
	 */
	public GregorianCalendar getEnd();

	/**
	 * Sets a time for this event to expire, if the expiration is different from
	 * the end date.
	 * 
	 * @param expirationDate
	 *            A {@link GregorianCalendar} object representing the expiration
	 *            date.
	 */
	public void setExpiration(GregorianCalendar expirationDate);

	/**
	 * Returns the expiration date of the Event.
	 * 
	 * @return A {@link GregorianCalendar} object representing the expiration
	 *         date.
	 */
	public GregorianCalendar getExpiration();

	/**
	 * Returns a {@link String} representing the category(/ies) this event
	 * belongs to.
	 * 
	 * @return A {@link String} containing the category.
	 */
	public String getCategory();

	/**
	 * Gets the duration of the event in a presentable format.
	 * 
	 * @return A {@link String} of the form [hh:mm - hh:mm] (start - end).
	 */
	public String getTextDuration();

	/**
	 * Returns the duration of the event in milliseconds.
	 * 
	 * @return A long value representing the duration of this event.
	 */
	public long getDuration();

	/**
	 * Sets the starting and ending times according to the parameters.
	 * 
	 * @param start
	 *            The new starting time.
	 * @param end
	 *            The new ending time.
	 */
	public void setNewStartEnd(GregorianCalendar start, GregorianCalendar end);

	/**
	 * Returns a serialization of the Event in iCal-format. Note, the
	 * serialization is not by itself iCal-compatible, it has to be inserted
	 * into a VCALENDAR body.
	 * 
	 * @return The serialization as a {@link String}.
	 */
	public StringBuilder getSerialization();

	/**
	 * Sets the priority from 1-9. 1-5 represents low priority, 6 represents
	 * medium and 7-9 represents high (as specified by the iCal specification).
	 * 
	 * @param priority
	 *            The priority to be set.
	 */
	public void setPriority(int priority);

	/**
	 * Returns the priority of the Event.
	 * 
	 * @return An int value between 1-9 representing the priority.
	 */
	public int getPriority();

	/**
	 * Checks whether an event takes place in a certain time.
	 * 
	 * @param start
	 *            The time to start checking.
	 * @param end
	 *            The end of the interval to be checked in.
	 * @return true if the Event takes place whithin the specified time, false
	 *         otherwise.
	 */
	public boolean isValid(GregorianCalendar start, GregorianCalendar end);

	/**
	 * Checks whether the Event has a high priority.
	 * 
	 * @return true if the event's priority is equal to or greater than the
	 *         lowest priority rating that is considered to be a high priority.
	 */
	public boolean isHighPriority();

	/**
	 * Checks whether this is a repeating event.
	 * 
	 * @return true if this event has some kind of repeating rule, false
	 *         otherwise.
	 */
	public boolean isRepeating();

	/**
	 * Returns the value of the field that the repeating rule applies to. As
	 * specified in Calendar (i.e Calendar.DAY_OF_MONTH for daily repetition,
	 * for example).
	 * 
	 * @return An integer representing the field that the repeating rule applies
	 *         to, as specified in Calendar.
	 */
	public int getRepeatField();

	/**
	 * Returns the repeat interval of this Event.
	 * 
	 * @return The interval between repetitions of this event. If the event is
	 *         not a repeating event, returns 0.
	 */
	public int getInterval();

	/**
	 * Sets the category of the Event.
	 * 
	 * @param text
	 *            The category the event belongs to (eg. "Meeting").
	 */
	public void setCategory(String text);

	/**
	 * Sets the repeating rule for this event.
	 * 
	 * @param field
	 *            The field the repeat instruction applies to, such as
	 *            Calendar.DAY_OF_YEAR for daily repetition.
	 * @param interval
	 *            The interval for the repetition (every one/two/three
	 *            days/weeks etc.).
	 * @param expiry
	 *            The date that the Event expires and stops repeating. If this
	 *            parameter is left as null, the Event repeats forever.
	 */
	public void setRepeat(int field, int interval, GregorianCalendar expiry);
	
	/**
	 * Deletes all the repeating rules of this event.
	 */
	public void endRepeat();

	/**
	 * Returns the total number of milliseconds this event is valid within the
	 * specified month.
	 * 
	 * @param firstOfMonth
	 *            The first day of the month to be checked.
	 * @return The total duration of this event in the month in milliseconds.
	 */
	public long getDurationInMonth(GregorianCalendar firstOfMonth);

	/**
	 * Sets the UID of the Event.
	 * 
	 * @param UID
	 *            The Event's UID.
	 */
	public void setUID(String UID);

	/**
	 * Sets the datestamp of the event.
	 * 
	 * @param dateStamp
	 *            The new date stamp.
	 */
	// public void setDateStamp(GregorianCalendar dateStamp);
}
