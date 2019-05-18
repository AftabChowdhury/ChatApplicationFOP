package client; 

import java.awt.BorderLayout; 
import java.awt.Container; 
import java.awt.Font; 

import java.awt.GridLayout; 
import java.awt.Insets; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.rmi.RemoteException; 
import java.util.Scanner; 
//import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler; 
import java.util.logging.SimpleFormatter; 
import java.util.logging.Logger; 

import javax.swing.BorderFactory; 
import javax.swing.ButtonGroup; 
import javax.swing.DefaultListModel; 

import javax.swing.JButton; 
import javax.swing.JFrame; 
import javax.swing.JLabel; 
import javax.swing.JList; 
import javax.swing.JOptionPane; 
import javax.swing.JPanel; 
import javax.swing.JPasswordField; 
import javax.swing.JRadioButton; 
import javax.swing.JScrollPane; 
import javax.swing.JTextArea; 
import javax.swing.JTextField; 
import javax.swing.ListSelectionModel; 
import javax.swing.UIManager; 
import javax.swing.UIManager.LookAndFeelInfo; 
import javax.swing.border.Border; 

//import com.sun.istack.internal.logging.Logger;
//import com.sun.net.ssl.internal.www.protocol.https.Handler;






import java.awt.*; 
import java.io.IOException; 

/**
 * TODO description
 */
public   class  ClientRMIGUI  extends JFrame  implements ActionListener {
	
	
	private static final long serialVersionUID = 1L;

		
	private JPanel textPanel, inputPanel;

	
	private JTextField textField;

	
	private String name, message, messageToLog;

	
	private Font meiryoFont = new Font("Meiryo", Font.PLAIN, 14);

	
	private Border blankBorder = BorderFactory.createEmptyBorder(10,10,20,10);

	//top,r,b,l
	private ChatClient3 chatClient;

	
    private JList<String> list;

	
    private DefaultListModel<String> listModel;

	
    
    protected JTextArea textArea, userArea;

	
    protected JFrame frame;

	
    protected JButton privateMsgButton, startButton, sendButton, passwordButton;

	
    protected JPanel clientPanel, userPanel;

	
    protected JPasswordField passwordField;

	 
    private SpamFilterAll objectSpamFilterAll;

	

	/**
	 * Main method to start client GUI app.
	 * @param args
	 */
	public static void main(String args[]){
		//set the look and feel to 'Nimbus'
		try{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
				if("Nimbus".equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch(Exception e){
			}
		new ClientRMIGUI();
		}

	//end main
	
	
	/**
	 * GUI Constructor
	 */
	public ClientRMIGUI(){
			
		frame = new JFrame("Client Chat Console");	
		objectSpamFilterAll = new SpamFilterAll();
	
		//-----------------------------------------
		
		
		 //* intercept close method, inform server we are leaving
		 //* then let the system exit.
		 
		 
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        
		    	if(chatClient != null){
			    	try {
			    		message = rot13("Bye all, I am leaving");
			        	sendMessage(message);			        	
			        	chatClient.serverIF.leaveChat(name);
					} catch (RemoteException e) {
						e.printStackTrace();
					}		        	
		        }
		        System.exit(0);  
		    }   
		});
		//-----------------------------------------
		//remove window buttons and border frame
		//to force user to exit on a button
		//- one way to control the exit behaviour
	    //frame.setUndecorated(true);
	    //frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
	
		Container c = getContentPane();
		JPanel outerPanel = new JPanel(new BorderLayout());
		
		outerPanel.add(getInputPanel(), BorderLayout.CENTER);
		outerPanel.add(getTextPanel(), BorderLayout.NORTH);
		
		c.setLayout(new BorderLayout());
		c.add(outerPanel, BorderLayout.CENTER);
		c.add(getUsersPanel(), BorderLayout.WEST);

		frame.add(c);
		frame.pack();
		frame.setAlwaysOnTop(true);
		frame.setLocation(150, 150);
		textField.requestFocus();
	
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	
	
	
	
	/**
	 * Method to set up the JPanel to display the chat text
	 * @return
	 */
	public JPanel getTextPanel(){
		String welcome = "Welcome enter your name and press Start to begin\n";
		textArea = new JTextArea(welcome, 14, 34);
		textArea.setMargin(new Insets(10, 10, 10, 10));
		textArea.setFont(meiryoFont);
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setForeground(Color.BLUE);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textPanel = new JPanel();
		textPanel.add(scrollPane);
	
		textPanel.setFont(new Font("Meiryo", Font.PLAIN, 14));
		return textPanel;
	}

	
	
