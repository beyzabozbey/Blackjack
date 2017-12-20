package objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//game's functionality is here
public class Game {
	//private static final String[] rounds = {"", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN"};
	//variables of the class
	private int playerNum;
	private int currentPlayers; //current number of players
	private int status;
	private String name;
	private Player owner;
	//players
	private Vector<Player> players;
	//player usernames
	private Vector<String> usernames;
	//to access each player by their username
	private Map<String, Player> playerMap;
	private int turn;
	private Player dealer;
	private Deck deck;
	private int round;
	private boolean end;
	private boolean dWinner; //is dealer the winner

	// status = 0 is waiting status 
	public Game(String name, int pn, Player owner) {
		dealer = new Player(name, "", null);
		deck = new Deck();
		this.name = name;
		this.owner = owner;
		playerNum = pn;
		round = 1;
		end = false;
		dWinner = false;
		//creates the data structures
		players = new Vector<Player>();
		players.add(owner);
		usernames = new Vector<String>();
		usernames.add(owner.getUsername());
		playerMap = new HashMap<String, Player>();
		playerMap.put(owner.getUsername(), owner);
		currentPlayers = 1;
		turn = -1;
		if(pn != 1)
			status = 0;
		else
			status = 1;
	}
	//gives the status of the game: playing, betting, ended,etc.
	public void setStatus(int s) {
		status = s;
	}
	public int getStatus() {
		return status;
	}
	//adds the player to the game
	public boolean join(Player player) {
		currentPlayers++;
		players.add(player);
		playerMap.put(player.getUsername(), player);
		if(currentPlayers == playerNum)
			status = 1;
		return true;
	}
	public Player getOwner() {
		return owner;
	}
	//gives how many players can enter
	public int getRemaining() {
		return playerNum - currentPlayers;
	}
	//setters and getters
	public Vector<Player> getPlayers() {
		return players;
	}
	public String getName() {
		return name;
	}
	public Vector<String> getUsernames() {
		return usernames;
	}
	public Map<String, Player> getPlayerMap() {
		return playerMap;
	}
	public void setPlayerMap(Map<String, Player> playerMap) {
		this.playerMap = playerMap;
	}
	public Player getDealer() {
		return dealer;
	}
	public void setDealer(Player dealer) {
		this.dealer = dealer;
	}
	public int getTurn() {
		return turn;
	}
	public void setTurn(int turn) {
		this.turn = turn;
	}
	public Deck getDeck() {
		return deck;
	}
	public void setDeck(Deck deck) {
		this.deck = deck;
	}
	//gives the next round messages
	public String getRound() {
		return ("ROUND " + round);
	}
	public void nextRound() {
		round++;
	}
	public boolean isEnded() {
		return end;
	}
	//decides whether the game should end
	public void setEnd() {
		int cnt = 0;
		for(Player p : players) {
			if(p.getChips() == 0)
			{
				end = true;
				cnt++;
			}
		}
		if(cnt == players.size())
			dWinner = true;
	}
	public boolean isDWinner() {
		return dWinner;
	}
	public int getPlayerNum() {
		return playerNum;
	}
	
}
