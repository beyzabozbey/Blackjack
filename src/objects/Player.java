package objects;

import java.io.Serializable;
import java.util.Vector;

import serverside.ServerThread;

//player's functionality is here
public class Player implements Serializable {
	public static final long serialVersionUID = 1L;
	//variables of the class
	private int chips;
	private Vector<String> cards;
	private int total; //their hand's total
	private String gname;
	private int turn;
	private int bet;
	int aces; // number of aces that the player has
	boolean busted;
	boolean blackjack;
	private String username;
	private ServerThread st;
	//constructor initializes the values which are very important since we must have a game name
	//and a username and their serverthread to communicate
	public Player(String gname, String username, ServerThread st) {
		chips = 500;
		blackjack = false;
		this.gname = gname;
		turn = 0;
		bet = 0;
		total = 0;
		aces = 0;
		busted = false;
		cards = new Vector<String>();
		this.username = username;
		this.st = st;
	}
	//setters and getters
	public int getChips() {
		return chips;
	}
	public void setChips(int chips) {
		this.chips = chips;
	}
	public Vector<String> getCards() {
		return cards;
	}
	public void setCards(Vector<String> cards) {
		this.cards = cards;
	}
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	public int getTurn() {
		return turn;
	}
	public void setTurn(int turn) {
		this.turn = turn;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getBet() {
		return bet;
	}
	public void setBet(int bet) {
		this.bet = bet;
	}
	public ServerThread getSt() {
		return st;
	}
	public void setSt(ServerThread st) {
		this.st = st;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	//adds a card to their hand
	public void addCard(String card, int value) {
		cards.add(card);
		total += value;
		if(card.contains("ACE"))
			aces++;
	}
	//gives the number of aces that they have
	public int getAces() {
		return aces;
	}
	public void setAces(int aces) {
		this.aces = aces;
	}
	public boolean isBusted() {
		return busted;
	}
	public void setBusted(boolean busted) {
		this.busted = busted;
	}
	public boolean isBlackjack() {
		return blackjack;
	}
	public void setBlackjack(boolean bj) {
		blackjack = bj;
	}
}