	/**
	 * Method to build the panel with input field
	 * @return inputPanel
	 */
	public JPanel getInputPanel(){
		inputPanel = new JPanel(new GridLayout(1, 1, 5, 5));
		inputPanel.setBorder(blankBorder);	
		textField = new JTextField();
		textField.setFont(meiryoFont);
		inputPanel.add(textField);
		return inputPanel;
	}

	

	/**
	 * Method to build the panel displaying currently connected users
	 * with a call to the button panel building method
	 * @return
	 */
	public JPanel getUsersPanel(){
		
		userPanel = new JPanel(new BorderLayout());
		String  userStr = " Current Users      ";
		
		JLabel userLabel = new JLabel(userStr, JLabel.CENTER);
		userPanel.add(userLabel, BorderLayout.NORTH);	
		userLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));

		String[] noClientsYet = {"No other users"};
		setClientPanel(noClientsYet);

		clientPanel.setFont(meiryoFont);
		userPanel.add(makePasswordPanel(), BorderLayout.NORTH);
		userPanel.add(makeButtonPanel(), BorderLayout.SOUTH);
		//userPanel.add(radioButtonPanel(), BorderLayout.LINE_END);
		userPanel.setBorder(blankBorder);

		return userPanel;		
	}

	

	/**
	 * Populate current user panel with a 
	 * selectable list of currently connected users
	 * @param currClients
	 */
    public void setClientPanel(String[] currClients) {  	
    	clientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();
        
        for(String s : currClients){
        	listModel.addElement(s);
        }
        if(currClients.length > 1){
        	privateMsgButton.setEnabled(true);
        }
        
        //Create the list and put it in a scroll pane.
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(meiryoFont);
        JScrollPane listScrollPane = new JScrollPane(list);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }

	
	public JPanel makeButtonPanel  () {	
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

	
	
	public JPanel makePasswordPanel() {		
		  passwordField = new JPasswordField(10);
		  JLabel label = new JLabel("Enter the password: ");
	      label.setLabelFor(passwordField);
	      passwordField.setEnabled(false);
	      //Lay out everything.
	      JPanel textPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
	      textPane.add(label);
	      textPane.add(passwordField);
	      
	      return textPane;
	}

	
	
	public JPanel radioButtonPanel() {	
		JRadioButton radioBtnRot13 = new JRadioButton("ROT13");
        JRadioButton radioBtnExchange2Letter = new JRadioButton("Exchanging of first two letters");
        radioBtnRot13.setSelected(true);
 
        ButtonGroup group = new ButtonGroup();
        group.add(radioBtnRot13);
        group.add(radioBtnExchange2Letter);
        
        JPanel radioPanel = new JPanel(new BorderLayout());
        radioPanel.add(radioBtnRot13, BorderLayout.NORTH);
        radioPanel.add(radioBtnExchange2Letter, BorderLayout.CENTER);
        return radioPanel;
	}

	
	
	/**
	 * Action handling on the buttons
	 */
	@Override
	public void actionPerformed(ActionEvent e){

		try {
			//get connected to chat service
			if(e.getSource() == startButton){
				name = textField.getText();				
				if(name.length() != 0){
					frame.setTitle(name + "'s console ");
					/*textField.setText("");
					textArea.append("username : " + name + " connecting to chat...\n");							
					getConnected(name);*/
					//if(!chatClient.connectionProblem){
						startButton.setEnabled(false);
						//sendButton.setEnabled(true);
						passwordButton.setEnabled(true);
						passwordField.setEnabled(true);
						passwordField.requestFocus();
					//	}
				}
				else{
					JOptionPane.showMessageDialog(frame, "Enter your name to Start");
				}
			}

			//get text and clear textField
			if(e.getSource() == sendButton){
				message = textField.getText();
				textField.setText("");
				message = objectSpamFilterAll.spamFilter(message);
				message = rot13(message);
				sendMessage(message);
				textField.requestFocus();
				saveinLogFile(message);			    
				System.out.println("Sending message : " + message);
			}
			
			//send a private message, to selected users
			if(e.getSource() == privateMsgButton){
				int[] privateList = list.getSelectedIndices();
				
				for(int i=0; i<privateList.length; i++){
					System.out.println("selected index :" + privateList[i]);
				}
				message = textField.getText();
				textField.setText("");
				message = objectSpamFilterAll.spamFilter(message);
				message = "[PM from " + name + "] :" + message;
				message = rot13(message);
				sendPrivate(privateList);
				saveinLogFile(message);
			}
			//verify password
			if(e.getSource() == passwordButton){
				String pass = "123456";
				char[] input = passwordField.getPassword();
				String userpassword = new String(input);
				 if(userpassword.equals(pass)){
					 JOptionPane.showMessageDialog(frame,
			         "Success! You typed the right password.");
					 sendButton.setEnabled(true);
					 privateMsgButton.setEnabled(true);
					 passwordButton.setEnabled(false);
					 passwordField.setText("");
					 passwordField.setEnabled(false);
					 name = textField.getText();
					 textField.setText("");
					 textField.requestFocus();
				     textArea.append("username : " + name + " connecting to chat...\n");							
				     getConnected(name);
				 }	
				 else {
		                JOptionPane.showMessageDialog(frame,
                        "Invalid password. Try again.",
                        "Error Message",
                        JOptionPane.ERROR_MESSAGE);
		         }			
				
			}
			
		}
		catch (RemoteException remoteExc) {			
			remoteExc.printStackTrace();	
		}
		
	}

	//end actionPerformed

	// --------------------------------------------------------------------
	
	/**
	 * Send a message, to be relayed to all chatters
	 * @param chatMessage
	 * @throws RemoteException
	 */
	private void sendMessage(String chatMessage) throws RemoteException {
		chatClient.serverIF.updateChat(name, chatMessage);
	}

	
	

	/**
	 * Send a message, to be relayed, only to selected chatters
	 * @param chatMessage
	 * @throws RemoteException
	 */
	private void sendPrivate(int[] privateList) throws RemoteException {
		String privateMessage = message + "\n";
		chatClient.serverIF.sendPM(privateList, privateMessage);
	}

	
	
	/**
	 * Make the connection to the chat server
	 * @param userName
	 * @throws RemoteException
	 */
	//private void getConnected(String userName) throws RemoteException{
	private void getConnected(String userName){
		//remove whitespace and non word characters to avoid malformed url
		String cleanedUserName = userName.replaceAll("\\s+","_");
		cleanedUserName = userName.replaceAll("\\W+","_");
		try {		
			chatClient = new ChatClient3(this, cleanedUserName);
			chatClient.startClient();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	
	/* Implement ROT13 for encryption*/
	 public static String rot13(String value) {

	        char[] values = value.toCharArray();
	        for (int i = 0; i < values.length; i++) {
	            char letter = values[i];

	            if (letter >= 'a' && letter <= 'z') {
	                // Rotate lowercase letters.

	                if (letter > 'm') {
	                    letter -= 13;
	                } else {
	                    letter += 13;
	                }
	            } else if (letter >= 'A' && letter <= 'Z') {
	                // Rotate uppercase letters.

	                if (letter > 'M') {
	                    letter -= 13;
	                } else {
	                    letter += 13;
	                }
	            }
	            values[i] = letter;
	        }
	        // Convert array to a new String.
	        return new String(values);
	    }

	
	 /* Implement Exchange 2 letter for encryption*/
	 public static String exchange2Letter(String value) {
		 char[] values = value.toCharArray();
		 if(values.length > 1){
		            char temp = values[0];
		            values[0] = values[1];
		            values[1] = temp;		 
		 }
	        
	        return new String(values);
	 }

	
	 /* Create log file*/
	 public void saveinLogFile(String message) {
		////create log file/////////

		    try {  

		        // This block configure the logger with handler and formatter 
		    	Logger logger = Logger.getLogger("MyLog");  
		        FileHandler fh; 
		        fh = new FileHandler("MyLogFile.log");  
		        logger.addHandler(fh);
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter);  

		        // the following statement is used to log any messages
		        messageToLog = rot13(message);
		        messageToLog = name + " : " + messageToLog + "\n";
		        logger.info(messageToLog);  

		    } catch (SecurityException e1) {  
		        e1.printStackTrace();  
		    } catch (IOException e1) {  
		        e1.printStackTrace();  
		    }  
	 }


}
