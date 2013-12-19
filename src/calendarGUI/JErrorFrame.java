package calendarGUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

/**
 * A small class representing a popupframe that displays a message and has an "OK" button on the bottom.
 * @author aisopuro@tkk
 *
 */

public class JErrorFrame extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private int LINE_LENGTH = 30;

	/**
	 * Constructs an errorframe and displays it.
	 * @param errorMessage The message to be displayed
	 */
	public JErrorFrame(String errorMessage) {

		this.setTitle("Whoops");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JTextPane message = new JTextPane();
		message.setEditable(false);
		
		StringBuilder builder = new StringBuilder(40);
		// Append the message.
		while (errorMessage.length() > this.LINE_LENGTH) {
			builder.append(errorMessage.substring(0, this.LINE_LENGTH - 1));
			char current;
			int i;
			for (i = this.LINE_LENGTH - 1; i < errorMessage.length(); i++) {
				current = errorMessage.charAt(i);
				builder.append(current);
				if (current == ' ') {
					builder.append("\n");
					break;
				}
			}
			errorMessage = errorMessage.substring(i);
		}
		builder.append(errorMessage);
		message.setText(builder.toString());
		panel.add(message);
		
		JButton ok = new JButton("OK");
		ok.setPreferredSize(new Dimension(50, 25));
		ok.addActionListener(this);
		panel.add(ok);
		
		this.add(panel);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	// Handle the OK button.
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.dispose(); //Only one button to listen to.
		
	}

}
