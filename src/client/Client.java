package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import message_objects.BiddingMessage;
import message_objects.CheckMessage;
import message_objects.GameMessage;
import message_objects.JoinMessage;
import message_objects.PlayingMessage;
import objects.Player;

//client side which is the player and has the player functionality
public class Client extends Thread {
	//private variables of the class
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private int status;
	private int turn;
	private String gname;
	private Player player;
	private int pn;
	private String username;
	//this is the constructor and prepares the initial state of the client side
	public Client(String hostname, int port) throws Exception {
		Socket s = new Socket(hostname, port);
		ois = new ObjectInputStream(s.getInputStream());
		oos = new ObjectOutputStream(s.getOutputStream());
		this.start();
		Scanner scan = new Scanner(System.in);
		while(true) {
			//after connecting to the server, the options will appear for the user
			System.out.println("Please choose from the options below");
			System.out.println("1) Start Game");
			System.out.println("2) Join Game"); 
			String line = scan.nextLine();
			line.replaceAll(" ", "");
			// start game option
			if(line.equals("1")) {
				while(true) {
					System.out.println("Please choose the number of players in the game (1-3)");
					line = scan.nextLine();
					line.replaceAll(" ", "");
					if(!line.equals("1") && !line.equals("2") && !line.equals("3"))
						continue;
					//player number for the game
					pn = Integer.parseInt(line);
					//this function checks whether the game name is valid
					checkGameName(false);	
					break;
				}
				break;
			}
			//join game option
			else if(line.equals("2")) {
				//this function checks whether the game name is valid
				checkGameName(true);
				break;
			}
		}
	}
	//checks whether the given string is a number
	public static boolean isInteger(String str) {
		if(str.isEmpty())
			return false;
		int i = 0;
		if(str.charAt(0) == '-')
			i++;
		for(; i < str.length(); i++)
			if(!(str.charAt(i) <= '9' && str.charAt(i) >= '0'))
				return false;
		return true;
	}
	//when the client's turn comes, this function is called and they play
	public void play() {
		Scanner scan = new Scanner(System.in);
		String line = scan.nextLine();
		GameMessage gm = null;
		// betting (when the client should bet)
		if(status == 2)
		{
			while(!isInteger(line))
				line = scan.nextLine();
			gm = new BiddingMessage(player.getGname(), Integer.parseInt(line));
		}
		//playing (when it's client's turn to add cards into their hand)
		else if(status == 3) {
			line.replaceAll(" ", "");
			while(!line.equals("stay") && !line.equals("hit") && !line.equals("1") && !line.equals("2")) {
				line = scan.nextLine();
				line.replaceAll(" ", "");
			}
			gm = new PlayingMessage(player.getGname(), line);
		}
		//sends the message to the server
		if(gm != null) {
			gm.setSender(player.getUsername());
			try {
				oos.writeObject(gm);
				oos.flush();
			} catch (IOException ioe) {
				System.out.println("ioe in Client.Play(): " + ioe.getMessage());
			} 
		}
		//client's turn is over
		player.setTurn(0);
		turn = 0;
	}
	
	//this is the thread part and always reads the messages that come from the server
	public void run() {
		try {
			while(true) {
				//reads the game message from the server
				GameMessage gm = (GameMessage)ois.readObject();
				//CheckMessage is used to check game name and username
				if(gm instanceof CheckMessage) {
					// if it's not valid, it prints and error message and asks again
					if(!((CheckMessage) gm).isValid())
					{
						System.out.println(((CheckMessage) gm).getError());
						if(((CheckMessage) gm).isGname())
							checkGameName(((CheckMessage) gm).isJoin());
						else
							checkUsername();
					}
					//if it's valid then it creates the game or asks for username to join
					//if the user is creating a game then there is no need to check their username
					//since there is only one player
					else {
						if(!((CheckMessage) gm).isJoin())
							createGame();
						else {
							if(((CheckMessage) gm).isGname())
								checkUsername();
							else
								joinGame();
						}
					}
				}
				//if it is not a CheckMessage, then it means its client's turn to play
				else {
					System.out.println(gm.getMessage());
					status = gm.getStatus();
					if(gm.getTurn().equals(player.getUsername()))
					{
						player.setTurn(1);
						turn = 1;
						play();
					}
					//status 5 means game ended and client stops receiving messages from the server
					if(status == 5)
						break;
				}
			}
		} catch (IOException ioe) {
			System.out.println("ioe in Client.run(): " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		}
	}

	
	public static void main(String [] args) {
		System.out.println("Welcome to Black Jack!");
		while(true)
		{
			//receiving ipaddress and port to connect to the server
			System.out.println("Please enter the ipaddress");
			Scanner sc = new Scanner(System.in);
			String ipaddress = sc.nextLine();
			System.out.println("Please enter the port");
			String portstr = sc.nextLine();
			//after connecting to the server, it creates the client side
			try {
				Client cc = new Client(ipaddress, Integer.parseInt(portstr));
				break;
			} catch (Exception e) {
				System.out.println("Unable to connect to the servers with provided fields");
			}
		}
	}
	
	//asks for a game name and sends a message to the server to check whether it's valid
	public void checkGameName(boolean join) {
		Scanner scan = new Scanner(System.in);
		//asks for a game name
		if(!join)
			System.out.println("Please choose a name for your game");
		else
			System.out.println("Please enter the name of the game you wish to join");
		gname = scan.nextLine();
		//creates the CheckMessage to check the game name in the server
		CheckMessage cm = new CheckMessage("");
		cm.setJoin(join);
		cm.setGname(true);
		cm.setUsername(false);
		cm.setName(gname);
		//sends the message
		try {
			oos.writeObject(cm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//asks for a username and sends a message to the server to check whether it's valid
	public void checkUsername() {
		Scanner scan = new Scanner(System.in);
		username = "";
		//asks for a username
		while(username.equals("")) {
			System.out.println("Please choose a username");
			username = scan.nextLine();
		}
		//creates a CheckMessage to check username in the server
		CheckMessage cm = new CheckMessage(gname);
		cm.setUsername(true);
		cm.setGname(false);
		cm.setName(username);
		cm.setJoin(true);
		try {
			oos.writeObject(cm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//this function sends a message to the server to create a game 
	//and assign this user as the first player
	public void createGame() {
		Scanner scan = new Scanner(System.in);
		username = "";
		//after confirming the game name, it asks for a username
		//and since this user creates the game, there is no need to check username
		//it only checks whether it is an empty string
		while(username.equals("")) {
			System.out.println("Please choose a username");
			username = scan.nextLine();
		}
		player = new Player(gname, username, null);
		GameMessage gm = new JoinMessage(gname, pn, player, false);
		gm.setSender(username);
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//this function sends a message to the server to assign the user to the desired game as a player
	public void joinGame() {
		player = new Player(gname, username, null);
		GameMessage gm = new JoinMessage(gname, -1, player, true);
		gm.setSender(username);
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
