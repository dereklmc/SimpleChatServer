package consistent;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

	private static final int SERVER_SOCKET_TIMEOUT = 500;
	
	private ServerSocket listenSocket;
	private List<Connection> clients;
	
	public ChatServer(int serverPort) throws IOException {
		listenSocket = new ServerSocket(serverPort);
		listenSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT);
		
		clients = new LinkedList<Connection>();
	}
	
	public void acceptNewConnections() throws IOException {
		try {
		Socket clientSocket = listenSocket.accept();
		Connection c = new Connection(clientSocket);
		clients.add(c);
		} catch (SocketTimeoutException e) { }
	}
	
	public void processMessages() {
		for (Connection source : clients) {
			String message = source.readMessage();
			if (message != null) {
				for (Connection dest : clients) {
					if (source != dest)
						dest.writeMessage(message);
				}
			}
		}
	}
	
	public static void main(String args[]) {
		try {
			int serverPort = Integer.parseInt(args[0]);
			ChatServer server = new ChatServer(serverPort);

			while (true) {
				server.acceptNewConnections();
				server.processMessages();
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Bad Server Port!");
		}
	}
}