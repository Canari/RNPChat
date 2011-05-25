package server.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import client.main.ChatClient;

/**
 *
 * @author stavrou
 */
public class TCPServerParallel {
    public static void main(String[] args) {
        try {
            ServerSocket welcomeSocket = new ServerSocket(ChatClient.tcpPort);
            Socket connectionSocket;
        
            while(true) {
            	if (Monitor.isEntryPossible()) {
	                connectionSocket = welcomeSocket.accept();
	                
	                WorkerThread thread = new WorkerThread(connectionSocket);
	                thread.start();
            	}
            }
        } catch (IOException ex) {
        	System.out.println(ex.getMessage());
        } catch (NullPointerException ex) {
	    }    
    }
}