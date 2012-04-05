package casual;

import java.util.Arrays;

public class Chat {
	
	public Chat(int port, String[] nodeAddresses) {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Syntax: java Chat <port> "
					+ "<node 1 ip name> ... <node n ip name>");
			System.exit(1);
		}
		
		int port = Integer.parseInt(args[0]);
		String[] nodeAddresses = Arrays.copyOfRange(args, 1, args.length);
		
		Chat c = new Chat(port, nodeAddresses);
	}
}
