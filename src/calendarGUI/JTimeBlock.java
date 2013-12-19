package calendarGUI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.PriorityQueue;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import calendar.Event;

public class JTimeBlock extends JPanel {

	private static final long serialVersionUID = 1L;

	public static Color EMPTY_COLOR = null;
	public static Color NO_PRIORITY = Color.CYAN;
	public static Color LOW_PRIORITY = Color.GREEN;
	public static Color MEDIUM_PRIORITY = Color.YELLOW;
	public static Color HIGH_PRIORITY = Color.ORANGE;
	public static Color OVERLAP_BORDERS = Color.RED;

	private JTimeBlock previous; // A reference to the previous block
	private JTimeBlock next; // A reference to the next block
	ArrayList<Event> events; // A list of all events that are valid in this
								// timeblock
	public PriorityQueue<Integer> priorities; // A priority queue holding the
												// priorities of the events in
												// this block.

	/**
	 * Constructs a new JTimeBlock that comes after the block given as a
	 * parameter.
	 * 
	 * @param previous
	 *            The JTimeBlock before this one.
	 */
	public JTimeBlock(JTimeBlock previous) {
		this.previous = previous;
		if (this.previous != null) {
			this.previous.next = this;
		}
		this.next = null;
		this.events = new ArrayList<Event>();
		this.priorities = new PriorityQueue<Integer>(3);
	}

	/**
	 * Adds an event to this block and to all subsequent blocks where it is
	 * valid.
	 * 
	 * @param toAdd
	 *            The Event to be added.
	 * @param duration
	 *            The number of blocks that this event will be valid in.
	 */
	public void addEvent(Event toAdd, int duration) {
		int priority = toAdd.getPriority();
		this.events.add(toAdd);
		// If the priority is 0, add it to the queue as a 10 for comparison
		// purposes.
		if (priority != 0) {
			this.priorities.add(new Integer(priority));
		} else {
			this.priorities.add(new Integer(10));
		}
		this.color();
		duration--;
		if (this.next != null && duration > 0) {
			this.next.addEvent(toAdd, duration);
		}
	}

	/**
	 * Colors this block. The color is decided by the highest priority of the
	 * events valid in the currents block. If more than one event is valid in
	 * the same block, the sides are given red borders.
	 */
	public void color() {
		// If the time block is occupied, mark the sides red.
		if (this.events.size() > 1) {
			this.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 2,
					OVERLAP_BORDERS));
		}
		if (this.priorities.isEmpty()) {
			this.setBackground(EMPTY_COLOR);
		} else {
			int top = this.priorities.peek();
			if (top > 5 && top < 10) { // Low priority.
				this.setBackground(LOW_PRIORITY);
			} else if (top == 5) { // Medium priority.
				this.setBackground(MEDIUM_PRIORITY);
			} else if (top > 0 && top < 5) { // High priority.
				this.setBackground(HIGH_PRIORITY);
			} else { // Default priority
				this.setBackground(NO_PRIORITY);
			}
		}
	}

	/**
	 * Clears the events and priorities from this block and sets the background
	 * to the default empty color.
	 */
	public void flush() {
		this.setBorder(null);
		this.setBackground(EMPTY_COLOR);
		this.events.clear();
		this.priorities.clear();
	}

	/**
	 * Returns the list of Events in this block.
	 * 
	 * @return The {@link ArrayList} of the Events in this block.
	 */
	public ArrayList<Event> getEventsInBlock() {
		return this.events;
	}

	/**
	 * Removes an event from the block.
	 * 
	 * @param e
	 *            The {@link Event} to be removed.
	 */
	public void remove(Event e) {
		if (!this.events.isEmpty()) {
			this.events.remove(e);
			this.priorities.remove(e.getPriority());
		}
	}

}
