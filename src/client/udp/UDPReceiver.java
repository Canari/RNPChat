package client.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import client.main.ClientMonitor;

public final class UDPReceiver extends Thread {
	private static int messageLength = 85;
	
	private DatagramSocket socket;
	private boolean toxic = true;
	
	public UDPReceiver(DatagramSocket udpSocket) throws SocketException {
		socket = udpSocket;
	}
	
	public void triggerToxic() {
		toxic = !toxic;
	}
	
	public void run() {
		while(toxic) {
			try {
				checkForIncomingMessages();
			} catch (IOException e) {
				ClientMonitor.addTextMessage("Fehler: " + e.getMessage());
			}
		}
	}

	private void checkForIncomingMessages() throws IOException {
		byte[] receiveData = new byte[messageLength];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);
		ClientMonitor.addTextMessage(String.valueOf(receiveData));
	}
}
