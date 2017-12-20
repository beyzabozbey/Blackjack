package message_objects;

//this message is used to check game name or username
public class CheckMessage extends GameMessage {
	private static final long serialVersionUID = 1L;
	//declaring variables of the class
	private String gname;
	private String name;
	private boolean isValid;
	private boolean isGname;
	private boolean isUsername;
	private String error;
	private boolean join;
	//constructor assigns the initial values to the variables
	public CheckMessage(String gname) {
		super(gname);
		this.gname = gname;
		isUsername = false;
		isGname = false;
		error = "";
	}
	//getters and setters
	public String getGname() {
		return gname;
	}
	public String getName() {
		return name;
	}
	//this is the string that will be checked
	public void setName(String name) {
		this.name = name;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	//this variable tells us whether the name is a game name or not
	//because it can be a username
	public boolean isGname() {
		return isGname;
	}
	public void setGname(boolean isGname) {
		this.isGname = isGname;
	}
	//this variable tells us whether the name is a username or not
	//because it can be a game name
	public boolean isUsername() {
		return isUsername;
	}
	public void setUsername(boolean isUsername) {
		this.isUsername = isUsername;
	}
	//error message
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	//is the user trying to join a game or create a new one
	public boolean isJoin() {
		return join;
	}
	public void setJoin(boolean join) {
		this.join = join;
	}
	
}
