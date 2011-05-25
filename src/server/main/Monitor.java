package server.main;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Monitor {
	private final static int maxClients = 150;
	private static int clientCount = 0;
	private static Map<InetAddress, String> clientList = new HashMap<InetAddress, String>();
	
	protected synchronized static Map<InetAddress, String> getClientList() {
		return clientList;
	}
	
	protected synchronized static void addClient(InetAddress ip, String name) {
		clientList.put(ip, name);
	}
	
	protected synchronized static boolean incClientCount() {
		if (isEntryPossible()) {
			clientCount++;
			return true;
		}
		
		return false;			
	}
	
	protected synchronized static boolean decClientCount() {
		if (0 < clientCount) {
			clientCount--;
			return true;
		}
		
		return false;
	}
	
	protected synchronized static boolean isEntryPossible() {		
		return (maxClients > clientCount);
	}
	
	protected synchronized static void println(String text) {
		System.out.println(text);
	}
}