# Blackjack - Standalone Networked Game

- Please read Description.pdf for the details of this project.

Implementation:

- There are objects, message_objects, server, serverside, client packages. All message classes that I use to communicate between client and the server are inside of message_objects package. GameMessage is the parent class of BiddingMessage, PlayingMessage, JoinMessage, and CheckMessage. The other object such as Game, Player, and Deck are inside of objects package. My server code is inside of server package which is GameRoom. GamePlay is a class that I use for the functionality when players finish bidding. ServerThread is used to communicate between server and client. GamePlay and ServerThread are inside of serverside package. 

- Please run GameRoom.java under the server package. This will start the server side of the program. Its main method is in this class.

- Please run Client.java under the client package to start the client side. Its main method in this class. You can run this as many times as you want. Depends on how many players you would like to create.

- And enjoy the game! 

