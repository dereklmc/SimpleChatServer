package consistent;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

	private static final int DEFAULT_SERVER_SOCKET_TIMEOUT = 500;

	private ServerSocket listenSocket;
	private List<Connection> clients;

	public ChatServer(int serverPort, int serverSocketTimeout) throws IOException {
		listenSocket = new ServerSocket(serverPort);
		listenSocket.setSoTimeout(serverSocketTimeout);

		clients = new LinkedList<Connection>();
	}

	public boolean acceptNewConnection() throws IOException {
		try {
			Socket clientSocket = listenSocket.accept();
			Connection c = new Connection(clientSocket);
			if (c != null) {
				clients.add(c);
				return true;
			}
		} catch (SocketTimeoutException e) { }
		return false;
	}

	public void processMessages() {
		ListIterator<Connection> clientsIterator = clients.listIterator();
		while(clientsIterator.hasNext()) {
			Connection source = clientsIterator.next();
			if (!source.hasMessage()) {
				continue;
			}
			try {
				String message = source.readMessage();
				for (Connection dest : clients) {
					if (source != dest) {
						dest.writeMessage(message);
					}
				}
			} catch (IOException e) {
				source.close();
				clientsIterator.remove();
				continue;
			}
		}
	}

	public static void main(String args[]) {
		try {
			int serverPort = Integer.parseInt(args[0]);
			ChatServer server = new ChatServer(serverPort,DEFAULT_SERVER_SOCKET_TIMEOUT);

			while (true) {
				server.acceptNewConnection();
				server.processMessages();
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Bad Server Port!");
		}
	}
}