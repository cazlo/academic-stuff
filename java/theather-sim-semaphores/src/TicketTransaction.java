package theaterSim;

public class TicketTransaction {
	private int ownerID;
	private String movieTitle;
	private boolean isTorn;
	
	public TicketTransaction(int custID, String title){
		movieTitle = title;
		ownerID = custID;
		isTorn=false;
	}

	public boolean isTorn() {
		return isTorn;
	}

	public int getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}

	public String getMovieTitle() {
		return movieTitle;
	}

	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}
}
