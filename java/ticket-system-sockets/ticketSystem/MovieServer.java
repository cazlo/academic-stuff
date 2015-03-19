//============================================================================
// Name        : MovieServer
// Author      : Andrew Paettie
// Version     : 1
// Copyright   : 
// Description : A server for a movie ticketing system.  Listens for a client 
//               and when one connects, starts a new thread to service them
//				 Due to this the server has concurrency control features to 
//               prevent a multithreaded mess.
//============================================================================

package ticketSystem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MovieServer {
	static int portNum = 3307;//default to a crazy port that noone uses 
	static String filename;
	public ServerSocket ss = null;
	//Socket s = null;
	Semaphore accessSeats = new Semaphore(1);
	static ArrayList<Movie> movies = new ArrayList<>();
	
	//constructor which opens a connection to a client and then runs a thread 
	//when a client connects to process client interactions
	public MovieServer(int port){
		try {
			ss = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true){
			try {
				
				System.out.println("Waiting for client to connect...");
				//s = ss.accept();//waits for a client to connect
				Socket s = ss.accept();
				MovieServerThread m = new MovieServerThread(s);
				m.start();
				//System.out.println("Got back from thread");//DEGUG
				//System.out.println("test");//DEBUG
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;//kill server if there is a problem with the socket
			} catch (Exception ex){
				System.out.println("General exception caught");
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		if (args.length <  2){
			printHelp();//why would you run without arguments?
			System.exit(1);
		}
		try{
			portNum  = Integer.parseInt(args[0]);//try to convert the port to an int
			filename = args[1];
		}catch(NumberFormatException ex){
			System.out.println("Error: invalid port number");
			System.out.println(ex);
			System.exit(1);
		}
		if (loadedMoviesFile()){
			//successfully loaded the movies file
		}else{
			System.out.println("Exiting...");
			System.exit(1);
		}
		
		new MovieServer(portNum);
	}
	
	//returns true of loaded movie file ok and false otherwise
	public static boolean loadedMoviesFile(){
		try{
		FileInputStream fis = new FileInputStream(filename);
		BufferedReader in = new BufferedReader(new InputStreamReader(fis));
		String line;
		movies = new ArrayList<>();
		
		while ((line = in.readLine()) != null){
			String [] result = line.split("\\s");
			try{
				int numSeats = Integer.parseInt(result[result.length-1]);
				String movieTitle = "";
				for (int i = 0; i < (result.length-1); i++){
					if (i == result.length-1){
						movieTitle = movieTitle + result[i];
					}else{
						movieTitle = movieTitle + result[i] + " ";
					}
				}
				movieTitle = movieTitle.trim();
				movies.add(new Movie(movieTitle, numSeats));
			}catch (Exception e){
				in.close();
				System.out.println("Error parsing file '" + filename + "'\n :: "+e);
				return false;
			}
		}
		in.close();
		return true;
		
		}catch(Exception ex){
			System.out.println("Error loading file '" + filename + "'\n :: "+ex);
			return false;
		}
	}
	//function to print program usage in case that user is dumb and incorrectly uses program
	public static void printHelp(){
		System.out.println(
				"Program usage: java ticketSystem.MovieServer <port to listen on> <path to movie file>");
	}
	private class MovieServerThread extends Thread implements Runnable{

		private Socket s = null;
		private ObjectInputStream in = null;
		private ObjectOutputStream out = null;
		public boolean keepRunning = true;
		
		public MovieServerThread(Socket _s){
			System.out.println("Created thread to handle new client");
			s = _s;
		}
		
		@Override
		public void run() {
			try {
				//yes using bjectInputStream means that client and server
				//must both be java, but it also means that is just works
				//(don't have to mess with encoding issues, etc.)
				in = new ObjectInputStream(s.getInputStream());
				out = new ObjectOutputStream(s.getOutputStream());
				while (keepRunning){
					ClientMessage m = (ClientMessage) in.readObject();
					if (m == null){
						keepRunning = false;
					}else{
						//System.out.println("'"+lineIn+"'");//DEBUG
						//Double d = Double.parseDouble(lineIn);//this is wierd but have to do this to avoid 
						handleInstruction(m);      //number format exception.  (Why can't I parse "0"?)
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			System.out.println("Thread terminating...");
		}
		
		public void handleInstruction(ClientMessage m){
			
			switch (m){
			case REQUEST_MOVIES:
				System.out.println("Got request for movie list");
				try{
					out.writeInt(movies.size());
					//out.newLine();//first tell the client the num of movies
					for (int i = 0; i < movies.size(); i ++){
						out.writeObject(movies.get(i).getName() );
						//out.newLine();
					}
				}catch (Exception e){
					//TODO: handle exception
					e.printStackTrace();
				}
				break;
			case REQUEST_TICKETS:
				System.out.println("Got request for ticket purchasing");
				int movNum = 0, numTickets = 0;
				try{
					//System.out.println("Semaphore value: "+accessSeats.availablePermits());//DEBUG
					accessSeats.acquire();//get a hold of semaphore
					//System.out.println("Aquired semaphore; value: "+accessSeats.availablePermits());//DEBUG
					movNum = in.readInt();//first client writes movie index, which is index in movie list + 1
					numTickets = in.readInt();//then num of tickets
					//System.out.println("numTickets: "+ numTickets);//DEBUG
				    
				    //nest a try-catch to ensure semaphore gets released if an exception is thrown
				    //sorry we're getting messy now
				    try{
				    	if ((movNum -1) < 0 || (movNum-1) >= movies.size()){//index out of bounds
				    		System.out.println("Requested movie index out of bounds");
				    		out.writeObject(ServerMessage.INVALID_QUERY);
							//out.newLine();
				    	}else if(movies.get(movNum-1).getSeats() < numTickets){//requested more seats than exist
				    		System.out.println("Unrealistic seats");
				    		out.writeObject(ServerMessage.INVALID_QUERY);
							//out.newLine();
				    	}else if(movies.get(movNum-1).getAvailable() < numTickets ){//not enough seats available
				    		out.writeObject(ServerMessage.SEATS_UNAVAILABLE);
							//out.newLine();
				    	}else{//exhausted all bad possibilites, so we must be ok to purchase
				    		switch ((ClientMessage) in.readObject()){
				    		case CONFIRM:
				    			int prvSeats = movies.get(movNum - 1).getHeldSeats(); //previous seats
				    			movies.get(movNum-1).setHeldSeats(prvSeats + numTickets);
				    			out.writeObject(ServerMessage.SEATS_AVAILABLE);
								//out.newLine();
				    			System.out.println(numTickets + 
				    					" seats successfully purchased for: " + 
				    					movies.get(movNum - 1).getName());
				    			break;
				    		case CANCEL:
				    			//don't need to do anything here, just release the semaphore later and move on
				    			System.out.println("Transaction canceled.  Releasing movies");
				    			break;
				    		default:
				    			//this should never happen, so die ungracefully
				    			System.out.println("woops...client is dumb");
				    			System.exit(1);
				    		}
				    	}
				    }catch(IOException | ClassNotFoundException ex2){
				    	ex2.printStackTrace();
				    }
				    accessSeats.release();//finally release 
				}catch (IOException ex){//for reading from socket
					//TODO: handle this
					ex.printStackTrace();
				} catch (InterruptedException e) {//for semaphore
					 //TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case CANCEL:
				System.out.println("Got exit request");
				try {
					//in.close();
					//s.close();//close the stream
					in.close();
					out.close();
					keepRunning = false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
