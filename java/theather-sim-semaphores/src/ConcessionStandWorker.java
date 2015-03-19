package theaterSim;

public class ConcessionStandWorker extends Project2 implements Runnable{

	public static long FILL_TIME=3*1000;//3seconds=3000milliseconds
	
	enum Concessions{
		POPCORN, SODA, POPCORN_AND_SODA
	}
	
	private SnackTransaction snack;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				cust_ready_cs.acquire();
				snack = csQueue.remove();
				announceOrderTaken();
				makeAndServeOrder();
				orderFilled[snack.getCustID()].release();
				availableCS.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void announceOrderTaken(){
		System.out.println("Order for "+snack.getName().name()+ " taken from Customer "+ snack.getCustID());
	}
	
	private void makeAndServeOrder() throws InterruptedException{
		Thread.sleep(FILL_TIME);
		System.out.println(snack.getName().name() + " given to Customer "+ snack.getCustID());
	}

}
