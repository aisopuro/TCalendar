package calendarGUI;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import calendar.Event;

/**
 * A JEventPreview component offers a small preview of an {@link Event}. It
 * shows the category, starting and ending times and allows the user to either
 * edit or delete the event.
 * 
 * @author aisopuro@tkk
 * 
 */

public class JEventPreview extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	// Action Commands
	private static JButton EDIT_BUTTON;
	private static JButton DELETE_BUTTON;

	private TCalGUI master;
	private Event event;

	/**
	 * Constructs a new preview with the specified master program and target
	 * {@link Event}.
	 * 
	 * @param master
	 *            The {@link TCalGUI} this preview belongs to.
	 * @param event
	 *            The {@link Event} to be previewed.
	 */

	public JEventPreview(TCalGUI master, Event event) {
		this.master = master;
		this.event = event;
		
		//Set the background color according to the priority.
		int priority = event.getPriority();
		if (priority < 5 && priority > 0) {
			this.setBackground(Color.ORANGE);
		} else if (priority == 5) {
			this.setBackground(Color.YELLOW);
		} else if (priority > 5 && priority < 10) {
			this.setBackground(Color.GREEN);
		} else {
			this.setBackground(Color.CYAN);
		}

		this.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel category = new JLabel(this.event.getCategory());
		JLabel duration = new JLabel(this.event.getTextDuration());

		this.add(category);
		this.add(duration);

		JPanel buttons = new JPanel(new GridLayout(1, 2));
		EDIT_BUTTON = new JButton("Edit");
		EDIT_BUTTON.addActionListener(this);
		DELETE_BUTTON = new JButton("Delete");
		DELETE_BUTTON.addActionListener(this);
		buttons.add(EDIT_BUTTON);
		buttons.add(DELETE_BUTTON);
		this.add(buttons);
	}

	// Handle the button presses
	@Override
	public void actionPerformed(ActionEvent action) {
		Object source = action.getSource();
		// Handle the Edit button
		if (EDIT_BUTTON.equals(source)) {
			this.master.flushToolPane();
			this.master.toolPaneEventSetup(this.event);
		}
		// Handle the delete button
		if (DELETE_BUTTON.equals(source)) {
			this.master.deleteEvent(this.event);
			this.master.removePreview(this);
		}

	}
}
