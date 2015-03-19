package theaterSim;

import theaterSim.ConcessionStandWorker.Concessions;

public class SnackTransaction {
	private Concessions snackName;
	private int custID;
	
	public SnackTransaction(Concessions SnackName, int CustID){
		snackName = SnackName;
		custID=CustID;
	}
	
	public Concessions getName() {
		return snackName;
	}
	public void setName(Concessions name) {
		this.snackName = name;
	}
	public int getCustID() {
		return custID;
	}
	public void setCustID(int custID) {
		this.custID = custID;
	}
	
}
