import java.io.Serializable;


public class Message implements Serializable {
	
	int state[];
	private String body;
	
	public Message(int [] state, String body) {
		this.state = state;
		this.body = body;
	}

	@Override
	public String toString() {
		String outFormat = "Message {State: [%s], Body: \"%s\"}";
		String outState = Integer.toString(state[0]);
		for (int i = 1; i < state.length; i++)
			outState += "," + state[i];
		return String.format(outFormat, outState, body);
	}

	public int[] getState() {
		return state;
	}

	public String getBody() {
		return body;
	}

}
