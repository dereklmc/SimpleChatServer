package consistent;

/** 
 * ChatClient.java:<p> 
 * realizes communication with other clients through a central chat server. 
 * 
 * @author  Munehiro Fukuda (CSS, University of Washington, Bothell) 
 * @since   1/23/05 
 * @version 2/5/05 
 */

import java.net.*; // for Socket 
import java.io.*; // for IOException 

public class ChatClient {
	private Socket socket; // a socket connection to a chat server
	private InputStream rawIn; // an input stream from the server
	private DataInputStream in; // a filtered input stream from the server
	private DataOutputStream out; // a filtered output stream to the server
	private BufferedReader stdin; // the standart input

	/**
	 * Creates a socket, contacts to the server with a given server ip name and
	 * a port, sends a given calling user name, and goes into a "while" loop in
	 * that:
	 * <p>
	 * 
	 * <ol>
	 * <li>forward a message from the standard input to the server
	 * <li>forward a message from the server to the standard output
	 * </ol>
	 * 
	 * @param name
	 *            the calling user name
	 * @param server
	 *            a server ip name
	 * @param port
	 *            a server port
	 */
	public ChatClient(String name, String server, int port) {
		// Create a scoket, register, and listen to the server
		try {
			// Connect to the server
			socket = new Socket(server, port);
			rawIn = socket.getInputStream();
			// Create an input, an output, and the standard output stream.
			in = new DataInputStream(rawIn);
			out = new DataOutputStream(socket.getOutputStream());
			stdin = new BufferedReader(new InputStreamReader(System.in));
			// Send the client name to the server
			out.writeUTF(name);
			while (true) {
				// If the user types something from the keyboard, read it from
				// the standard input and simply forward it to the srever
				if (stdin.ready()) {
					String str = stdin.readLine();
					// no more keyboard inputs: the user typed ^d.
					if (str == null)
						break;
					out.writeUTF(str);
				}
				// If the server gives me a message, read it from the server
				// and write it down to the standard output.
				if (rawIn.available() > 0) {
					String str = in.readUTF();
					System.out.println(str);
				}
			}
			// Close the connection. That's it.
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Usage: java ChatClient <your_name> <server_ip_name> <port>
	 * 
	 * @param args
	 *            Receives a client user name, a server ip name, and its port in
	 *            args[0], args[1], and args[2] respectively.
	 */
	public static void main(String args[]) {
		// Check # args.
		if (args.length != 3) {
			System.err.println("Syntax: java ChatClient <your name> "
					+ "<server ip name> <port>");
			System.exit(1);
		}
		// convert args[2] into an integer that will be used as port.
		int port = Integer.parseInt(args[2]);
		// instantiate the main body of ChatClient application.
		new ChatClient(args[0], args[1], port);
	}
}