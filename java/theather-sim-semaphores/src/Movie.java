package theaterSim;

public class Movie {
	private String name;
	private int maxSeats;
	private int availableSeats;
	
	public Movie(String Name, int MaxSeats){
		this.name=Name;
		this.maxSeats=MaxSeats;
		this.availableSeats=MaxSeats;
	}
	
	public Movie(String Name, int MaxSeats, int AvlSeats){
		this.name=Name;
		this.maxSeats=MaxSeats;
		this.availableSeats=AvlSeats;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxSeats() {
		return maxSeats;
	}
/*
	public void setMaxSeats(int maxSeats) {
		this.maxSeats = maxSeats;
	}*/

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
	}
	
}
