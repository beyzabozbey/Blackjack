package message_objects;

//this message is used for bets
public class BiddingMessage extends GameMessage {
	private static final long serialVersionUID = 1L;
	//variables of the message
	//we should know which game that this message belongs
	private String gname;
	private int bid;
	//this is the constructor to assign initial values to the variables
	public BiddingMessage(String gname, int x) {
		super(gname);
		this.gname = gname;
		bid = x;
	}
	public int getBid() {
		return bid;
	}
	public String getGname() {
		return gname;
	}
}
