package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import calendar.CorruptedCalendarFileException;
import calendar.Event;
import calendar.MeetingEvent;
import calendar.TCalendar;

public class TCalMethodTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            File file = new File("test2.ics");
            TCalendar calendar = new TCalendar(file);
        } catch (CorruptedCalendarFileException problem) {
            System.out.println(problem);
            problem.printStackTrace();
        } catch (FileNotFoundException notFound) {
            System.out.println(notFound);
        } catch (IOException problem) {
            System.out.println(problem);
            problem.printStackTrace();
        }

    }
    
    private static String dateTime(GregorianCalendar date) {
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
    
    private static Event eventConstructor(BufferedReader buffReader) throws IOException, CorruptedCalendarFileException {
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
                throw new CorruptedCalendarFileException("Unexpected end of file");
            }
            if ("END:VEVENT".equalsIgnoreCase(currentLine)) {
                break;
            }
            if (currentLine.contains("BEGIN")) {
                throw new CorruptedCalendarFileException("Unexpected end of VEVENT section");
            }
            if (currentLine.startsWith("DTSTART")) {
                start = parseToDate(currentLine);
                continue;
            }
            if (currentLine.startsWith("UID")) {
                String[] split = currentLine.split(":");
                if (split.length != 2) {
                    throw new CorruptedCalendarFileException("The UID is corrupted or missing");
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
                    throw new CorruptedCalendarFileException("The priority value is outside the permitted bounds (0-9)");
                }
                continue;
            }
            if (currentLine.startsWith("DURATION")) {
                Time duration = parseDURATION(currentLine);
                if (end != null) {
                    throw new CorruptedCalendarFileException("A VEVENT component contained both a DURATION and a DTEND-field.");
                } else {
                    end = new GregorianCalendar();
                    end.setTimeInMillis(start.getTimeInMillis() + duration.getTime());
                }
                continue;
            }
            if (currentLine.startsWith("RRULE")) {
                repeater = true;
                if (!currentLine.contains("FREQ=")) {
                    throw new IllegalArgumentException("A Recurrence rule section is missing the FREQ-parameter.");
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
                    repeatFieldValue = Calendar.DAY_OF_MONTH;
                } else if (currentLine.contains("MONTHLY")) {
                    repeatFieldValue = Calendar.MONTH;
                } else if (currentLine.contains("YEARLY")) {
                    repeatFieldValue = Calendar.YEAR;
                } else {
                    throw new CorruptedCalendarFileException("The frequency of repetition is missing or corrupted");
                }
                if (currentLine.contains("UNTIL")) {
                    String[] split = currentLine.split("UNTIL=");
                    expiration = parseToDate(":" + split[1]);
                } else if (currentLine.contains("COUNT")) {
                    //Is this necessary?
                }
            }
            if (currentLine.startsWith("DTSTAMP")) {
                dateStamp = parseToDate(currentLine);
                continue;
            }
        }
        if (dateStamp == null || start == null || UID == null) {
            throw new CorruptedCalendarFileException("A VEVENT section is missing required information");
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

    private static Time parseDURATION(String currentLine) throws CorruptedCalendarFileException {
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
            throw new CorruptedCalendarFileException("The DURATION section is corrupted.");
        } else {
            int first = 0;
            int second = 0;
            int i = 1;
            while (i < dur.length()) {
                currentChar = dur.charAt(i);
                if (Character.isDigit(currentChar)) {
                    if (first != 0 || second != 0) {
                        throw new CorruptedCalendarFileException("The DURATION section is corrupted.");
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
        return new Time(weeks*7*24*60*60*1000 + days*24*60*60*1000 + hours*60*60*1000 + minutes*60*1000 + seconds*1000);
    }


    private static GregorianCalendar parseToDate(String parseable) throws CorruptedCalendarFileException {
        String[] line = parseable.split(":");
        if (line.length != 2) {
            throw new CorruptedCalendarFileException("A date value is incorrect");
        }
        String time = line[1]; // Only one colon is allowed per line. (Except description)
        Integer year = new Integer(time.substring(0, 4));
        Integer month = new Integer(time.substring(4, 6)) - 1; //Months start at zero.
        Integer dayOfMonth = new Integer(time.substring(6, 8));

        if (time.length() == 8) {
            return new GregorianCalendar(year.intValue(), month.intValue(), dayOfMonth.intValue(), 0, 0, 0);
        } else if (time.length() >= 15) {
            Integer hour = new Integer(time.substring(9, 11));
            Integer minute = new Integer(time.substring(11, 13));
            Integer second = new Integer(time.substring(13, 15));
            return new GregorianCalendar(year.intValue(), month.intValue(), dayOfMonth.intValue(), hour.intValue(),
                    minute.intValue(), second.intValue());
        } else {
            throw new CorruptedCalendarFileException("A date value is incorrect");
        }
    }

}
