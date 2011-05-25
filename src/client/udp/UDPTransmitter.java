package client.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import client.main.ChatClient;
import client.main.ClientMonitor;

public final class UDPTransmitter {
	private DatagramSocket socket;
	
	public UDPTransmitter(DatagramSocket udpSocket) throws SocketException {
		socket = udpSocket;
	}
	
	public void sendMessage(String message) throws IOException {
		for(InetAddress ip : ClientMonitor.getClientIPs()) {
			byte[] sendData = message.getBytes();
			
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, ChatClient.udpPort);
			socket.send(sendPacket);
		}
	}
}
