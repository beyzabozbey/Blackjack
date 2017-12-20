package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import message_objects.CheckMessage;
import message_objects.GameMessage;
import message_objects.JoinMessage;
import objects.Game;
import objects.Player;
import serverside.GamePlay;
import serverside.ServerThread;

//the server's functionality is here
public class GameRoom {
	private Vector<ServerThread> players;
	private Map<String, Game> games;
	// starts the server
	public GameRoom(int port) {
		ServerSocket ss = null;
		games = new HashMap<String, Game>();
		try {
			ss = new ServerSocket(port);
			System.out.println("Successfully started the Black Jack Server on port " + port);
			players = new Vector<ServerThread>();
			//always receives a client here
			while(true) {
				Socket s = ss.accept(); //connects to the client
				ServerThread st = new ServerThread(s, this);
				players.add(st);
			}
		} catch (IOException ioe) {
			System.out.println("ioe in GameRoom constructor: " + ioe.getMessage());
		} finally {
			try {
				if(ss != null)
					ss.close();
			} catch (IOException e) {
				System.out.println("e in GameRoom constructor: " + e.getMessage());
			}
		}
	}
	
	//checks whether the game name or username is valid
	public void checkGameNameorUsername(GameMessage gm, ServerThread st) {
		CheckMessage cm = (CheckMessage)gm;
		CheckMessage reply = new CheckMessage("");
		//checks game name
		if(cm.isGname() && cm.isJoin()) {
			reply.setGname(true);
			reply.setUsername(false);
			reply.setJoin(true);
			if(games.containsKey(cm.getName())) {
				Game game = games.get(cm.getName());
				if(game.getRemaining() > 0)
					reply.setValid(true);
				else {
					reply.setValid(false);
					//sets error message to send
					reply.setError("Invalid choice. The game already has " + game.getPlayerNum() + " players");
				}
			}
			else {
				reply.setValid(false);
				reply.setError("Invalid choice. There are no ongoing games with this name");
			}
		}
		//if it is for game name check but the user is not joining they are creating
		else if(cm.isGname() && !cm.isJoin()) {
			reply.setGname(true);
			reply.setUsername(false);
			reply.setJoin(false);
			if(cm.getName().equals("")) {
				reply.setValid(false);
				reply.setError("Invalid choice. Please enter a game name");
			}
			else if(games.containsKey(cm.getName())) {
				reply.setValid(false);
				reply.setError("Invalid choice. This game name has already been chosen by another user");
			}
			else
				reply.setValid(true);
		}
		//checks username
		else {
			reply.setGname(false);
			reply.setUsername(true);
			reply.setJoin(true);
			if(games.get(cm.getGname()).getPlayerMap().containsKey(cm.getName())) {
				reply.setValid(false);
				reply.setError("Invalid choice. This username has already been chosen by another player in this game");
			}
			else
				reply.setValid(true);
		}
		st.sendMessage(reply);
	}
	
	//this function evaluates the message that comes from the client
	//and it creates reply messages that should be seen on client's side and sends them to the clients
	public void broadcast(GameMessage gm, ServerThread st) {
		if (gm != null) {
			//checks whether the game name or username is valid
			if(gm instanceof CheckMessage) {
				checkGameNameorUsername(gm, st);
				return;
			}
			Game game = null;
			if(games.containsKey(gm.getGname()))
				game = games.get(gm.getGname());
			if(gm instanceof JoinMessage ) {
				JoinMessage jm = (JoinMessage) gm;
				boolean join = jm.getJoin();
				//here, it creates a game and assigns the client as a player
				if(!join) {
					Player player = jm.getPlayer();
					player.setSt(st);
					game = new Game(jm.getGname(), jm.getPn(), player);
					games.put(jm.getGname(), game);
				}
				//here, it adds the client to an existing game as a player
				else {
					Player player = jm.getPlayer();
					player.setSt(st);
					game = games.get(jm.getGname());
					game.join(player);
					GameMessage reply = new GameMessage(jm.getGname());
					reply.setMessage(jm.getSender() + " joined the game");
					game.getOwner().getSt().sendMessage(reply);
					
				}
				GameMessage reply2 = new GameMessage(jm.getGname());
				reply2.setStatus(game.getStatus());
				//if we reached the max number of players, the game starts
				if(game.getStatus() != 0) {
					reply2.setMessage("Let the game commence. Good luck to all players!");
					for(Player p : game.getPlayers()) 
						p.getSt().sendMessage(reply2);
					GamePlay gameplay= new GamePlay(game);
					gameplay.Play(gm);
				}
				// if not, the server sends a waiting message
				else {
					reply2.setTurn("");
					reply2.setMessage("Waiting for other " + game.getRemaining() + " players to join...");
					game.getOwner().getSt().sendMessage(reply2);
					if(join)
					{
						reply2.setMessage("The game will start shortly. Waiting for other players to join...");
						st.sendMessage(reply2);
					}
				}		
			}
			else {
				//if it is not a JoinMessage, then the game has already started so the clients are sending 
				//bet or playing messages
				//GamePlay is the place where the all functionality of the game is
				//meaning: it is where the magic happens
				GamePlay gameplay= new GamePlay(game);
				gameplay.Play(gm);
				//here, it checks whether there is a game in the list that has ended
				//so it removes that game
				Iterator it = games.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<String, Game> pair = (Map.Entry<String, Game>)it.next();
			        if(((Game)pair.getValue()).isEnded())
			        		it.remove();
			    }
			}
		}
	}
	//this is the main function and it receives the port to set up everything
	public static void main(String [] args) {
		System.out.println("Welcome to the Black Jack Server!");
		int port;
		while(true)
		{
			//receives the port
			System.out.println("Please enter a port");
			Scanner sc = new Scanner(System.in);
			String str = sc.next();
			if(isPort(str))
			{
				port = Integer.parseInt(str);
				break;
			}
			else
				System.out.println("Invalid port number.");
		}
		GameRoom gr = new GameRoom(port);
	}
	//checks whether the port is valid
	public static boolean isPort(String str) {
	    if (str == null)
	        return false;
	    if (str.length() == 0)
	        return false;
	    for (int i=0; i < str.length(); i++) {
	        if (!(str.charAt(i) >= '0' && str.charAt(i) <= '9'))
	            return false;
	    }
	    int port = Integer.parseInt(str);
	    if(!(port >= 1024 && port <= 49151))
	    		return false;
	    return true;
	}
}
