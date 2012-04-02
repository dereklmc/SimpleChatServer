package consistent;

import java.io.*;
import java.net.*;

class Connection {

	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;

	private String clientName;

	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());

			clientName = in.readUTF();
			System.out.println("Connected: " + clientName);

		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}
	
	public boolean hasMessage() {
		try {
			return in.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}

	public String readMessage() throws IOException {
		return String.format("%s: %s", clientName, in.readUTF());
	}

	public boolean writeMessage(String message) {
		try {
			out.writeUTF(message);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return clientName.equals(((Connection)obj).clientName);
	}

	public void close() {
		// TODO CLOSE CONNECTIONS		
	}
}