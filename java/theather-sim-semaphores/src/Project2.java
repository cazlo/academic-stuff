package theaterSim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Project2 {
	public static final int NUM_CUSTOMERS = 300;
	
	//Semaphores
	//protected static final Semaphore assignID = new Semaphore(1);
	protected static final Semaphore readMovies = new Semaphore(1);
	protected static final Semaphore availableAgent = new Semaphore(0);
	protected static final Semaphore getTransactionInfo = new Semaphore(1);
	protected static final Semaphore cust_ready = new Semaphore(0);
	protected static final Semaphore soldOutResponse [] = new Semaphore[NUM_CUSTOMERS];
	protected static final Semaphore buyTicket [] = new Semaphore[NUM_CUSTOMERS];
	protected static final Semaphore availableTT = new Semaphore(1);
	protected static final Semaphore cust_ready_tt = new Semaphore(0);
	protected static final Semaphore tore_ticket [] = new Semaphore [NUM_CUSTOMERS];
	protected static final Semaphore availableCS = new Semaphore(1);
	protected static final Semaphore cust_ready_cs = new Semaphore(0);
	protected static final Semaphore orderFilled [] = new Semaphore [NUM_CUSTOMERS];
	protected static final Semaphore leftTheater [] = new Semaphore[NUM_CUSTOMERS];
	//protected static final Semaphore accessID = new Semaphore(1);
	//protected static final Semaphore[] findID = new Semaphore[NUM_CUSTOMERS];
	protected static final Semaphore accessMovies = new Semaphore(1);
	//protected static final Semaphore accessHash = new Semaphore(1);
	//protected static final Semaphore accessConcession = new Semaphore(1);
	
	
	protected static String filename = "";//filename for movies file
	protected static ArrayList<Movie> movies;
	protected static Queue<TicketTransaction> agentQueue;
	protected static Queue<TicketTransaction> ttQueue;
	protected static Queue<SnackTransaction> csQueue;
	protected static boolean soldOut [];
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0){
			printHelp();
			return;
		}
		filename = args[0];
		if (filename.isEmpty()){
			printHelp();
			return;
		}
		//load the shared movie list
		if (loadedMoviesFile()){
			//continue;
		}else{
			System.out.println("Error loading file; exiting");
			System.exit(1);
		}
		//write back to the movies file to ensure that any added/removed whitespace doesnt mess things up later
		//if (wroteMoviesFile()){
			//continue;
		//}else{
		//	System.out.println("Error loading file; exiting");
		//	System.exit(1);
		//}
		//create queues
		agentQueue = new LinkedList<>();
		ttQueue = new LinkedList<>();
		csQueue = new LinkedList<>();
		//create other shared stuff
		soldOut = new boolean [NUM_CUSTOMERS];
		Arrays.fill(soldOut, false);
		initSemArray(0, soldOutResponse);
		initSemArray(0, buyTicket);
		initSemArray(0, tore_ticket);
		initSemArray(0, orderFilled);
		
		//create threads
		//Box Office Agents
		new Thread(new BoxOfficeAgent(0)).start();
		new Thread(new BoxOfficeAgent(1)).start();
		//Ticket Taker
		new Thread(new TicketTaker()).start();
		//Concession Stand Worker
		new Thread(new ConcessionStandWorker()).start();
		//Customers
		for (int i = 0; i < NUM_CUSTOMERS; i++){
			new Thread(new Customer(i)).start();
			leftTheater[i] = new Semaphore(0);
		}
		
		for(int i = 0; i < NUM_CUSTOMERS; i++){
			try {
				leftTheater[i].acquire();
				//System.out.println("Main Joined customer "+ i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Simulation Over. Exiting ....");
		System.exit(0);
	}

	public static void printHelp(){
		System.out.println("USAGE: java Theater <Movie-filename>");
	}
	
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
	
	public static boolean wroteMoviesFile(){
		try{
			FileWriter fw = new FileWriter(new File(filename).getAbsoluteFile());
			BufferedWriter out = new BufferedWriter(fw);
			for (Movie m : movies ){
				out.write(m.getName() + " " + m.getAvailableSeats()+"\n");
			}
			out.close();
			return true;
		}catch (Exception ex){
			System.out.println("Error writing to file '" + filename + "'\n :: "+ex);
			return false;
		}
	}
	
	private static void initSemArray(int initValue, Semaphore [] semArr){
		for (int i = 0; i < semArr.length; i++){
			semArr[i]=new Semaphore(initValue);
		}
	}
}
