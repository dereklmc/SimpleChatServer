package consistent;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author derek
 * 
 */
public class ChatServer {

	// How long to wait for new connectiosn from clients.
	private static final int DEFAULT_SERVER_SOCKET_TIMEOUT = 500;

	// The socket the server should listen on
	private ServerSocket listenSocket;
	// All currently connected clients.
	private List<Connection> clients;

	/**
	 * Initializes a chat server that accepts connections from new clients on a
	 * given port.
	 * 
	 * @param serverPort
	 *            - The port through which clients will communicate with the
	 *            server. Assumed is greater than 5000.
	 * @param serverSocketTimeout
	 *            -The length of time to wait for a new connection.
	 * @throws IOException
	 *             If an error occurs when creating a server.
	 */
	public ChatServer(int serverPort, int serverSocketTimeout)
			throws IOException {
		listenSocket = new ServerSocket(serverPort);
		listenSocket.setSoTimeout(serverSocketTimeout);

		clients = new LinkedList<Connection>();
	}

	/**
	 * Waits for a limited amount of time for a new connection from a client.
	 * 
	 * @return True if a new connection was found, false otherwise.
	 * 
	 * @throws IOException
	 *             if an error occurred while accepting a new connection.
	 */
	public boolean acceptNewConnection() throws IOException {
		try {
			Socket clientSocket = listenSocket.accept();
			try {
				Connection c = new Connection(clientSocket);
				broadcastClientState(c, "Connected");
				clients.add(c);
				return true;
			} catch (IOException e) {
				clientSocket.close();
			}
		} catch (SocketTimeoutException e) {
		}
		return false;
	}

	/**
	 * Broadcasts changes in state for a client to all clients. Useful for
	 * broadcasting that a new client has connected or has disconnected.
	 * 
	 * @param client - The client whose state has changed.
	 * @param state - The new state of the client.
	 */
	private void broadcastClientState(Connection client, String state) {
		for (Connection connectedClient : clients) {
			connectedClient.writeMessage(String.format("## %s %s ##",
					client.getClientName(), state));
		}
	}

	/**
	 * Reads all unread messages and delivers them to the appropriate clients.
	 * 
	 * A message from one client is delivered to all other clients first before
	 * the next message is read and delivered. Messages are always read from
	 * clients in the same order.
	 */
	public void processMessages() {
		for (int i = 0; i < clients.size(); i++) {
			Connection source = clients.get(i);
			if (!source.hasMessage())
				continue;
			String message = source.readMessage();
			if (message == null)
				continue;
			// Double loop -> no if check that only matters on one iteration.
			// Deliver messages to all clients < current client's rank
			for (int j = 0; j < i; i++)
				clients.get(j).writeMessage(message);
			// Deliver messages to all clients > current client's rank
			for (int j = i + 1; i < clients.size(); i++)
				clients.get(j).writeMessage(message);
		}
	}

	/**
	 * Looks for bad connections that errored during reads or writes. Closes the
	 * connections.
	 * 
	 * @throws IOException
	 */
	private void closeBadConnections() throws IOException {
		ListIterator<Connection> clientIt = clients.listIterator();
		while (clientIt.hasNext()) {
			Connection client = clientIt.next();
			if (client.error()) {
				clientIt.remove();
				client.close();
				broadcastClientState(client, "Disconnected");
			}
		}
	}

	/**
	 * Attempts to close all connections to all clients and the socket the
	 * server is listening on.
	 */
	private void close() {
		// Try to close each client.
		for (Connection client : clients) {
			try {
				client.close();
			} catch (IOException e) {
				System.err.printf("Failed to close client [%s] : %s\n",
						client.getClientName(), e.getMessage());
			}
		}
		try {
			listenSocket.close();
		} catch (IOException e) {
			System.err.printf("Failed to close server : %s\n", e.getMessage());
		}
	}

	public static void main(String args[]) {
		ChatServer server = null;
		try {
			int serverPort = Integer.parseInt(args[0]);
			server = new ChatServer(serverPort, DEFAULT_SERVER_SOCKET_TIMEOUT);

			while (true) {
				server.acceptNewConnection();
				server.processMessages();
				server.closeBadConnections();
			}
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Syntax: java ChatServer <port> ");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Listen :" + e.getMessage());
			System.exit(2);
		} catch (NumberFormatException e) {
			System.err.println("Bad Server Port!");
			System.exit(3);
		} finally {
			if (server != null)
				server.close();
		}
	}
}