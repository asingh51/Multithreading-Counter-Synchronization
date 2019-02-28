//Abhay Singh
//1001669333

/**
* The ClientConnection program implements an application that
* starts the client window.
* 
* @author  Abhay Singh [1001669333]
* @version 1.0 
*/

package com.demo.socketdemo;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/*Class ClientConnection
 *
 *Its for Client Connection to initiate the client side programming.*/
public class ClientConnection {
	private int portNo = 8005;
	private String ipAddress = "localhost";
	private static DataOutputStream dataOutputStream;
	private static DataInputStream dataInputStream;
	private Socket clientsocket;
	private static String clientName;
	/*Boolean Flag to store whether the user is connected.*/
	private boolean flag = false;
	private final static String host = "Host: localhost";
	private final static String userAgent = "User-Agent: MultiChat/2.0";
	private final static String contentType = "Content-Type: text/html";
	private final static String contentLength = "Content-Length: ";
	private final static String date = "Date: ";
	private JTextField jTextField;
	private static JTextArea notificationArea;
	private JFrame jFrame;
	private JScrollPane jscrollPane;

	/*Default constructor 
	 * 
	 * Provides/Creates the Object for initialization of class.*/
	public ClientConnection() {
	}
	
	/*Parameterized construcor 
	 * 
	 * @param name of Client Window
	 *Provides/Creates for window initialization of Client*/
	public ClientConnection(String clientWindow) {
		constructWindow(clientWindow);
	}

	/* Method name: constructWindow 
	 * 
	 * @param client window name
	 * It will create a Swing based Jframe and put all the components and add listerners to components.*/
	private void constructWindow(String clientWindow) {
		jFrame = new JFrame(clientWindow);															// create a frame.
		jFrame.setBounds(500, 0, 490, 490);
		jFrame.getContentPane().setLayout(null);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);									// Client will close the Frame and counting
		
		JLabel userLabel = new JLabel("User Name");												// Create label
		userLabel.setBounds(20, 33, 195, 14);
		jFrame.getContentPane().add(userLabel);			
		
		jTextField = new JTextField();															// Text field to enter the name
		jTextField.setBounds(115, 31, 188, 20);
		jFrame.getContentPane().add(jTextField);
		jTextField.setColumns(10);
		
		JButton jButtonConnect = new JButton("Connect");										// Button to Send the Username to Server
		jButtonConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			if(flag == true) {																	// flag to check if user is connected
					JOptionPane.showMessageDialog(null, "You are already connected !");
				}
				else if(flag == false) {
					clientName = jTextField.getText();
					jFrame.setTitle("CLIENT - "+clientName);									// Rename the client Windoe with the name
					
					if(clientName.equals(null)||clientName.trim().isEmpty())
					{
						JOptionPane.showMessageDialog(null, "Please enter an username to connect to server! ");
					}
					else {																			// If all okay, start the connection
						startClientConnection();
						SendRandomTimeBetween3and10();											// Genrrate and send random number between 5-15 to server
					}
				}
			}
		});
		jButtonConnect.setBounds(350, 31, 89, 23);
		jFrame.getContentPane().add(jButtonConnect);
		
		jscrollPane = new JScrollPane();														// Set the scrollpane, otherwise the 
		jscrollPane.setBounds(0, 75, 490, 350);
		jFrame.getContentPane().add(jscrollPane);
		
		notificationArea = new JTextArea();														// Text area to print the time wited by server
		jscrollPane.setViewportView(notificationArea);
		notificationArea.setEditable(false);
		
		JButton jButtonExit = new JButton("Exit");												//Button for Exit functionality
		jButtonExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				System.exit(0);
			}
		});
		jButtonExit.setBounds(200, 440, 89, 23);
		jFrame.getContentPane().add(jButtonExit);
	}
	
	/*Method name startClientConnection 
	 *
	 *
	 *It creates Socket Connection with the ipaddress and the port to maintain DataInputStream and DataOutputStream Objects.*/
	private void startClientConnection() {
		try {
			clientsocket = new Socket(ipAddress,portNo);										//create Socket Connection to server
			dataInputStream = new DataInputStream(clientsocket.getInputStream());				//get data I/o Stream
			dataOutputStream = new DataOutputStream(clientsocket.getOutputStream());
			StringBuilder stringBuilderHTTP = new StringBuilder();								//Generate HTTP message Format For Connect
			stringBuilderHTTP.append("POST /").append("{"+clientName+"}").append("/ HTTP/1.1\r\n").append(host).append("\r\n").
			append(userAgent).append("\r\n").append(contentType).append("\r\n").append(contentLength).append(clientName.length()).append("\r\n").
			append(date).append(new Date()).append("\r\n");
			dataOutputStream.writeUTF(stringBuilderHTTP.toString());
			flag = true;			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to Register! Check Server Connection");
		}
	}
	
	/*Method Name SendRandomTimeBetween3and10 
	 * 
	 * It generates random digits between 3-10 and send the http message on DataOutputStream object*/
	protected void SendRandomTimeBetween3and10() {
		int randomNumber = ThreadLocalRandom.current().nextInt(3, 11);							// Math Function to generate randm number between 3-10
		StringBuilder stringBuilderHTTP = new StringBuilder();
		try {																					//Generate HTTP message Format to send number
			stringBuilderHTTP.append("POST /").append(randomNumber).append("/ HTTP/1.1\r\n").append(host).append("\r\n").
			append(userAgent).append("\r\n").append(contentType).append("\r\n").append(contentLength).append(String.valueOf(randomNumber).length()).append("\r\n").
			append(date).append(new Date()).append("\r\n");
			dataOutputStream.writeUTF(stringBuilderHTTP.toString());			
		} catch (IOException e1) {
			System.exit(0);
		}
	}
	
	/*Main method 
	 * 
	 * @param array of String
	 *It is called first by JVM when a class is run. It is a starting point of an application*/
	public static void main(String[] args) {													//Main will be invoked on starting application
		new Thread(new Runnable() {
			public void run() {
				try {
						ClientConnection window = new ClientConnection("Socket");						// Start the Client Application
						window.jFrame.setVisible(true);
				} catch (Exception e) {
						e.printStackTrace();
				}
			}
		}).start();
		String notification = ""; 
		String  splitArray[];
		
		while(true) {
			try {
				notification = dataInputStream.readUTF();										//Receive the message to show on the text area
				splitArray = notification.split(":");
				if(splitArray[0].equals("CONNECTED")) {											//Check what kind of message is it
					notification = splitArray[1]+" has Connected";
					notificationArea.append(notification+"\n");
				}
				else if(splitArray[0].equals("WAITED")) {										//Get the number and notify that server has waited for duration
					notification = splitArray[1]+"";
					notificationArea.append(notification+"\n");
					ClientConnection clientConnection=new ClientConnection();
					clientConnection.SendRandomTimeBetween3and10();								//Again Generate a random number and Send it to the server
				}
				else {
				}
			} catch (Exception e) {
			}
		}
		
	}
}
