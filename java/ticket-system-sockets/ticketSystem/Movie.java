package ticketSystem;

public class Movie {
	private String name;
	private int seats;
	private int heldSeats;
	
	public int getHeldSeats() {
		return heldSeats;
	}

	public void setHeldSeats(int heldSeats) {
		this.heldSeats = heldSeats;
	}

	public Movie(String Name, int numSeats){
		name = Name;
		seats = numSeats;
		heldSeats = 0;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSeats() {
		return seats;
	}
	public void setSeats(int seats) {
		this.seats = seats;
	}
	public int getAvailable(){
		return (seats - heldSeats);
	}
	
}
