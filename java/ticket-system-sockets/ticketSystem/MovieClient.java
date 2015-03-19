//============================================================================
// Name        : MovieClient
// Author      : Andrew Paettie
// Version     : 1
// Copyright   : 
// Description : The client for a movie ticketing system.  Talks to the server
//               via sockets with commands related to purchasing tickets.
//============================================================================

package ticketSystem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MovieClient {
	static String serverHostname;
	static int serverPort;
	static Socket server = null;
	
	static ObjectOutputStream serverOut = null;
	static ObjectInputStream serverIn = null;
	static ArrayList<Movie> movies = new ArrayList<>();
	
	public static void main(String[] args){
		if (args.length != 2){
			printHelp();
			System.exit(1);
		}
		try{
			serverHostname = args[0];
			serverPort = Integer.parseInt(args[1]);
		}catch (NumberFormatException ex){
			System.out.println("Error parsing to int " + args[1]);
			ex.printStackTrace();
		}
		connectToServer();
		doMenu();
		closeConnection();
	}
	
	public static void connectToServer(){
		try {
			server = new Socket(serverHostname, serverPort);
			System.out.println("Connected to server");
			serverOut = new ObjectOutputStream(server.getOutputStream());
			
			serverIn = new ObjectInputStream(server.getInputStream());
		} catch (Exception e) {
			System.out.println("Error connecting to server!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void closeConnection(){
		try {
			serverOut.close();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void doMenu(){
		boolean keepGoing = true;
		while (keepGoing){
			String userIn = 
			  System.console().readLine("A.  Display the list of movies\n"+
				  			            "B.  Purchase tickets\n"+
						                "C.  Exit\n" +
						                "Enter a choice: ");
			userIn = userIn.trim();//get rid of any excessive whitespace
			switch(userIn.charAt(0)){// check out the first char the user typed in
			case 'a':
			case 'A'://case insensitive
				try {
					serverOut.writeObject(ClientMessage.REQUEST_MOVIES);
					int movieCount = serverIn.readInt();
					//System.out.println("READING "+movieCount+" MOVIES...");//DEBUG
					System.out.println("Movie list:");
					for (int i = 1; i <= movieCount; i++){
						//String lineRead = serverIn.readLine();
						String lineRead = (String)serverIn.readObject();
						System.out.println("   "+i+".   "+lineRead);
					}
					System.out.println("");//add line break between end of list and menu
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {//for a String! seriously java?!>?
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 'b':
			case 'B':
				//send (ClientMessage.REQUEST_TICKETS);
				try{
					userIn = System.console().readLine(
							"Enter the number of the movie in the list: ");
					//Double d = Double.parseDouble(userIn);
					int movieIndex = Integer.parseInt(userIn);
					//int movieIndex = d.intValue();
					userIn = System.console().readLine(
							"Enter the number of tickets: ");
					//d = 0.0;
					//d = Double.parseDouble(userIn);
					int numTickets = Integer.parseInt(userIn);
					//int numTickets = d.intValue();
					
					//send request for tickets so it gets held until confirmation
					serverOut.writeObject(ClientMessage.REQUEST_TICKETS);
					serverOut.flush();//write this immediately so sempahore works right 
									  //(actually I don't think this is needed, but program is working, don't want to take it out :P)
					serverOut.writeInt(movieIndex );//then send movie #
					serverOut.writeInt(numTickets);//then send num tickets
					serverOut.flush();
					boolean invalid = false;//for handling user not entering 1 or 2
					do{
						userIn = System.console().readLine(
								   "Enter 1 to confirm or 2 to cancel: ");
						System.out.println(userIn.charAt(0));
						switch (userIn.charAt(0)){
						case '1':
							//serverOut.write(ClientMessage.CONFIRM.ordinal());
							serverOut.writeObject(ClientMessage.CONFIRM);
							System.out.println("Processing request..."); //say something while the user waits for other clients to get their turn
							ServerMessage s = (ServerMessage) serverIn.readObject();
							switch (s){
							case SEATS_AVAILABLE:
								System.out.println("Purchase successful. " +
										"Proceed to theater to make payment and pickup tickets.");
								break;
							case SEATS_UNAVAILABLE:
								System.out.println("Sorry these seats are unavailable at this time.  Try back later");
								break;
							case INVALID_QUERY:
								System.out.println("This is not a valid query (probably not enough seats in theater)");
								break;
							default:
								//this should really never happen
								System.out.println("Some internal program error has happened");
								//System.out.println("Server response: "+ s);//DEBUG
								System.exit(1);
								break;
							}
							invalid = false;//for handling invalid option
							break;
						case '2':
							System.out.println("Transaction canceled");
							invalid = false;
							serverOut.writeObject(ClientMessage.CANCEL);
							break;
						default:
							System.out.println("Invalid Option");
							invalid = true;
						}
					}while(invalid);
				}catch (NumberFormatException ne){
					//handle user entered invalid info 
					System.out.println("You entered something wrong.\n" +
							"Here's scary text so you don't do it again:");
					ne.printStackTrace();
				}catch(Exception ex){
					//TODO handle generic exception
					System.out.println("Some general problem happened.  Scary text:");
					ex.printStackTrace();
				}
				break;
			case 'c':
			case 'C':
				//send(ClientMessage.CANCEL);
				try {
					serverOut.writeObject(ClientMessage.CANCEL);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					keepGoing = false;
				}
				break;
			default:
				System.out.println("Error: please enter only A,B, or C");
			}
		}
	}
	
	//function to print program usage in case that user is dumb and incorrectly uses program
	public static void printHelp(){
		System.out.println(
				"Program usage: MovieClient <server name> <server port>");
	}
}
