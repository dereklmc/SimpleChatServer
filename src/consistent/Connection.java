package consistent;

import java.io.*;
import java.net.*;

class Connection {

	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;

	private String clientName;
	private InputStream rawIn;

	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			rawIn = clientSocket.getInputStream();
			in = new DataInputStream(rawIn);
			out = new DataOutputStream(clientSocket.getOutputStream());

			clientName = in.readUTF();
			System.out.println("Connected: " + clientName);

		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}
	
	public boolean hasMessage() {
		try {
			return rawIn.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}

	public String readMessage() {
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

	public boolean error() {
		// TODO Auto-generated method stub
		return false;
	}
}