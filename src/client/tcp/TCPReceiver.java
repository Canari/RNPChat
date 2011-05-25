package client.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import client.main.ClientMonitor;

public final class TCPReceiver extends Thread {
	private Socket socket;
	private TCPTransmitter transmitter;
	private boolean toxic = true;
	
	public TCPReceiver(Socket tcpSocket) throws SocketException {
		socket = tcpSocket;
		transmitter = new TCPTransmitter(socket);
	}
	
	public void triggerToxic() {
		toxic = !toxic;
	}
	
	@Override
	public void run() {
		while (toxic) {
			try {
				Map<InetAddress, String> clientList = transmitter.getClientList();
				
				if (clientList != null) {
					ClientMonitor.setClientList(clientList);
				}
				
				sleep(5000);
			} catch (InterruptedException e) {
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
			}
		}
	}
}
