package server.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class WorkerThread extends Thread {
	Socket connectionSocket = null;
	
	public WorkerThread(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}
	
    public void run() {
        try {
        	Monitor.incClientCount();
        	
        	System.out.println("Client angemeldet.");
	        
	        BufferedReader inFromClient;
	        
	        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	        
	        String lineInput;
	        
	        while(true) {
	        	inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	            lineInput = "";
	            
	            for (int i = 0; i < 255; i++) {
	            	char current = (char)inFromClient.read();
	            	
	            	if (current == '\n')
	            		break;
	            	
	            	lineInput += current;
	            }
	            
	            if(lineInput.substring(0, 3).equals("NEW")) {
	            	Monitor.addClient(connectionSocket.getInetAddress(), lineInput.substring(3));
	                
	                outToClient.writeBytes("OK" + "\n");
	            }
	            else if(lineInput.substring(0, 4).equals("INFO")) {
	            	outToClient.writeBytes("LIST" + "\n");
	            	ObjectOutputStream listToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
	            	listToClient.writeObject(Monitor.getClientList());
	            }
	            else if(lineInput.substring(0, 3).equals("BYE")) {	
	            	outToClient.writeBytes("BYE" + "\n");
	            	
	                outToClient.close();
	                inFromClient.close();
	                connectionSocket.close();
	                
	                break;
	            }
	            else {
	            	outToClient.writeBytes("ERROR" + " Unbekannter Befehl\n");
	            	
	                outToClient.close();
	                inFromClient.close();
	                connectionSocket.close();
	                
	                break;
	            }
	        }
        } catch (IOException ex) {
        	Monitor.println(ex.getMessage());
        } catch (NullPointerException ex) {
        }
        
        Monitor.decClientCount();
    }
}
