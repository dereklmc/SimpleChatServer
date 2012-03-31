package consistent;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private static final int SERVER_SOCKET_TIMEOUT = 500;

	public static void main(String args[]) {
		try {
			int serverPort = Integer.parseInt(args[0]);
			ServerSocket listenSocket = new ServerSocket(serverPort);
			listenSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT);

//			ConnectionManager cm = new ConnectionManager(listenSocket);
			List<Connection> clients = new LinkedList<Connection>(); //cm.getConnectionPool();
//			cm.start();

			while (true) {
				try {
					Socket clientSocket = listenSocket.accept();
					Connection c = new Connection(clientSocket);
					clients.add(c);
					System.out.println("Added connection");
				} catch (SocketTimeoutException e) { }
				//synchronized (clients) {
					for (Connection source : clients) {
						String message = source.readMessage();
						if (message != null) {
						for (Connection dest : clients) {
							if (source != dest)
								dest.writeMessage(message);
						}
						}
					}
				//}
			}
		} catch (IOException e) {
			System.out.println("Listen :" + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Bad Server Port!");
		}
	}
}