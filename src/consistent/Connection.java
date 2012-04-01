package consistent;

import java.io.*;
import java.net.*;

class Connection {

	public class Message {

		private String senderName;
		private String messageBody;

		public Message(String senderName, String messageBody) {
			this.senderName = senderName;
			this.messageBody = messageBody;
		}

		@Override
		public String toString() {
			return String.format("%s: %s", senderName, messageBody);
		}
	}

	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;

	private String name;

	public Connection(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());

			name = in.readUTF();
			System.out.println("Connected: " + name);

		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public Message readMessage() {
		try {
			if (in.available() > 0) {
				return new Message(name, in.readUTF());
			}
		} catch (IOException e) {
		}
		return null;
	}

	public boolean writeMessage(Message message) {
		try {
			out.writeUTF(message.toString());
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}