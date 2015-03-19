package theaterSim;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import theaterSim.ConcessionStandWorker.Concessions;

public class Customer extends Project2 implements Runnable{
	private int id;
	TicketTransaction ticket;
	SnackTransaction snack;
	
	public Customer(int ID){
		id = ID;
		ticket = new TicketTransaction(id, null);
		snack = new SnackTransaction(null, id);
	}
	
	@Override
	public void run() {
		announceBirth();
		try {
		readMovies.acquire();
		readInputFile();
		ticket.setMovieTitle(decideOnMovie());
		if (soldOut(ticket.getMovieTitle())){
			announceLeft();
			readMovies.release();
			return;
		}
		readMovies.release();
		
		getInLine();
		availableAgent.acquire();
		getTransactionInfo.acquire();
		agentQueue.add(ticket);
		cust_ready.release();
		getTransactionInfo.release();
		soldOutResponse[id].acquire();
		if (soldOut[id]){
			announceLeft();
			return;
		}
		buyTicket[id].acquire();
		getInTicketTackerLine();
		availableTT.acquire();
		ttQueue.add(ticket);
		cust_ready_tt.release();
		tore_ticket[id].acquire();
		if (gettingConcessions()){
			snack = new SnackTransaction(decideOnType(), id);
			getInConcessionStandLine();
			availableCS.acquire();
			csQueue.add(snack);
			cust_ready_cs.release();
			orderFilled[id].acquire();
		}
		}catch (InterruptedException e){
			//TODO
			e.printStackTrace();
		}
		announceJoined();
	}
	
	private void announceBirth(){
		System.out.println("Customer "+ id + " created");
	}
	
	private void readInputFile(){
		if (Project2.loadedMoviesFile()){
			
		}else{
			//TODO
		}	
	}
	
	private String decideOnMovie(){
		return movies.get(new Random().nextInt(movies.size())).getName();
	}
	
	private boolean soldOut(String movieTitle){
		for (Movie m : movies){
			if (m.getName().equals(movieTitle)){
				//found the movie
				if (m.getAvailableSeats() > 0)
					return false;
				else
					return true;
			}
		}
		System.out.println("ERROR: CUSTOMER :: SOLDOUT() cannot find movie");
		return true;
	}

	private void getInLine() {
		System.out.println("Customer "+ this.id + " buying ticket to "+ ticket.getMovieTitle());	
	}
	
	private void seeTicketTaker() {
	// TODO Auto-generated method stub
		
	}

	private void getInTicketTackerLine() {
		System.out.println("Customer " + id + " in line to see ticket taker");
	}

	private void buyTicket() {
	// TODO Auto-generated method stub
		
	}

	private boolean gettingConcessions() {
		return new Random().nextBoolean();
	}
	
	private Concessions decideOnType(){
		switch (new Random(new Date().getTime()).nextInt(2)){
		case 0:
			return Concessions.POPCORN;
		case 1:
			return Concessions.POPCORN_AND_SODA;
		case 2:
			return Concessions.SODA;
		default:
			System.out.println("THIS SHOULD NEVER HAPPEN");
			return null;
		}
	}
	
	private void getInConcessionStandLine(){
		System.out.println("Customer "+ id + " in line to buy " + snack.getName().toString());
	}
	
	private void placeSnackOrder(){
		//TODO
	}
	
	private void recieveSnack(){
		System.out.println("Customer "+ id+" recieves "+ snack.getName().toString());
	}
	
	private void announceJoined(){
		System.out.println("Customer "+ id + " enters theater to see "+ ticket.getMovieTitle()
				        +"\nJoined customer " +id);
		leftTheater[id].release();
	}
	
	private void announceLeft(){
		System.out.println("Customer " + id + " left due to " + ticket.getMovieTitle() + " being sold out"
				       +"\nJoined customer " +id);
		leftTheater[id].release();
	}
}
