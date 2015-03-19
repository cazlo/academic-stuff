package theaterSim;

public class BoxOfficeAgent extends Project2 implements Runnable{
	public static final double SELL_TIME = 1.5 * 1000; 
	int id;
	TicketTransaction ticket;
	
	public BoxOfficeAgent(int ID){
		id=ID;
	}
	
	@Override
	public void run() {
		announceBirth();
		while(true){
			availableAgent.release();
			try{
			cust_ready.acquire();
			getTransactionInfo.acquire();
			ticket = agentQueue.remove();
			getTransactionInfo.release();
			readMovies.acquire();
			if (isSoldOut()){
				readMovies.release();
				soldOut[ticket.getOwnerID()]= true;
				System.out.println(ticket.getMovieTitle()+ " is sold out! Sorry Customer "+ ticket.getOwnerID());
				soldOutResponse[ticket.getOwnerID()].release();
				continue;
			}else{
				
				soldOutResponse[ticket.getOwnerID()].release();
				ticketSaleAccounting();
				readMovies.release();
				sellTicket();
				buyTicket[ticket.getOwnerID()].release();
			}
			}catch(InterruptedException e){
				//TODO
				e.printStackTrace();
			}
		}
	}
	
	private void announceBirth(){
		System.out.println("Box Office Agent "+ id + " created");
	}
	
	private boolean isSoldOut(){
		if (Project2.loadedMoviesFile()){
			for (Movie m : movies){
				if (m.getName().equals(ticket.getMovieTitle())){
					if(m.getAvailableSeats()>0){
						return false;
					}else{
						return true;
					}
				}
			}
			System.out.println("Could not find movie (This should never happen!)");
			for (Movie m : movies){
				System.out.println("m.name= '"+m.getName()+"'\n"+
								   "target= '"+ticket.getMovieTitle()+"'\n"+
								   "equal? "+m.getName().equals(ticket.getMovieTitle()));
			}
			System.exit(1);
			return false;//needed for compiler
		}else{
			System.out.println("ERROR: could not load movie file");
			return false;
		}
	}
	
	private void ticketSaleAccounting(){
		for (int i = 0; i < movies.size(); i++){
			if (movies.get(i).getName().equals(ticket.getMovieTitle())){
				//found movie
				movies.get(i).setAvailableSeats(movies.get(i).getAvailableSeats() - 1);
			}
		}
		if (Project2.wroteMoviesFile()){
			//success writing the movies list back to file
		}else{
			System.out.println("ERROR: could not write back movies to file");
			System.exit(1);
		}
	}
	
	private void sellTicket(){
		//TODO
		System.out.println("Box Office Agent " + id + " sold ticket for "
							+ticket.getMovieTitle()+" to Customer "+ticket.getOwnerID());
		try {
			Thread.sleep((long) SELL_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
