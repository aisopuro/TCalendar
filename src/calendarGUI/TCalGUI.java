package calendarGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.*;

import calendar.CorruptedCalendarFileException;
import calendar.DateCalc;
import calendar.Event;
import calendar.TCalendar;
import calendarGUI.JTimeBlock;

/**
 * The class TCalGUI describes a graphical user interface for the TCalendar
 * program.
 * 
 * @author aisopuro
 * 
 */
public class TCalGUI implements ActionListener {

	// Public constants
	public static int BLOCKS_IN_HOUR = 4;
	public static int MINUTES_PER_BLOCK = 15;

	// Private constants
	private static Locale DEFAULT_LOCALE = Locale.UK;
	private static double SPLIT_PANE_WEIGHT = 0.7;
	private static int SCROLL_SPEED = 16;

	// Interactive objects
	private static JMenuItem MENU_NEW;
	private static JMenuItem MENU_IMPORT;
	private static JMenuItem MENU_LOAD;
	private static JMenuItem MENU_SAVE;

	private static JButton PREVIOUS_BUTTON;
	private static JButton CURRENT_BUTTON;
	private static JButton NEXT_BUTTON;

	private static JTextField GOTO;

	private static JButton MONTH_VIEW;

	// Other fields/objects
	private TCalendar calendar;
	private int currentWeek;
	private static GregorianCalendar thisMonday;
	private GregorianCalendar currentMonday;
	private JPanel toolPane;
	private JFrame topFrame;
	private JPanel weekPanel;
	private JToolBar weekdates;
	private JLabel currentYear;

