package tests;

import static org.junit.Assert.*;

import org.junit.Test;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;

import calendar.DateCalc;
import calendar.Event;
import calendar.MeetingEvent;
import calendarGUI.TCalParser;

public class TCalTest {
    @Test
    public void meetingTest() {

        Time week = new Time(7 * 24 * 60 * 60 * 1000);
        Time day = new Time(24 * 60 * 60 * 1000);
        GregorianCalendar start = new GregorianCalendar(2011, 2, 30, 13, 0);
        GregorianCalendar end = new GregorianCalendar(2011, 2, 30, 15, 0);
        GregorianCalendar expiry = new GregorianCalendar(2011, 4, 15);
        
        MeetingEvent test = new MeetingEvent(start, end);
        test.setRepeat(Calendar.DAY_OF_YEAR, 3, expiry);
        
        GregorianCalendar startmonth = new GregorianCalendar();
        startmonth = DateCalc.startOf(startmonth, Calendar.MONTH);
        
        long duration = test.getDurationInMonth(startmonth);
        
        System.out.println("Duration: " + TCalParser.presentDuration(duration));
    }
}
