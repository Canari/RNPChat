package client.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import client.main.ClientMonitor;

public final class TCPTransmitter {
	private Socket socket;
	
	public TCPTransmitter(Socket tcpSocket) throws SocketException {
		socket = tcpSocket;
	}
	
	public boolean signOut() throws IOException {
        BufferedReader inFromServer =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        
        String message = "BYE" + "\n";
        outToServer.writeBytes(message);
        
        String answer = inFromServer.readLine();
        
        inFromServer.close();
        outToServer.close();
        
        if (answer.equals("BYE")) {
        	ClientMonitor.signOut();
        	
        	return true;
        }
		
        ClientMonitor.addTextMessage(answer);
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Map<InetAddress, String> getClientList() throws IOException, ClassNotFoundException {
        BufferedReader messageFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        
        String message = "INFO" + "\n";
        outToServer.writeBytes(message);
        
        String answer = messageFromServer.readLine();
        
        messageFromServer.close();
        outToServer.close();

        if (answer.equals("LIST")) {            
            ObjectInputStream listFromServer = new ObjectInputStream(socket.getInputStream());
            
            try {
            	return (Map<InetAddress, String>)listFromServer.readObject();
            } catch (ClassCastException e) {
            	ClientMonitor.addTextMessage("Fehler: " + e.getMessage());
            }
        }
        
        ClientMonitor.addTextMessage(answer);
        return null;
	}
	
	public boolean sendUsername(String username) throws IOException {
        BufferedReader inFromServer =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        
        String message = "NEW " + username + "\n";
        outToServer.writeBytes(message);
        
        String answer = inFromServer.readLine();
        
        inFromServer.close();
        outToServer.close();
        
        if (answer.equals("OK")) {
        	return true;
        }
		
        ClientMonitor.addTextMessage(answer);
		return false;
	}
}
