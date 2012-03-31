package consistent;

import java.io.*;
import java.net.*;
import java.util.*;

public class ConnectionManager extends Thread {

	private List<Connection> connections;
	private ServerSocket listenSocket;

	public ConnectionManager(ServerSocket listenSocket) {
		this.listenSocket = listenSocket;

		connections = Collections
				.synchronizedList(new LinkedList<Connection>());
	}

	public List<Connection> getConnectionPool() {
		return connections;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				Socket clientSocket = listenSocket.accept();

				if (clientSocket == null) {
					continue;
				}
				Connection c = new Connection(clientSocket);

				synchronized (connections) {
					System.out.println("Added connection");
					connections.add(c);
				}

			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				System.out.println("IO ERROR: " + e.getMessage());
				continue;
			}
		}
	}

}