package serverside;

import message_objects.BiddingMessage;
import message_objects.GameMessage;
import message_objects.PlayingMessage;
import objects.Game;
import objects.Player;

//this is the place where game functionality is
public class GamePlay {
	private Game game;
	public GamePlay(Game game) {
		this.game = game;
	}
	//constructor receives the message that comes from the client
	public void Play(GameMessage gm) {
		if(game.getStatus() == 1) {
			GameMessage reply = new GameMessage(game.getName());
			reply.setStatus(1);
			reply.setMessage("Dealer is shuffling cards...");
			for(Player p : game.getPlayers()) {
				p.getSt().sendMessage(reply);
			}
			game.setStatus(2);
		}
		//bidding state of the game
		if(game.getStatus() == 2) {
			biddingPlay(gm);
		}
		//playing state of the game
		if(game.getStatus() == 3) {
			playingStatus(gm);
		}
		// after receiving the hit or stay answer from the player
		//it does this part and sends the message to all users
		if(game.getStatus() == 4) {
			Player player = game.getPlayers().get(game.getTurn());
			player.setTurn(1);
			String message1 = "You ";
			String message2 = player.getUsername() + " ";
			if(gm instanceof PlayingMessage) {
				int choice = ((PlayingMessage) gm).getChoice();
				if(choice == 2) {
					message1 += "hit. You were dealt the ";
					message2 += "hit. They were dealt the ";
					String card = game.getDeck().getCard();
					message1 += card;
					message2 += card;
					//adds card to player's hand when they hit
					player.addCard(card, game.getDeck().getValue(card));
					if(player.getTotal() > 21) {
						message1 += "\nYou busted. You lose " + player.getBet() + " chips\n";
						message2 += "\n" + player.getUsername() + " busted. They lose " + player.getBet() + " chips\n";
						player.setBusted(true);
						message1 += printGame(player);
						message2 += printGame(player);
					}
				}
				//when the player stays
				else {
					//finalize the total when they stay
					for(int i=0; i < player.getAces(); i++) {
						if(player.getTotal()+10 <= 21)
							player.setTotal(player.getTotal()+10);
						else
							break;
					}
					message1 += " stayed.\n" + printGame(player);
					message2 += " stayed.\n" + printGame(player);
					if(player.getTotal() == 21)
						player.setBlackjack(true);
					
				}
				GameMessage reply = new GameMessage(game.getName());
				reply.setStatus(3);
				if(choice == 2 && !player.isBusted()) {
					message1 += "\nEnter either '1' or 'stay' to stay. Enter either '2' or 'hit' to hit.";
					reply.setTurn(player.getUsername());
				}
				else
					reply.setTurn("");
				reply.setMessage(message1);
				player.getSt().sendMessage(reply);
				reply.setMessage(message2);
				for(Player p : game.getPlayers())
					if(p.getSt() != player.getSt())
						p.getSt().sendMessage(reply);
				//if the player stays or busted, ask the next player to hit or stay
				if(choice == 1 || player.isBusted()) {
					playCards(gm);
				}
			}
		}
	}
	
