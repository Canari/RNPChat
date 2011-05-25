package client.main;


import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;

import client.tcp.TCPReceiver;
import client.tcp.TCPTransmitter;
import client.udp.UDPReceiver;
import client.udp.UDPTransmitter;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public final class ChatClient extends JFrame {

	private static final long serialVersionUID = 1L;
	public static int udpPort = 50001;
	public static int tcpPort = 50000;
	
	private JPanel jContentPane = null;
	private JList clientList = null;
	private JTextField inputTextField = null;
	private JTextArea messagesTextArea = null;
	private JScrollPane jScrollPane = null;
	private JButton sendButton = null;
	
	private UDPTransmitter messageTransmitter = null;  //  @jve:decl-index=0:
	private UDPReceiver messageReceiver = null;  //  @jve:decl-index=0:
	
	private TCPTransmitter tcpTransmitter = null;
	private TCPReceiver tcpReceiver = null;  //  @jve:decl-index=0:
	
	private DatagramSocket udpSocket = null;
	private Socket tcpSocket = null;  //  @jve:decl-index=0:

	private String servername = "";  //  @jve:decl-index=0:
	private String username = "";

	/**
	 * This is the default constructor
	 */
	public ChatClient() {
		super();
		initialize();
		ClientMonitor.initialize(this);

		try {
			getMessageReceiver().start();
		} catch (SocketException e) {
			addErrorMessage(e.getMessage());
		}
	}
	
	protected void addErrorMessage(String message) {
		addTextMessage("Fehler: " + message + "\n");
	}
	
	protected void addTextMessage(String text) {
		messagesTextArea.setText(messagesTextArea.getText() + text);
	}
	
	private boolean checkServerName() {
		getTcpSocket();
		return (tcpSocket != null);
	}
	
	private void consumeInput() {
		try {
			getMessageTransmitter().sendMessage(getFormattedInput());
		} catch (IOException e) {
			addErrorMessage(e.getMessage());
		}
		
		TextFieldLimit.triggerState();
		addTextMessage(getFormattedInput());
	}
	
	private void consumeServernameInput() {
		if (checkServerName()) {
			TextFieldLimit.triggerState();
			servername = inputTextField.getText();
		}
	}
	
	private void consumeUsernameInput() {
		try {
			if (getTcpTransmitter().sendUsername(inputTextField.getText())) {
				TextFieldLimit.triggerState();
				username = inputTextField.getText();
				
				getTcpReceiver().start();
			}
		} catch (SocketException e) {
			addErrorMessage(e.getMessage());
		} catch (IOException e) {
			addErrorMessage(e.getMessage());
		}
	}

	/**
	 * This method initializes clientList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getClientList() {
		if (clientList == null) {
			clientList = new JList();
			
			clientList.setToolTipText("Liste der Clients die aktuell im Chat sind.");
			clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			clientList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			clientList.setBackground(Color.white);
			clientList.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			
			int width = 150;
			int height = 442;
			int x = 0;
			int y = 0;
			clientList.setBounds(new Rectangle(x, y, width, height));
		}
		
		return clientList;
	}

	private String getFormattedInput() {
		return username + ": " + inputTextField.getText() + "\n";
	}

	/**
	 * This method initializes inputTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getInputTextField() {
		if (inputTextField == null) {
			inputTextField = new JTextField();
			
			inputTextField.setBackground(Color.white);
			inputTextField.setDocument(new TextFieldLimit());
			inputTextField.setToolTipText("Hier können Sie einen Text eingeben den Sie senden möchten.");
			
			int width = messagesTextArea.getWidth();
			int height = 30;
			int x = messagesTextArea.getX();
			int y = (int)messagesTextArea.getBounds().getMaxY() + 15;
			inputTextField.setBounds(new Rectangle(x, y, width, height));
		}
		
		return inputTextField;
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);

			jContentPane.add(getClientList(), null);
			jContentPane.add(getJScrollPane(), null);
			jContentPane.add(getInputTextField(), null);
			jContentPane.add(getSendButton(), null);
		}
		
		return jContentPane;
	}
	
	/**
	 * This method initializes inputTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane(getMessagesTextArea());
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			int width = messagesTextArea.getWidth();
			int height = messagesTextArea.getHeight();
			int x = messagesTextArea.getX();
			int y = messagesTextArea.getY();
			jScrollPane.setBounds(new Rectangle(x, y, width, height));
		}
		
		return jScrollPane;
	}

	private UDPReceiver getMessageReceiver() throws SocketException {
		if (messageReceiver == null) {
			messageReceiver = new UDPReceiver(getUdpSocket());
		}
		
		return messageReceiver;
	}

	/**
	 * This method initializes inputTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getMessagesTextArea() {
		if (messagesTextArea == null) {
			messagesTextArea = new JTextArea();
			
			messagesTextArea.setLineWrap(true);
			messagesTextArea.setWrapStyleWord(true);
			messagesTextArea.setText("RNP Chat Client v1.0 von Vassilios Stavrou, Philip Rose und Jan Kuffer\n\nGeben Sie den Servernamen an\n");
			messagesTextArea.setBackground(Color.white);
			messagesTextArea.setToolTipText("Übersicht über gesendete Nachrichten.");
			
			int width = 472;
			int height = 325;
			int x = clientList.getWidth() + 2;
			int y = 0;
			messagesTextArea.setBounds(new Rectangle(x, y, width, height));
		}
		
		return messagesTextArea;
	}
	
	private UDPTransmitter getMessageTransmitter() {
		if (messageTransmitter == null) {
			try {
				messageTransmitter = new UDPTransmitter(getUdpSocket());
			} catch (SocketException e) {
				addTextMessage("Fehler: " + e.getMessage());
			}
		}
		
		return messageTransmitter;
	}
	
	/**
	 * This method initializes sendButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSendButton() {
		if (sendButton == null) {
			sendButton = new JButton();
			
			sendButton.setText("Servernamen festlegen");
				
			int width = inputTextField.getWidth();
			int height = 50;
			int x = messagesTextArea.getX();
			int y = (int)inputTextField.getBounds().getMaxY() + 15;
			sendButton.setBounds(new Rectangle(x, y, width, height));
			
			sendButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (!inputTextField.getText().equals("")) {
						if (TextFieldLimit.getState() == 2) {
							consumeInput();
						}
						else if (TextFieldLimit.getState() == 1) {
							consumeUsernameInput();
						}
						else {
							consumeServernameInput();
						}
						
						updateLayout();
					}
				}
			});
		}
		
		return sendButton;
	}
	
	private TCPReceiver getTcpReceiver() throws SocketException {
		if (tcpReceiver == null) {
			tcpReceiver = new TCPReceiver(getTcpSocket());
		}
		
		return tcpReceiver;
	}
	
	private Socket getTcpSocket() {
		if (tcpSocket == null) {
			try {
				tcpSocket = new Socket(InetAddress.getByName(servername), tcpPort);
			} catch (SocketException e) {
				addErrorMessage(e.getMessage());
			} catch (UnknownHostException e) {
				addErrorMessage(e.getMessage());
			} catch (IOException e) {
				addErrorMessage(e.getMessage());
			}
		}
		
		return tcpSocket;
	}
	
	private TCPTransmitter getTcpTransmitter() throws SocketException {
		if (tcpTransmitter == null) {
			tcpTransmitter = new TCPTransmitter(getTcpSocket());
		}
		
		return tcpTransmitter;
	}
	
	private DatagramSocket getUdpSocket() {
		if (udpSocket == null) {
			try {
				udpSocket = new DatagramSocket(udpPort);
			} catch (SocketException e) {
				addErrorMessage(e.getMessage());
			}
		}
		
		return udpSocket;
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {		
		this.setSize(640, 480);
		this.setContentPane(getJContentPane());
		this.setTitle("RNP Chat Client");
		this.setLocationByPlatform(true);
	}
	
	protected void updateClientList(Object[] clientnames) {
		clientList.setListData(clientnames);
	}
	
	private void updateLayout() {
		switch (TextFieldLimit.getState()) {
			case 0:
				messagesTextArea.setText("RNP Chat Client v1.0 von Vassilios Stavrou, Philip Rose und Jan Kuffer\n\nGeben Sie den Servernamen an\n");
				sendButton.setText("Servernamen festlegen");
				break;
			case 1:
				addTextMessage("Servernamen erfolgreich festgelegt: " + servername + "\n\n");
				addTextMessage("Geben Sie ihren Benutzernamen an\n");
				sendButton.setText("Benutzernamen festlegen");
				break;
			case 2:
				addTextMessage("Benutzernamen erfolgreich festgelegt: " + username + "\n\n");
				sendButton.setText("Nachricht senden");
				break;
		}
		
		inputTextField.setText("");
	}

	public void signOut() {
		TextFieldLimit.triggerState();
		
		try {
			getTcpReceiver().triggerToxic();
			getMessageReceiver().triggerToxic();
		} catch (SocketException e) {
			addErrorMessage(e.getMessage());
		}
		
		updateLayout();
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"