package client.main;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ClientMonitor {
	private static ChatClient gui = null;
	private static Map<InetAddress, String> clients = new HashMap<InetAddress, String>();
	
	public static synchronized void initialize(ChatClient gui) {
		ClientMonitor.gui = gui;
	}
	
	// GUI Manipulation
	public static synchronized void addTextMessage(String text) {
		gui.addTextMessage(text);
	}
	
	// Client Liste verwalten
	public static synchronized Set<InetAddress> getClientIPs() {
		return clients.keySet();
	}
	public static synchronized void updateClientList() {
		gui.updateClientList(clients.values().toArray());
	}
	public static synchronized void addClient(InetAddress ip, String name) {
		clients.put(ip, name);
	}
	public static synchronized void setClientList(Map<InetAddress, String> clients) {
		ClientMonitor.clients = new HashMap<InetAddress, String>(clients);
		System.out.println(ClientMonitor.clients);
		updateClientList();
	}

	public static void signOut() {
		clients.clear();
		updateClientList();
		gui.signOut();
	}
}
