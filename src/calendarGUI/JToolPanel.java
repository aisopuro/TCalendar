package calendarGUI;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import calendar.Event;
import calendar.MeetingEvent;
import calendar.TCalendar;

import java.lang.IllegalArgumentException;

/**
 * A JToolPanel represents a series of textfields and comboboxes that a user
 * uses to input the parameters of a new event, or for editing a preexisting
 * one.
 * 
 * @author aisopuro@tkk
 * 
 */
public class JToolPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L; // A default UID as given
	// by the Eclipse SDK
	// Buttons
	private static JButton APPLY_BUTTON;
	private static JButton CANCEL_BUTTON;
	private static JButton EDIT_BUTTON;
	private static JButton DELETE_BUTTON;

	// References to the input fields
	private TCalGUI master;
	private Event target;

	private JTextField category;

	private JTextField startDate;
	private JTextField startTime;

	private JTextField endDate;
	private JTextField endTime;

	private JComboBox priority;

	private JComboBox rrule;
	private JTextField interval;
	private JTextField expiry;

	/**
	 * Creates a new JToolPanel with the specified {@link Event}.
	 * 
	 * @param master
	 *            The {@link TCalGUI} that this JToolPane belongs to.
	 * @param target
	 *            The {@link Event} that is to be edited, or null if a new
	 *            {@link Event} is created.
	 */
	public JToolPanel(TCalGUI master, Event target) {
		this.master = master;
		this.target = target;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Check the label, either New Event or Change Event
		JLabel label;
		if (this.target == null) {
			label = new JLabel("New Event");
		} else {
			label = new JLabel("Change Event");
		}
		this.add(label);

		// Create the categorization section
		JPanel categorize = new JPanel(new GridLayout(1, 2));
		JLabel what = new JLabel("Category:");
		this.category = new JTextField();
		if (target != null) {
			this.category.setText(target.getCategory());
		}
		categorize.add(what);
		categorize.add(this.category);
		this.add(categorize);

		// Create the starting date/time section
		JPanel start = new JPanel(new GridLayout(2, 2));
		JLabel startName = new JLabel("Starting Date:");
		JLabel startName2 = new JLabel("Starting Time:");
		// If the Event isn't null, input it's values as defaults
		if (target != null) {
			this.startDate = new JTextField(TCalParser.extractDate(target
					.getStart()));
			this.startTime = new JTextField(TCalParser.extractTime(target
					.getStart()));
		} else {
			this.startDate = new JTextField();
			this.startTime = new JTextField();
		}
		start.add(startName);
		start.add(this.startDate);
		start.add(startName2);
		start.add(this.startTime);
		this.add(start);

		// Create the ending date/time section
		JPanel end = new JPanel(new GridLayout(2, 2));
		JLabel endName = new JLabel("Ending Date:");
		JLabel endName2 = new JLabel("Ending Time:");
		if (target != null) {
			this.endDate = new JTextField(TCalParser.extractDate(target
					.getEnd()));
			this.endTime = new JTextField(TCalParser.extractTime(target
					.getEnd()));
		} else {
			this.endDate = new JTextField();
			this.endTime = new JTextField();
		}
		end.add(endName);
		end.add(this.endDate);
		end.add(endName2);
		end.add(this.endTime);
		this.add(end);

		// Create the priority selection
		JPanel prior = new JPanel(new GridLayout(1, 2));
		JLabel whut = new JLabel("Priority:");
		String[] priorities = { "None", "Low", "Medium", "High" };
		this.priority = new JComboBox(priorities);
		if (target != null) {
			int p = target.getPriority();
			if (p > 5 && p < 10) {
				this.priority.setSelectedIndex(1);
			} else if (p == 5) {
				this.priority.setSelectedIndex(2);
			} else if (p < 5 && p > 0) {
				this.priority.setSelectedIndex(3);
			}
		}
		prior.add(whut);
		prior.add(this.priority);
		this.add(prior);

		// Create the repeat rule section
		String[] rrules = { "No", "Daily", "Weekly", "Monthly", "Yearly" };
		this.rrule = new JComboBox(rrules);
		if (target != null && target.isRepeating()) {
			int field = target.getRepeatField();
			if (field == Calendar.DAY_OF_YEAR) {
				this.rrule.setSelectedIndex(1);
			} else if (field == Calendar.WEEK_OF_YEAR) {
				this.rrule.setSelectedIndex(2);
			} else if (field == Calendar.MONTH) {
				this.rrule.setSelectedIndex(3);
			} else if (field == Calendar.YEAR) {
				this.rrule.setSelectedIndex(4);
			}

			this.interval = new JTextField("" + target.getInterval());
			GregorianCalendar exp = target.getExpiration();
			if (exp != null) {
				this.expiry = new JTextField("" + TCalParser.extractDate(exp));
			} else {
				this.expiry = new JTextField();
			}
		} else {
			this.interval = new JTextField();
			this.expiry = new JTextField();
		}
		JLabel repeat = new JLabel("Repeating?");
		JPanel repeating = new JPanel(new GridLayout(3, 2));
		JLabel interval = new JLabel("Interval:");
		JLabel expiration = new JLabel("Until:");
		repeating.add(repeat);
		repeating.add(this.rrule);
		repeating.add(interval);
		repeating.add(this.interval);
		repeating.add(expiration);
		repeating.add(this.expiry);
		this.add(repeating);

		// Create the bottom default button array (Apply, Cancel)
		JPanel done = new JPanel(new GridLayout(1, 2));
		APPLY_BUTTON = new JButton("Create");
		APPLY_BUTTON.addActionListener(this);
		CANCEL_BUTTON = new JButton("Cancel");
		CANCEL_BUTTON.addActionListener(this);
		done.add(APPLY_BUTTON);
		done.add(CANCEL_BUTTON);
		this.add(done);

		// If this is a preexisting event, add Edit and Delete buttons as well.
		if (target != null) {
			JPanel change = new JPanel(new GridLayout(1, 2));
			EDIT_BUTTON = new JButton("Change");
			EDIT_BUTTON.addActionListener(this);
			DELETE_BUTTON = new JButton("Delete");
			DELETE_BUTTON.addActionListener(this);
			change.add(EDIT_BUTTON);
			change.add(DELETE_BUTTON);
			this.add(change);
		}
	}

	// Button event handler
	@Override
	public void actionPerformed(ActionEvent action) {
		Object source = action.getSource();
		// Handle the "Apply" button.
		if (APPLY_BUTTON.equals(source)) {
			this.eventCreator();
			return;
		}
		// Cancel button.
		if (CANCEL_BUTTON.equals(source)) {
			this.master.flushToolPane();
			return;
		}
		// Edit button
		if (EDIT_BUTTON.equals(source)) {
			this.editEvent();
			return;
		}

		// Delete button
		if (DELETE_BUTTON.equals(source)) {
			this.master.deleteEvent(this.target);
			this.master.flushToolPane();
			return;
		}

	}

	// Reads and parses the various fields and tries to construct an event from
	// them.
	private void eventCreator() {
		try {
			GregorianCalendar start = TCalParser.parseToDate(this.startDate
					.getText(), this.startTime.getText());
			GregorianCalendar end = TCalParser.parseToDate(this.endDate
					.getText(), this.endTime.getText());
			if (start == null || end == null) {
				new JErrorFrame(
						"One of the fields was incorrectly filled, no new event was created");
				return;
			}

			if (start.after(end)) {
				new JErrorFrame("The start cannot be after the end");
				return;
			}

			MeetingEvent event = new MeetingEvent(start, end);

			event.setCategory(this.category.getText());

			// Check the repeating rule.
			int position = this.rrule.getSelectedIndex();
			if (position != 0) {
				int field;
				int interval = Integer.parseInt(this.interval.getText());
				GregorianCalendar expiry = TCalParser.parseToDate(this.expiry
						.getText(), "00:00");

				// Check that the expiry is after the end.
				if (expiry != null && expiry.before(end)) {
					new JErrorFrame("The event cannot expire before it ends.");
				}

				if (position == 1) {
					field = Calendar.DAY_OF_YEAR;
				} else if (position == 2) {
					field = Calendar.WEEK_OF_YEAR;
				} else if (position == 3) {
					field = Calendar.MONTH;
				} else {
					field = Calendar.YEAR;
				}

				event.setRepeat(field, interval, expiry);
			}

			int pSelection = this.priority.getSelectedIndex();
			switch (pSelection) {
			case 1:
				event.setPriority(9);
				break;
			case 2:
				event.setPriority(5);
				break;
			case 3:
				event.setPriority(1);
				break;
			}
			event.setUID(TCalendar.UID);

			this.master.addEvent(event);
			this.master.flushToolPane();
		} catch (NumberFormatException num) {
			new JErrorFrame("The interval value must be an integer");
			return;
		} catch (IllegalArgumentException dateProb) {
			new JErrorFrame("Incorrect date/time values");
			return;
		}

	}

	// Attempts to change the target event to suit the input parameters.
	private void editEvent() {
		try {
			GregorianCalendar start = TCalParser.parseToDate(this.startDate
					.getText(), this.startTime.getText());
			GregorianCalendar end = TCalParser.parseToDate(this.endDate
					.getText(), this.endTime.getText());
			if (start == null || end == null) {
				new JErrorFrame(
						"One of the date fields was incorrectly filled, the event was not changed");
				return;
			}

			if (start.after(end)) {
				new JErrorFrame("The start cannot be after the end");
				return;
			}

			this.target.setNewStartEnd(start, end);

			this.target.setCategory(this.category.getText());

			// Check the repeating rule.
			int position = this.rrule.getSelectedIndex();
			if (position != 0) {
				int field;
				int interval = Integer.parseInt(this.interval.getText());
				GregorianCalendar expiry = TCalParser.parseToDate(this.expiry
						.getText(), "00:00");

				// Check that the expiry is after the end.
				if (expiry != null && expiry.before(end)) {
					new JErrorFrame("The event cannot expire before it ends.");
				}
				if (position == 1) {
					field = Calendar.DAY_OF_YEAR;
				} else if (position == 2) {
					field = Calendar.WEEK_OF_YEAR;
				} else if (position == 3) {
					field = Calendar.MONTH;
				} else {
					field = Calendar.YEAR;
				}

				this.target.setRepeat(field, interval, expiry);
			} else {
				target.endRepeat();
			}

			int pSelection = this.priority.getSelectedIndex();
			switch (pSelection) {
			case 1:
				this.target.setPriority(9);
				break;
			case 2:
				this.target.setPriority(5);
				break;
			case 3:
				this.target.setPriority(1);
				break;
			}

			this.master.flushToolPane();
			this.master.refreshWeek();
		} catch (NumberFormatException num) {
			new JErrorFrame("The interval value must be an integer");
			return;
		} catch (IllegalArgumentException dateProb) {
			new JErrorFrame("Incorrect date/time values");
			return;
		}
	}
}