	//the next part of hit or stay and the dealer's turn is in this function too
	public void playCards(GameMessage gm) {
		game.setTurn(game.getTurn()+1);
		
		while(game.getTurn() != game.getPlayers().size() && game.getPlayers().get(game.getTurn()).isBusted())
		{
			game.setTurn(game.getTurn()+1);
		}
		
		//the dealer's turn to play
		if(game.getTurn() == game.getPlayers().size()) {
			game.setTurn(-1);
			GameMessage reply = new GameMessage(game.getName());
			String message = "It is now time for the dealer to play.\n";
			String message2 = "";
			int cnt = 0;
			Player player = game.getDealer();
			while(player.getTotal() < 17) {
				//check the case in which aces' values are 11
				if(player.getAces() > 0) {
					int x = player.getTotal();
					boolean flag = false;
					for(int i=0; i < player.getAces(); i++) {
						if(x + 10 <= 21 && x + 10 >= 17) {
							x += 10;
							flag = true;
							break;
						}
						else if(x + 10 <= 21)
							x += 10;
					}
					if(flag)
						player.setTotal(x);
				}
				// assign card to the dealer until its total is equal or more than 17
				if(player.getTotal() < 17) {
					String card = game.getDeck().getCard();
					int value = game.getDeck().getValue(card);
					player.addCard(card, value);
					cnt++;
					if(cnt != 1)
						message2 += ", ";
					message2 += "the " + card;
				}
			}
			//player is the dealer and check whether they had blackjack or busted
			if(player.getTotal() == 21)
				player.setBlackjack(true);
			else if(player.getTotal() > 21)
				player.setBusted(true);
			if(cnt != 0)
				message += "The dealer hit " + cnt + " times. They were dealt: " + message2 + "\n";
			else 
				message += "The dealer stayed.\n";
			message += printGame(player);
			reply.setTurn("");
			//send the result of the ended round to everyone
			for(Player p : game.getPlayers()) {
				reply.setMessage(message + printResult(p));
				p.getSt().sendMessage(reply);
			}
			game.setEnd();
			//if game is not ended, start the next round
			if(!game.isEnded()) {
				game.setStatus(1);
				beforeNextRound();
				Play(new GameMessage(game.getName()));
			}
			//if game is ended, print the winners and losers
			else
				printWinners();	
		}
		else {
			// sends the hit or stay message here and moves on to next game status 
			//which is the part that it receives the hit or stay message from the client (game status 4)
			Player player = game.getPlayers().get(game.getTurn());
			player.setTurn(1);
			GameMessage reply = new GameMessage(game.getName());
			reply.setStatus(3);
			game.setStatus(4);
			reply.setTurn(player.getUsername());
			reply.setMessage("It is your turn to add cards to your hand\nEnter either '1' or 'stay' to stay. Enter either '2' or 'hit' to hit.");
			player.getSt().sendMessage(reply);
			reply.setMessage("It is " + player.getUsername() + "'s turn to add cards to their hand.");
			for(Player p : game.getPlayers()) {
				if(p.getSt() != player.getSt())
					p.getSt().sendMessage(reply);
			}
		}
	}
	
	//this function prints the card status of the players
	//in the beginning of the round, it prints everyone's statuses
	//later it only prints the player's status that it receives as a parameter
	//if player is not null then it means this function should only print player's status
	public String printGame(Player player) {
		String message = "";
		// this part executes only in the beginning of the round
		if(player == null) {
			message = "----------------------------------------------\n";
			message += "DEALER\n\n";
			message += "Cards: | ? | ";
			for(int i=1; i < game.getDealer().getCards().size(); i++)
				message += game.getDealer().getCards().get(i) + " |";
			message += "\n----------------------------------------------\n";
		}
		// if flag is true then it means player is the dealer
		// we want to see the dealer's status which is printed at the end of the round
		boolean flag = false;
		if(player != null && player.getUsername().equals("")) {
			flag = true;
		}
		for(Player p : game.getPlayers()) {
			// this is the part that it check if the player is not the dealer and p is the player that we wanted
			if(!flag && player != null && p != player)
				continue;
			else if(flag)
				p = player;
			message += "----------------------------------------------\n";
			if(flag)
				message += "DEALER\n\n";
			else
				message += "Player: " + p.getUsername() + "\n";
			message += "Status: ";
			if(p.getTotal() == 21)
				message += "21 - blackjack";
			//check if it has an ace and when its value is 11 whether it will make 21
			else if(p.getAces() > 0 && p.getTotal()+10 == 21)
				message += "21 - blackjack";
			else if(p.getTotal() > 21)
				message += p.getTotal() + " - bust";
			// calculate the other statuses according to the aces
			else {
				message += p.getTotal();
				int x = p.getTotal();
				for(int i=0; i < p.getAces(); i++) {
					x += 10;
					if(x < 21) {
						message += " or " + x;
						continue;
					}
					else if(x == 21) {
						message += " or " + x + " - blackjack";
					}
					break;
				}
			}
			// print cards
			message += "\nCards: | ";
			for(int i=0; i < p.getCards().size(); i++) {
				message += p.getCards().get(i) + " | ";
			}
			message += "\n";
			if(!flag)
				message += "Chip total: " + p.getChips() + " | Bet Amount: " + p.getBet() + "\n";
			message += "----------------------------------------------\n";
			if(flag)
				break;
		}
		return message;
	}
	
