package consistent;

import java.io.*;
import java.net.*;

class Connection extends Thread {
	
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	
	private String name;
	
	public Connection (Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out = new DataOutputStream( clientSocket.getOutputStream());
			
			name = in.readUTF();
			System.out.println("Connected: " + name);
			
//			this.start();
		} catch(IOException e) {
			System.out.println("Connection:"+e.getMessage());
		}
	}
	
	public void run() {
//		try {
//			// an echo server
//			String data = in.readUTF();
//			out.writeUTF(data);
//		} catch(EOFException e) {
//			System.out.println("EOF:"+e.getMessage());
//		} catch(IOException e) {
//			System.out.println("IO:"+e.getMessage());
//		} finally {
//			try {
//				clientSocket.close();
//			} catch (IOException e) {
//				/*close failed*/
//			}
//		}
	}

	public String readMessage() {
		try {
			if (in.available() > 0) {
				String msg = in.readUTF();
				System.out.println("Saw message from " + name + ": " + msg);
				return String.format("%s: %s", name, msg);
			}
		} catch (IOException e) { }
		return null;
	}

	public boolean writeMessage(String message) {
		try {
			System.out.println("Delivering Message To: " + name + ":: " + message);
			out.writeUTF(message);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}