package theaterSim;

public class TicketTaker extends Project2 implements Runnable{

	public static double TEAR_TIME = 0.25 * 1000;
	
	TicketTransaction ticket;
	
	@Override
	public void run() {
		while(true){
			try {
				cust_ready_tt.acquire();
				ticket = ttQueue.remove();
				tearTicket();
				tore_ticket[ticket.getOwnerID()].release();
				availableTT.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void tearTicket() throws InterruptedException{
		Thread.sleep((long)TEAR_TIME);
		System.out.println("Ticket taken from Customer "+ ticket.getOwnerID());
	}

}
