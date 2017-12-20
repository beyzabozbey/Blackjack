package message_objects;

import objects.Player;

//this message is used to create or join a game
public class JoinMessage extends GameMessage {
	private static final long serialVersionUID = 1L;
	//member variables of the class
	private String gname;
	private Player player;
	private String username;
	private int pn;
	private boolean join;
	//constructor sets the values to the variables
	//pn is player number of the game
	public JoinMessage(String gname, int pn, Player player, boolean join) {
		super(gname);
		this.gname = gname;
		this.player = player;
		this.pn = pn;
		this.join = join;
		username = player.getUsername();
	}
	//setters and getters
	public String getGname() {
		return gname;
	}
	public Player getPlayer() {
		return player;
	}
	public int getPn() {
		return pn;
	}
	public boolean getJoin() {
		return join;
	}
	public String getUsername() {
		return username;
	}
}
