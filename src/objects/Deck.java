package objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

//this class is for deck operations
public class Deck {
	//names of the cards
	//it should be static and final because card names don't change
	//and we only need one instance of this String array among all Deck objects
	//because the value of this variable is independent of the objects (not unique for each object)
	private static final String[] cards = {"ACE of SPADES", "TWO of SPADES", "THREE of SPADES", "FOUR of SPADES", "FIVE of SPADES", "SIX of SPADES", "SEVEN of SPADES", "EIGHT of SPADES", "NINE of SPADES", "TEN of SPADES", "JACK of SPADES", "QUEEN of SPADES", "KING of SPADES",
							"ACE of HEARTS", "TWO of HEARTS", "THREE of HEARTS", "FOUR of HEARTS", "FIVE of HEARTS", "SIX of HEARTS", "SEVEN of HEARTS", "EIGHT of HEARTS", "NINE of HEARTS", "TEN of HEARTS", "JACK of HEARTS", "QUEEN of HEARTS", "KING of HEARTS",
							"ACE of CLUBS", "TWO of CLUBS", "THREE of CLUBS", "FOUR of CLUBS", "FIVE of CLUBS", "SIX of CLUBS", "SEVEN of CLUBS", "EIGHT of CLUBS", "NINE of CLUBS", "TEN of CLUBS", "JACK of CLUBS", "QUEEN of CLUBS", "KING of CLUBS",
							"ACE of DIAMONDS", "TWO of DIAMONDS", "THREE of DIAMONDS", "FOUR of DIAMONDS", "FIVE of DIAMONDS", "SIX of DIAMONDS", "SEVEN of DIAMONDS", "EIGHT of DIAMONDS", "NINE of DIAMONDS", "TEN of DIAMONDS", "JACK of DIAMONDS", "QUEEN of DIAMONDS", "KING of DIAMONDS"};
	//our deck
	private Set<String> deck;
	//value of each card
	private Map<String, Integer> values;
	//creates the deck and creates the values
	public Deck() {
		deck = new HashSet<String>();
		values = new HashMap<String, Integer>();
		for(int i=0; i < 52; i++) {
			deck.add(cards[i]);
			int k = (i % 13) + 1;
			if(k > 10)
				k = 10;
			values.put(cards[i], k);
		}
	}
	//when we need to assign a card to a player or the dealer,
	//this function is called
	//it randomly selects a card and removes it from the deck
	public String getCard() {
		Random rNumber = new Random();
		Random rNumber2 = new Random();
		String card;
		while(true) {
			int x = rNumber.nextInt(4);
			int y = rNumber2.nextInt(13);
			if(deck.contains(cards[x*13 + y])) {
				card = cards[x*13 + y];
				deck.remove(card);
				break;
			}
		}
		return card;
	}
	//gives the value of the card
	public int getValue(String card) {
		return values.get(card);
	}
	//getters and setters
	public Set<String> getDeck() {
		return deck;
	}
	public void setDeck(Set<String> deck) {
		this.deck = deck;
	}
	public Map<String, Integer> getValues() {
		return values;
	}
	public void setValues(Map<String, Integer> values) {
		this.values = values;
	}
	//it resets the deck for the other rounds
	public void resetDeck() {
		deck.clear();
		for(int i=0; i < 52; i++) {
			deck.add(cards[i]);
		}
	}
}
