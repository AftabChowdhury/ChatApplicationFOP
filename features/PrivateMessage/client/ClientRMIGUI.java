package client;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * TODO description
 */
public class ClientRMIGUI {
	public JPanel makeButtonPanel() {	
		sendButton = new JButton("Send ");
		sendButton.addActionListener(this);
		sendButton.setEnabled(false);

        privateMsgButton = new JButton("Send PM");
        privateMsgButton.addActionListener(this);
        privateMsgButton.setEnabled(false);
        privateMsgButton.setVisible(true);
        
        passwordButton = new JButton("Verify Password");
        passwordButton.addActionListener(this);
        passwordButton.setEnabled(false);
		
		startButton = new JButton("Start ");
		startButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
		buttonPanel.add(privateMsgButton);
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(startButton);
		buttonPanel.add(sendButton);
		buttonPanel.add(passwordButton);
		
		return buttonPanel;
	}
}