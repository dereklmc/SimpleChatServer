package consistent;

import java.io.*;
import java.net.*;

/**
 * Encapsulates the state of a connection between the client and a server.
 * 
 * Used to "safely" read messages from and write messages to a specific client.
 * If an error occurs during read and write, hides the error from the client and
 * prevents subsequent reads and writes.
 * 
 * Expects clients to connect with a specific name.
 * 
 */
class Connection {

	// Stream of messages from the client.
	DataInputStream in;

	// Stream of messages being sent to the client.
	DataOutputStream out;

	// For checking if the client has sent any messages.
	private InputStream rawIn;

	// The network connection to the client.
	Socket clientSocket;

	// The name of the client.
	private String clientName = null;

	// If the client had trouble reading or writing a connection.
	private boolean errorOccured;

	/**
	 * Instantiantes a new Connection to a client. The actual connection through
	 * a socket should already be open.
	 * 
	 * @param aClientSocket -
	 *            The network connection to the client.
	 * @throws IOException
	 *             If the client connects without sending a name.
	 */
	public Connection(Socket aClientSocket) throws IOException {
		errorOccured = false; // No errors yet!
		clientSocket = aClientSocket;

		// Get IO streams.
		rawIn = clientSocket.getInputStream();
		in = new DataInputStream(rawIn);
		out = new DataOutputStream(clientSocket.getOutputStream());

		// Attempt to read client name. If a name couldn't be read, fail.
		clientName = in.readUTF();
		System.out.println("Connected: " + clientName);
	}

	/**
	 * Check if there is a message to read.
	 * 
	 * @return True if a message is available to read, false otherwise.
	 */
	public boolean hasMessage() {
		try {
			// Shortcircuit if an error occured at some point in the past.
			return error() || rawIn.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Attempt to read a message. Does not read a message if an error has
	 * occured in the past or an error occurs during reading. Will block until a
	 * message is availble. Clients of Connection should call
	 * {@link #hasMessage()} first.
	 * 
	 * @return The read message.
	 * @return <code>null</code> if a message wasn't read.
	 * 
	 * @postcondition If an error occured, client is an error state:
	 *                {@link #error()} == <code>true</code>
	 */
	public String readMessage() {
		if (!error()) {
			try {
				return String.format("%s: %s", clientName, in.readUTF());
			} catch (IOException e) {
				errorOccured = true;
			}
		}
		return null;
	}

	/**
	 * Attempt to send a given message to the client. Does not send the message
	 * if Does not read a message if an error has occured in the past or an
	 * error occurs during writing.
	 * 
	 * @param message -
	 *            the message to send to the client. Should not be
	 *            <code>null</code>.
	 * 
	 * @postcondition If an error did not occur, the message is sent to the
	 *                client.
	 * @postcondition If an error occured, client is an error state:
	 *                {@link #error()} == <code>true</code>
	 * 
	 * @throws IllegalArgumentException if message is <code>null</code>
	 */
	public void writeMessage(String message) {
		if (message == null) {
			throw new IllegalArgumentException("Message Is Null!");
		}
		if (!error()) {
			try {
				out.writeUTF(message);
			} catch (IOException e) {
				errorOccured = true;
			}
		}
	}

	/**
	 * Attempts to close the connection.
	 * 
	 * @throws IOException if the connection could not be closed.
	 */
	public void close() throws IOException {
		clientSocket.close();
		in.close();
		out.close();
	}

	/**
	 * Checks if an error occured at any point during reading or writing.
	 * 
	 * @return True if an error occured, false otherwise.
	 */
	public boolean error() {
		return errorOccured;
	}

	/**
	 * @return the name of the client represented by this connection.
	 */
	public String getClientName() {
		return clientName;
	}
}