	/**
	 * Constructs a new GUI.
	 */
	public TCalGUI() {
		this.topFrame = new JFrame("TCalendar");
		this.calendar = new TCalendar();
		this.currentWeek = 0; // Cursor, 0 = current week, negative for past,
		// positive for future weeks.

		// Initialize a monday to which weeks can be added and subtracted.
		thisMonday = new GregorianCalendar();
		thisMonday = DateCalc.startOf(thisMonday, Calendar.WEEK_OF_YEAR);
		this.currentMonday = (GregorianCalendar) thisMonday.clone();
		JSeparator separator;

		// Create the menu bar.
		JMenuBar menu = new JMenuBar();
		menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));

		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(10, menu.getHeight()));
		menu.add(separator);

		JMenu file = new JMenu("File");
		MENU_NEW = new JMenuItem("New Event");
		MENU_IMPORT = new JMenuItem("Import Events");
		MENU_LOAD = new JMenuItem("Load Calendar");
		MENU_SAVE = new JMenuItem("Save Calendar");
		// Set the action commands for the menu items.
		MENU_NEW.addActionListener(this);
		MENU_IMPORT.addActionListener(this);
		MENU_LOAD.addActionListener(this);
		MENU_SAVE.addActionListener(this);

		file.add(MENU_NEW);
		file.add(MENU_IMPORT);
		file.add(MENU_LOAD);
		file.add(MENU_SAVE);
		menu.add(file);

		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(20, menu.getHeight()));
		menu.add(separator);

		// Create navigation buttons for the week view.
		PREVIOUS_BUTTON = new JButton("Previous");
		CURRENT_BUTTON = new JButton("Current");
		NEXT_BUTTON = new JButton("Next");
		PREVIOUS_BUTTON.addActionListener(this);
		CURRENT_BUTTON.addActionListener(this);
		NEXT_BUTTON.addActionListener(this);

		// Add to menuBar
		menu.add(PREVIOUS_BUTTON);
		menu.add(CURRENT_BUTTON);
		menu.add(NEXT_BUTTON);

		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(70, menu.getHeight()));
		menu.add(separator);

		// Create the GoTo textfield.
		JLabel goToLabel = new JLabel("Go To Date: ");
		GOTO = new JTextField();
		GOTO.addActionListener(this);

		menu.add(goToLabel);
		menu.add(GOTO);

		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(50, menu.getHeight()));
		menu.add(separator);

		// Create the Month View button.
		MONTH_VIEW = new JButton("Month View");
		MONTH_VIEW.addActionListener(this);

		menu.add(MONTH_VIEW);

		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(50, menu.getHeight()));
		menu.add(separator);

		this.currentYear = new JLabel("" + this.currentMonday.get(Calendar.YEAR));
		menu.add(this.currentYear);

		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(300, menu.getHeight()));
		menu.add(separator);

		this.weekdates = new JToolBar();
		this.weekdates.setLayout(new GridLayout(1, 0));
		this.weekdates.setEnabled(false);
		this.weekDateUpdate(new GregorianCalendar());

		// Create the week view with empty JTimeBlocks
		this.weekPanel = new JPanel(new GridLayout(1, 8));
		this.weekPanel.setBackground(Color.WHITE);

		JTimeBlock unit;
		JTimeBlock previousBlock = null;
		for (int i = 1; i < 8; i++) {
			JPanel current = new JPanel();
			for (int j = 1; j <= 24 * BLOCKS_IN_HOUR; j++) { // TimeBlocks are
				// set to be 15
				// min long.
				unit = new JTimeBlock(previousBlock);
				unit.setPreferredSize(new Dimension(50, 45));
				// Mark hours with light grey borders
				if (j % BLOCKS_IN_HOUR == 0) {
					unit.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1,
							Color.LIGHT_GRAY));
				} else {
					unit.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1,
							Color.LIGHT_GRAY));
				}
				// Add a MouseListener that processes clicks
				unit.addMouseListener(new MouseListener() {

					// Process the click
					@Override
					public void mouseClicked(MouseEvent e) {
						processClick(e.getComponent());
					}

					// The rest intentionally left blank.
					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
					}

					@Override
					public void mouseEntered(MouseEvent e) {
					}
				});

				current.add(unit);
				previousBlock = unit;
			}
			current.setLayout(new BoxLayout(current, BoxLayout.Y_AXIS));
			this.weekPanel.add(current);
		}

		// Put the weekpanel, with the timeblocks, into scrollpane
		JScrollPane scrollPane = new JScrollPane(weekPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setLayout(new ScrollPaneLayout());
		scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);

		// Add the toolPane into its own scrollpane
		this.toolPane = new JPanel();
		JScrollPane toolScroll = new JScrollPane(this.toolPane,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		toolScroll.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
		JPanel week = new JPanel();
		week.setLayout(new BoxLayout(week, BoxLayout.Y_AXIS));
		week.add(this.weekdates);
		week.add(scrollPane);

		// Add the week and tool scrollpanes to a splitpane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				week, toolScroll);
		splitPane.setResizeWeight(SPLIT_PANE_WEIGHT);

		// Add all the components to the frame and show the GUI
		this.topFrame.add(splitPane);
		this.topFrame.setJMenuBar(menu);
		this.topFrame.setSize(1200, 800);
		this.topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.topFrame.setVisible(true);

	}

	// Returns all the timeblocks to default coloration
	private void flushWeek() {
		Component[] days = this.weekPanel.getComponents();
		Component[] blocks;
		int i;
		for (Component day : days) {
			blocks = ((Container) day).getComponents();
			i = 1;
			for (Component unit : blocks) {
				((JTimeBlock) unit).flush();
				if (((JTimeBlock) unit).getComponentCount() != 0) {
					((JTimeBlock) unit).removeAll();
				}
				if (i % BLOCKS_IN_HOUR == 0) {
					((JTimeBlock) unit).setBorder(BorderFactory
							.createMatteBorder(0, 1, 1, 1, Color.LIGHT_GRAY));
				} else {
					((JTimeBlock) unit).setBorder(BorderFactory
							.createMatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY));
				}
				i++;

			}
		}
	}

	// Load an .ics file (replaces the old calendar).
	private void load(File toLoad) {
		if (toLoad.getName().endsWith(".ics")) {
			try {
				this.calendar = new TCalendar(toLoad);
				this.showWeek(new GregorianCalendar());
			} catch (FileNotFoundException e) {
				new JErrorFrame(e.getMessage());
			} catch (IOException e) {
				new JErrorFrame(e.getMessage());
			} catch (CorruptedCalendarFileException e) {
				new JErrorFrame(e.getMessage());
			}
		} else {
			new JErrorFrame(
					"The file type is incorrect, only .ics files are accepted");
		}
	}

	// Import an .ics file (adds events to current calendar).
	private void importEvents(File toLoad) {
		if (toLoad.getName().endsWith(".ics")) {
			try {
				this.calendar.loadCalendar(toLoad);
				this.showWeek(new GregorianCalendar());
			} catch (FileNotFoundException e) {
				new JErrorFrame(e.getMessage());
			} catch (IOException e) {
				new JErrorFrame(e.getMessage());
			} catch (CorruptedCalendarFileException e) {
				new JErrorFrame(e.getMessage());
			}
		} else {
			new JErrorFrame(
					"The file type is incorrect, only .ics files are accepted");
		}
	}

	/**
	 * Adds an {@link Event} to the calendar and updates the GUI.
	 * 
	 * @param toAdd
	 *            The {@link Event} to be added.
	 */
	public void addEvent(Event toAdd) {
		this.calendar.addEvent(toAdd);
		this.showWeek(this.currentMonday);
	}

	/**
	 * Updates the week view.
	 */
	public void refreshWeek() {
		this.showWeek(this.currentMonday);
	}

	// Shows the currents week.
	private void showWeek(GregorianCalendar reference) {
		Component[] days = this.weekPanel.getComponents();

		this.flushWeek();

		this.weekDateUpdate(reference);
		reference = (GregorianCalendar) reference.clone();
		reference = DateCalc.startOf(reference, Calendar.WEEK_OF_YEAR);

		this.currentYear.setText(""+ reference.get(Calendar.YEAR));

		for (Component day : days) {
			this.updateDay(reference, (JPanel) day);
			reference.add(Calendar.DAY_OF_MONTH, 1);
		}
		this.topFrame.validate();
	}

	// Updates a day in the weekview (a column of JTimeBlocks)
	private void updateDay(GregorianCalendar reference, JPanel weekday) {
		GregorianCalendar start = (GregorianCalendar) reference.clone();
		GregorianCalendar end = (GregorianCalendar) reference.clone();

		start = DateCalc.startOf(start, Calendar.DAY_OF_MONTH);

		end = DateCalc.endOf(end, Calendar.DAY_OF_MONTH);

		ArrayList<Event> today = this.calendar.getDay(start);
		for (Event current : today) {
			this.colorBlocks(start, weekday, current, end);
		}
	}

	// Colors the necessary number of blocks, depending on the event.
	private void colorBlocks(GregorianCalendar dayStart, JPanel weekday,
			Event current, GregorianCalendar dayEnd) {

		int startIndex = this.startBlock(current, dayStart);
		int duration = this.getDurationInBlocks(current.getDuration());

		JTimeBlock start = (JTimeBlock) weekday.getComponent(startIndex);
		if (start.getComponentCount() == 0) {
			start.add(new JLabel(current.getCategory()));
			start.add(new JLabel(current.getTextDuration()));
		} else {
			start.removeAll();
			start.add(new JLabel("Overlap"));
		}
		start.addEvent(current, duration);
	}

	private int startBlock(Event target, GregorianCalendar day) {
		DateCalc.startOf(day, Calendar.DAY_OF_MONTH);
		GregorianCalendar futureStart = (GregorianCalendar) target.getStart()
				.clone();
		if (target.isRepeating()) {
			int fieldDifference = DateCalc.getFieldDifference(target
					.getRepeatField(), target.getStart(), day);
			int multiple = Math.abs(fieldDifference / target.getInterval());
			futureStart.add(target.getRepeatField(), multiple
					* target.getInterval());
		}
		if (futureStart.before(day)) {
			return 0;
		} else {
			int hour = target.getStart().get(Calendar.HOUR_OF_DAY);
			int minute = target.getStart().get(Calendar.MINUTE);
			return (hour * BLOCKS_IN_HOUR) + (minute / MINUTES_PER_BLOCK);
		}
	}

	// Calculates how many blocks are needed to represent the duration (as given
	// in milliseconds)
	private int getDurationInBlocks(long duration) {
		long minute = 1000 * 60;
		// If the duration is less than a single block, return 1
		if (duration < MINUTES_PER_BLOCK * minute) {
			return 1;
		}
		long hour = 60 * minute;
		long day = 24 * hour;
		long week = 7 * day;
		int blocks = 0;

		if (duration > week) {
			blocks += (duration / week) * 7 * 24 * BLOCKS_IN_HOUR;
			duration = duration % week;
		}
		if (duration > day) {
			blocks += (duration / day) * 24 * BLOCKS_IN_HOUR;
			duration = duration % day;
		}
		if (duration > hour) {
			blocks += (duration / hour) * BLOCKS_IN_HOUR;
			duration = duration % hour;
		}
		if (duration > minute) {
			blocks += (duration / minute) / MINUTES_PER_BLOCK;
		}
		return blocks;
	}

	/**
	 * Flushes the toolPanel, clearing all objects from it and updating the GUI
	 */
	public void flushToolPane() {
		this.toolPane.removeAll();
		this.toolPane.repaint();
	}

	// Updates the toolbar holding the weekdays and dates
	private void weekDateUpdate(GregorianCalendar reference) {
		reference = (GregorianCalendar) reference.clone();
		this.weekdates.removeAll();
		reference = DateCalc.startOf(reference, Calendar.WEEK_OF_YEAR);
		int day = 1;
		JLabel current;
		while (day <= 7) {
			StringBuilder date = new StringBuilder();
			date.append(reference.getDisplayName(Calendar.DAY_OF_WEEK,
					Calendar.SHORT, DEFAULT_LOCALE)
					+ " ");
			date.append(reference.get(Calendar.DAY_OF_MONTH));
			date.append('.');
			date.append((reference.get(Calendar.MONTH) + 1));

			current = new JLabel(date.toString());
			this.weekdates.add(current);
			this.weekdates.addSeparator();
			reference.add(Calendar.DAY_OF_WEEK, 1);
			day++;
		}
	}

	// Checks that the file inHere is ok before writing into it
	private void fileBuilder(File inHere) {
		if (this.calendar == null) {
			new JErrorFrame("There is no calendar to save.");
		}
		try {
			this.calendar.serializeCalendar(inHere);
		} catch (IOException e) {
			String error = "There was an unexpected I/O exception: "
					+ e.getMessage();
			new JErrorFrame(error);
		}
	}

	// If a user clicks a timeblock this adds previews of any Events into the
	// toolPane
	private void processClick(Component block) {
		this.flushToolPane();
		ArrayList<Event> overlapping = ((JTimeBlock) block).getEventsInBlock();
		JPanel list = new JPanel();
		list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
		if (overlapping != null) {
			for (Event current : overlapping) {
				list.add(new JEventPreview(this, current));
			}
		}
		this.toolPane.add(list);
		this.toolPane.validate();
	}

	/**
	 * Sets up a new JToolPanel into the toolPane
	 * 
	 * @param event
	 *            The {@link Event} to be edited, or null if a new {@link Event}
	 *            is created
	 */
	public void toolPaneEventSetup(Event event) {
		this.toolPane.add(new JToolPanel(this, event));
		this.toolPane.validate();
	}

	/**
	 * Removes a particular preview from the toolPane. If the associated
	 * {@link Event} was deleted, for example.
	 * 
	 * @param target
	 *            The {@link JEventPreview} object to be removed.
	 */
	public void removePreview(JEventPreview target) {
		this.toolPane.remove(target);
		this.toolPane.repaint();
	}

	/**
	 * Deletes an {@link Event} from the calendar. Also updates the GUI
	 * accordingly.
	 * 
	 * @param target
	 */
	public void deleteEvent(Event target) {
		this.calendar.removeEvent(target);
		this.showWeek(this.currentMonday);
	}

	// Shows the month view: a list of previews for all the high priority events
	// in the month, followed by a summary of their durations.
	private void showMonth() {
		JPanel monthList = new JPanel();
		monthList.setLayout(new BoxLayout(monthList, BoxLayout.Y_AXIS));
		String currentMonth = this.currentMonday.getDisplayName(Calendar.MONTH,
				Calendar.LONG, DEFAULT_LOCALE);
		JLabel monthName = new JLabel(currentMonth);
		monthList.add(monthName);
		ArrayList<Event> thismonth = this.calendar
				.getMonthsEvents(this.currentMonday);
		if (thismonth.isEmpty()) {
			monthList.add(new JLabel("No high-priority"));
			monthList.add(new JLabel("events this month"));
		} else {
			HashMap<String, Long> durations = new HashMap<String, Long>(
					thismonth.size());
			Long duration;
			Long previous;
			GregorianCalendar firstOf = (GregorianCalendar) this.currentMonday
					.clone();
			firstOf = DateCalc.startOf(firstOf, Calendar.MONTH);
			for (Event current : thismonth) {
				duration = new Long(current.getDurationInMonth(firstOf));
				previous = durations.get(current.getCategory());
				if (previous != null) {
					previous += duration;
				} else {
					durations.put(current.getCategory(), duration);
				}
				monthList.add(new JEventPreview(this, current));
			}

			JPanel summary = new JPanel();
			summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));

			JLabel label = new JLabel("Durations:");
			summary.add(label);
			JLabel line;
			for (String current : durations.keySet()) {
				line = new JLabel(current
						+ ": "
						+ TCalParser.presentDuration(durations.get(current)
								.longValue()));
				summary.add(line);
			}

			monthList.add(summary);
		}

		this.toolPane.add(monthList);
		this.toolPane.validate();
	}

	// Action handler
	@Override
	public void actionPerformed(ActionEvent action) {
		Object source = action.getSource();

		// Handle the "New" option in the "File" menu.
		if (MENU_NEW.equals(action.getSource())) {
			this.flushToolPane();
			this.toolPaneEventSetup(null);
		}
		// Handle the "Save" button in the "File" menu and build a Save dialog.
		if (MENU_SAVE.equals(source)) {
			JFileChooser chooser = new JFileChooser();

			chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

				@Override
				public String getDescription() {
					return "iCalendar (.ics)";
				}

				@Override
				public boolean accept(File f) {
					return f.getName().endsWith(".ics");
				}
			});
			JFrame saveFrame = new JFrame("Save Calendar");
			int buttonPressed = chooser.showSaveDialog(saveFrame);

			if (buttonPressed == JFileChooser.APPROVE_OPTION) {
				this.fileBuilder(chooser.getSelectedFile());
			}
		}
		// Handle the "Load" button in the "File" menu.
		if (MENU_LOAD.equals(source)) {
			JFileChooser chooser = new JFileChooser();
			JFrame loadFrame = new JFrame("Load a Calendar");
			int buttonPressed = chooser.showOpenDialog(loadFrame);

			if (buttonPressed == JFileChooser.APPROVE_OPTION) {
				this.load(chooser.getSelectedFile());
			}
		}

		// Handle the "Import" button in the "File" menu.
		if (MENU_IMPORT.equals(source)) {
			JFileChooser chooser = new JFileChooser();
			JFrame loadFrame = new JFrame("Import events");
			int buttonPressed = chooser.showOpenDialog(loadFrame);

			if (buttonPressed == JFileChooser.APPROVE_OPTION) {
				this.importEvents(chooser.getSelectedFile());
			}
		}

		// Handle the "GoTo" field's action.
		if (GOTO.equals(source)) {

			try {
				String input = TCalParser.inputDateParser(((JTextField) action
						.getSource()).getText());
				GregorianCalendar toHere = TCalParser.parseToDate(input,
						"00:00");
				this.currentMonday = DateCalc.startOf(toHere,
						Calendar.WEEK_OF_YEAR);
				this.showWeek(this.currentMonday);
				((JTextField) action.getSource()).setText("");
			} catch (IllegalArgumentException e) {
				new JErrorFrame("Not a valid date");
				return;
			}

		}

		// Handle the Previous, Current and Next buttons.
		if (PREVIOUS_BUTTON.equals(source)) {
			this.currentWeek--;
			this.currentMonday.add(Calendar.WEEK_OF_YEAR, -1);
			this.showWeek((GregorianCalendar) this.currentMonday.clone());
		}

		if (CURRENT_BUTTON.equals(source)) {
			this.currentWeek = 0;
			this.currentMonday = (GregorianCalendar) thisMonday.clone();
			this.showWeek((GregorianCalendar) this.currentMonday.clone());
		}

		if (NEXT_BUTTON.equals(source)) {
			this.currentWeek++;
			this.currentMonday.add(Calendar.WEEK_OF_YEAR, 1);
			this.showWeek((GregorianCalendar) this.currentMonday.clone());
		}

		// Handle the "Month View" button.
		if (MONTH_VIEW.equals(source)) {
			this.flushToolPane();
			this.showMonth();
		}
	}
}
