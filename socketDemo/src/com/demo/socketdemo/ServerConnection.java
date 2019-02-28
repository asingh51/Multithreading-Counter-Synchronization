//Abhay Singh
//1001669333

package com.demo.socketdemo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/*Class ServerConnection
 * 
 * For Server Connection to initiate the server side programming.*/
public class ServerConnection {
	private static Object LOCK = new Object();
	private static int port = 8005;															//port for server to start
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static DataOutputStream dataOutputStream;
	private JFrame jFrame;
	
	/*Text area to show all logs*/
	private static JTextArea jTextArea;
	
	/*Text area to show all available users online*/
	private static JTextArea jTextAreaUserNames;
	
	/*Text area to show all waiting users online*/
	private static JTextArea jTextAreaWaitingUserNames;
	
	/*HashSet to store all available users online*/
	Set<String> userNames= new HashSet<>();
	
	/*Queue to store all Waiting users online*/
	Queue<String[]> waitingQueue = new LinkedList<String[]>();

	/*Default Constructor 
	 * 
	 * It is to initiate and create Swing based JFrame window*/
	public ServerConnection() {
		constructWindow();
	}

	/*Method constructWindow 
	 * 
	 * It will create a Swing based Jframe and put all the components and add listerners to components.*/
	private void constructWindow() {
		jFrame = new JFrame("SERVER");														//create a Swing JFrame	
		jFrame.setBounds(0, 0, 550, 8500);
		jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jFrame.addWindowListener(new java.awt.event.WindowAdapter() {						//https://stackoverflow.com/questions/13207519/adding-a-new-windowlistener-to-a-jframe
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		            System.exit(0);
		    }
		});
		jFrame.getContentPane().setLayout(null);											//For manual positioning
		
		JScrollPane jScrollPane = new JScrollPane();										//Create ScrollPane for TextArea 
		jScrollPane.setBounds(0, 20, 550, 625);
		jFrame.getContentPane().add(jScrollPane);
		
		jTextArea = new JTextArea();														//Create TextArea to print infp
		jScrollPane.setViewportView(jTextArea);
		jTextArea.setEditable(false);
		
		Thread t = new Thread () {
			@Override
			public void run() {
				startServerConnection();													//Start the operation
			};
		};
		t.start();
		
		JLabel userLabel = new JLabel("Available Users");									// Create label
		userLabel.setBounds(10, 680, 195, 14);
		jFrame.getContentPane().add(userLabel);	
		
		JScrollPane jScrollPaneUserNames = new JScrollPane();								//Create ScrollPane for TextArea 
		jScrollPaneUserNames.setBounds(10,700, 100, 150);
		jFrame.getContentPane().add(jScrollPaneUserNames);
		
		jTextAreaUserNames = new JTextArea();												//Create TextArea to print Real Time User Info
		jScrollPaneUserNames.setViewportView(jTextAreaUserNames);
		jTextAreaUserNames.setEditable(false);
		
		JButton jButtonExit = new JButton("Exit");											//Exit Functionality to exit the Frame
		jButtonExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		jButtonExit.setBounds(200, 830, 89, 23);
		jFrame.getContentPane().add(jButtonExit);
	}
	
	/*Method startServerConnection 
	 * 
	 * It creates Socket server on localhost ipaddress and the port.*/
	protected void startServerConnection() {
		try {
			serverSocket = new ServerSocket(port);											//Create ServerSocket Connection on the localhost
			jTextArea.append("Server is Up.\n");
			while(true) {
				clientSocket = serverSocket.accept();										//create clientSocket to start accepting the request
				dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());	
				ServerClientHandler serverClientHandler = new ServerClientHandler(clientSocket, dataOutputStream);
				//System.out.println("serverClientHandler.hashCode()"+serverClientHandler.hashCode());
				serverClientHandler.start();
			}
		}
		catch (IOException e) {
			jTextArea.append("IOException. Check connection. \n");
		}
	}
	
	/*Inner class ServerClientHandler
	 * 
	 * It is for the Server and Client message handling. It handles DataInputStream and DataOutputStream objects*/
	public class ServerClientHandler extends Thread {										//inner class to handle I/O data streams
		private Socket clientSocket;
		private String clientName;
		private DataInputStream dataInputStream;
		private DataOutputStream dataOutputStream;
		
		
		
		public ServerClientHandler(Socket client, DataOutputStream outputStream) {
			this.clientSocket = client;
			this.dataOutputStream= outputStream;
			try {
				dataInputStream = new DataInputStream(clientSocket.getInputStream());		//Accept the data from client Socket
			} catch (Exception e) {
				e.getMessage();
			}
		}
		
		/*Overrided run method 
		 * 
		 * It is for Server and Client message handling*/
		@Override
		public void run() {																	//Run method to handle all the Server Client Communications
			String line = "",msgin;
			String arr[]=null, arr1[] = null;
			try {
				while(true) {
					line = dataInputStream.readUTF();										//Read the data input Stream
					arr1 = line.split("\n");
					msgin = arr1[0].split("/")[1];
					waitingQueue.add(arr1);
					long startTime	= System.currentTimeMillis();
					arr=waitingQueue.remove();
					synchronized (LOCK) {
						if(msgin.contains("{")) {
							clientName = arr[0].substring(arr[0].indexOf("{")+1,arr[0].indexOf("}")); // Get the Client Name 
							jTextArea.append(line);
							dataToClient("CONNECTED:"+clientName, dataOutputStream);			//Send Connected message to Client
							userNames.add(clientName);
							jTextAreaUserNames.setText("");
							for (String string : userNames) {
								jTextAreaUserNames.append(string+"\n");							//adding Users in Text Area
							}
						}
						else {
							jTextArea.append(line);
							jTextArea.append(clientName+": "+msgin+"\n");
							try {
								Thread.sleep(Integer.parseInt(msgin)*1000);						//Sleep for the client thread.
								//System.out.println(msgin);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							jTextArea.append("Wait completed for "+clientName+"\n");			//Send the message to the  client and Print on Text Area
							long endTime= System.currentTimeMillis();
							//dataToClient("WAITED:Server waited "+(endTime-startTime)/1000+" seconds for client were"+msgin +"secs "+clientName, dataOutputStream);
							dataToClient("WAITED:Server waited "+(endTime-startTime)/1000+" seconds for client "+clientName, dataOutputStream);
						}
						for(String[] arrayString:waitingQueue)
						jTextAreaWaitingUserNames.setText(arrayString[0].substring(arr[0].indexOf("{")+1,arr[0].indexOf("}")));
					}
				}					
			} 
			catch (IOException e) {
					jTextArea.append("\n"+clientName+" has left.\n\n");
					userNames.remove(clientName);											//Removing Users from Set
					jTextAreaUserNames.setText("");
					for (String string : userNames) {
						jTextAreaUserNames.append(string+"\n");								//Removing Users from Text Area
					}
				}
			}
		}
	
	/*Method dataToClient 
	 * 
	 * this method will send the message to the server on DataOutputStream object*/
	public void dataToClient(String msg, DataOutputStream dataOutputStream) {				//Send data to Client	
		try {
			dataOutputStream.writeUTF(msg);
		} catch (Exception e) {
			
		}
	}
	
	/*Main method 
	 * 
	 * It is called first when the class is run. It is a starting point of an application*/
	public static void main(String[] args) {												//Main will be invoked on starting application
		ServerConnection serverConnection = new ServerConnection();							// Start the Client Application
		serverConnection.jFrame.setVisible(true);		
	}	
}
	