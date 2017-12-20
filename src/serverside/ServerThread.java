package serverside;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import message_objects.GameMessage;
import server.GameRoom;

//serverthread connects the client and the server
public class ServerThread extends Thread {
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private GameRoom gr;
	//receives the socket and the server
	public ServerThread(Socket s, GameRoom gr) {
		try {
			this.gr = gr;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread constructor: " + ioe.getMessage());
		}
	}
	//sends the message to the client
	public void sendMessage(GameMessage gm) {
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	//always receives the messages from client
	public void run() {
		while(true) {
			try {
				//reads the message and sends it to the server
				GameMessage gm = (GameMessage)ois.readObject();
				gr.broadcast(gm, this);
			} catch (IOException ioe) {
				break;
				//System.out.println("ioe in ServerThread.run(): " + ioe.getMessage());
			} catch (ClassNotFoundException cnfe) {
				break;
				//System.out.println("cnfe: " + cnfe.getMessage());
			}
			
		}
		
	}
}