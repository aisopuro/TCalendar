package tests;

import java.util.Calendar;
import java.util.GregorianCalendar;

import calendar.MeetingEvent;
import calendarGUI.TCalGUI;

public class GUITest {
	
	public static void main(String[] args) {
		GregorianCalendar start = new GregorianCalendar();
		GregorianCalendar end = (GregorianCalendar) start.clone();
		end.add(Calendar.HOUR_OF_DAY, 2);
		MeetingEvent thing = new MeetingEvent(start, end);
		end.add(Calendar.WEEK_OF_YEAR, 3);
		thing.setRepeat(Calendar.DAY_OF_MONTH, 2, end);
		TCalGUI gui = new TCalGUI();
	}
}
