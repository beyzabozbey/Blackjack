package message_objects;

//this message is used to add cards to the user's hand
public class PlayingMessage extends GameMessage{
	private static final long serialVersionUID = 1L;
	//variables of the class
	private String gname;
	private int choice;
	//constructor 
	//user can enter 1 or 2 to stay or hit
	public PlayingMessage(String gname, int x) {
		super(gname);
		this.gname = gname;
		choice = x;
	}
	//constructor for string input: "hit" ,"stay"
	public PlayingMessage(String gname, String str) {
		super(gname);
		this.gname = gname;
		if(str.equals("stay") || str.equals("1"))
			choice = 1;
		else
			choice = 2;
	}
	//setters getters
	public String getGname() {
		return gname;
	}
	public int getChoice() {
		return choice;
	}
}
