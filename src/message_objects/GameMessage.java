package message_objects;

import java.io.Serializable;

//this is the parent class of the messages
public class GameMessage implements Serializable {
	public static final long serialVersionUID = 1L;
	//variables of the class
	private String message;
	private int status;
	private String turn;
	private String sender;
	private String gname;
	//constructor sets the game name
	//which game this message belongs
	public GameMessage(String gname) {
		this.gname = gname;
		status = 0;
		turn = "";
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String str) {
		message = str;
	}
	public int getStatus() {
		return status;
	}
	//status gives the status of the game
	//in which state it is. it can be joining or playing or betting or ended, etc.
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTurn() {
		return turn;
	}
	public void setTurn(String turn) {
		this.turn = turn;
	}
	public String getSender() {
		return sender;
	}
	//gives the sender of the message
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getGname() {
		return gname;
	}
	
}
