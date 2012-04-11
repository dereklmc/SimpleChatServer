import java.io.Serializable;


public class Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1071983615436769006L;
	
	private int state[];
	private String body;
	private int hostRank;
	
	public Message(int hostRank, int [] state, String body) {
		this.hostRank = hostRank;
		this.state = state;
		this.body = body;
	}

	@Override
	public String toString() {
		String outFormat = "Message {Rank: %s, State: [%s], Body: \"%s\"}";
		String outState = Integer.toString(state[0]);
		for (int i = 1; i < state.length; i++)
			outState += "," + state[i];
		return String.format(outFormat, hostRank, outState, body);
	}

	public int[] getState() {
		return state;
	}

	public String getBody() {
		return body;
	}
	
	public int getHost() {
		return hostRank;
	}
	
	public boolean isDeliverable(int[] recieverState) {
		if (state[hostRank] != recieverState[hostRank] + 1) {
			return false;
		}
		for (int j = 0; j < state.length; j++) {
			if (j != hostRank && state[j] > recieverState[j]) {
				return false;
			}
		}
		return true;
	}

}