	//prints result at the end of a round
	public String printResult(Player player) {
		String message = "";
		for(Player p : game.getPlayers()) {
			if(p.getUsername() == player.getUsername()) {
				continue;
			}
			if(game.getDealer().isBlackjack()) {
				//checks for every case in which the dealer had blackjack
				if(p.isBusted()) 
					message += p.getUsername() + " busted. " + p.getBet() + " chips were deducted from " + p.getUsername() + "'s total\n";
				else if(p.isBlackjack())
					message += p.getUsername() + " tied with the dealer. " + p.getUsername() + "'s chip total remains the same\n";
				else 
					message += p.getUsername() + " had a sum less than the dealer's. " + p.getBet() + " chips were deducted from " + p.getUsername() + "'s total\n";
			}
			else if(game.getDealer().isBusted()) {
				//checks for every case in which the dealer busted
				if(p.isBusted()) 
					message += p.getUsername() + " busted. " + p.getBet() + " chips were deducted from " + p.getUsername() + "'s total\n";
				else if(p.isBlackjack())
					message += p.getUsername() + " had blackjack. " + (p.getBet()*2) + " chips were added to " + p.getUsername() + "'s total\n";
				else 
					message += p.getUsername() + " did not bust but the dealer busted. " + p.getBet() + " chips were added to " + p.getUsername() + "'s total\n";	
			}
			else {
				// checks for every case in which the dealer neither busted nor had blackjack
				if(p.isBlackjack())
					message += p.getUsername() + " had blackjack. " + (p.getBet()*2) + " chips were added to " + p.getUsername() + "'s total\n";
				else if(p.isBusted())
					message += p.getUsername() + " busted. " + p.getBet() + " chips were deducted from " + p.getUsername() + "'s total\n";
				else if(p.getTotal() > game.getDealer().getTotal())
					message += p.getUsername() + " had a sum greater than the dealer's. " + p.getBet() + " chips were added to " + p.getUsername() + "'s total\n";
				else if(p.getTotal() < game.getDealer().getTotal())
					message += p.getUsername() + " had a sum less than the dealer's. " + p.getBet() + " chips were deducted from " + p.getUsername() + "'s total\n";
				else
					message += p.getUsername() + " tied with the dealer. " + p.getUsername() + "'s chip total remains the same\n";
			}
		}
		int x = 0;
		// this part is personalized to the client and x is the change that will happen to their total
		if(game.getDealer().isBlackjack()) {
			//checks for every cases in which the dealer had blackjack
			if(player.isBlackjack())
				message += "You tied with the dealer. Your chip total remains the same";
			else if(player.isBusted()) {
				message += "You busted. " + player.getBet() + " chips were deducted from your total";
				x = player.getBet() * -1;
			}
			else {
				message += "You had a sum less than the dealer's. " + player.getBet() + " chips were deducted from your total";
				x = player.getBet() * -1;
			}
		}
		else if(game.getDealer().isBusted()) {
			//checks for every case in which the dealer busted
			if(player.isBlackjack()) {
				message += "You had blackjack. " + (player.getBet()*2) + " chips were added to your total";
				x = player.getBet()*2;
			}
			else if(player.isBusted()) {
				message += "You busted. " + player.getBet() + " chips were deducted from your total";
				x = player.getBet() * -1;
			}
			else {
				message += "You did not bust but the dealer busted. " + player.getBet() + " chips were added to your total";	
				x = player.getBet();
			}
		}
		else {
			// checks for every case in which the dealer neither busted nor had blackjack
			if(player.isBlackjack()) {
				message += "You had blackjack. " + (player.getBet()*2) + " chips were added to your total";
				x = player.getBet()*2;
			}
			else if(player.isBusted()) {
				message += "You busted. " + player.getBet() + " chips were deducted from your total";
				x = player.getBet() * -1;
			}
			else if(player.getTotal() > game.getDealer().getTotal()) {
				message += "You had a sum greater than the dealer's. " + player.getBet() + " chips were added to your total";
				x = player.getBet();
			}
			else if(player.getTotal() < game.getDealer().getTotal()) {
				message += "You had a sum less than the dealer's. " + player.getBet() + " chips were deducted from your total";
				x = player.getBet() * -1;
			}
			else
				message += "You tied with the dealer. Your chip total remains the same";
		}
		player.setChips(player.getChips()+x);
		return message;
	}
	
	//ask for bet and receive the bet
	public void biddingPlay(GameMessage gm) {
		// if a bid is received, set the bet and print the messages
		if(gm instanceof BiddingMessage)
		{
			Player player = game.getPlayerMap().get(gm.getSender());
			player.setBet(((BiddingMessage) gm).getBid());
			GameMessage reply = new GameMessage(game.getName());
			reply.setStatus(2);
			reply.setMessage("You bet " + ((BiddingMessage) gm).getBid() + " chips");
			player.getSt().sendMessage(reply);
			reply.setMessage(player.getUsername() + " bet " + ((BiddingMessage) gm).getBid() + " chips");
			//send the bet that the player made to everyone
			for(Player p : game.getPlayers()) {
				if(p.getSt() != player.getSt())
					p.getSt().sendMessage(reply);
			}
		}
		// next player
		game.setTurn(game.getTurn()+1);
		// if everyone bet, then the game should continue on next status
		//which will start the game
		if(game.getTurn() == game.getPlayers().size()) {
			game.setTurn(-1);
			game.setStatus(3);
		}
		//ask for bet
		else {
			Player player = game.getPlayers().get(game.getTurn());
			player.setTurn(1);
			GameMessage reply = new GameMessage(game.getName());
			reply.setStatus(2);
			reply.setTurn(player.getUsername());
			reply.setMessage(player.getUsername() + ", "
					+ "it is your turn to make a bet. Your chip total is " 
					+ player.getChips());
			player.getSt().sendMessage(reply);
			reply.setMessage("It is " + player.getUsername() + "'s turn to make a bet.");
			//send the bet turn message to everyone
			for(Player p : game.getPlayers()) {
				if(p.getSt() != player.getSt())
					p.getSt().sendMessage(reply);
			}
			
		}
	}
	
	//assign two cards to each player and the dealer
	public void playingStatus(GameMessage gm) {
		if(gm instanceof BiddingMessage) {
			//the dealer assigns 2 cards to each player
			for(Player p : game.getPlayers()) {
				String card = game.getDeck().getCard(); 
				int value = game.getDeck().getValue(card);
				p.addCard(card, value);
				card = game.getDeck().getCard();
				value = game.getDeck().getValue(card);
				p.addCard(card, value);

			}
			// the dealer assigns cards to themselves
			String card = game.getDeck().getCard();
			int value = game.getDeck().getValue(card);
			game.getDealer().addCard(card, value);
			card = game.getDeck().getCard();
			value = game.getDeck().getValue(card);
			game.getDealer().addCard(card, value);
			GameMessage reply = new GameMessage(game.getName());
			reply.setStatus(3);
			reply.setMessage(printGame(null));
			for(Player p : game.getPlayers())
				p.getSt().sendMessage(reply);
		}
		// asks for stay or hit according to turn
		playCards(gm);
	}
	
	//make everything initial state for each user before the next round begins
	public void beforeNextRound() {
		GameMessage reply = new GameMessage(game.getName());
		reply.setTurn("");
		game.nextRound();
		//send next round message to everyone
		reply.setMessage("\n" + game.getRound());
		for(Player p : game.getPlayers()) {
			p.getCards().clear();
			p.setTotal(0);
			p.setBlackjack(false);
			p.setBet(0);
			p.setBusted(false);
			p.setAces(0);
			p.getSt().sendMessage(reply);
		}
		Player player = game.getDealer();
		player.getCards().clear();
		player.setTotal(0);
		player.setBlackjack(false);
		player.setAces(0);
		player.setBusted(false);
		game.getDeck().resetDeck();
	}
	
	//prints the winners and losers of the game
	public void printWinners() {
		GameMessage reply = new GameMessage(game.getName());
		reply.setTurn("");
		//this will give the signal to the client to terminate the program
		reply.setStatus(5);
		String message = "\nWINNERS:\n";
		String message2 = "LOSERS:\n";
		int max = -1;
		if(!game.isDWinner()) {
			max = 0;
			for(Player p : game.getPlayers()) {
				if(p.getChips() > max)
					max = p.getChips();
			}
		}
		if(game.isDWinner())
			message += "DEALER\n";
		for(Player p : game.getPlayers()) {
			if(p.getChips() == max)
				message += p.getUsername() + "\n";
			else if(p.getChips() == 0)
				message2 += p.getUsername() + "\n";
		}
		//send the winners/losers message to all players
		reply.setMessage(message + message2);
		for(Player p : game.getPlayers()) {
			p.getSt().sendMessage(reply);
		}
	}
}


