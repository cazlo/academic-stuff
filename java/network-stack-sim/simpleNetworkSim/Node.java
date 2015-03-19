/**
 * AUTHOR: Andrew Paettie
 * CLASS: CS 4390.001 Computer Networks
 * DESCRIPTION: Main program for the node
 * 				Basically just a controller for the different layers.
 */

package simpleNetworkSim;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
	
	static protected int id, TTL, destID, numNeighbors;
	static protected int globalTimer;
	static protected String nodeMessage;
	//protected ArrayList<Integer> neighbors = new ArrayList<>();
	//map an index (0,1,...) to a neighbor ID
	static protected HashMap<Integer, Integer> neighborsMap = new HashMap<>();
	
	static protected DatalinkLayer dl;
	static protected NetworkLayer nl;
	static protected TransportLayer tl;
	
	public Node(){};
	
	public Node(String arg0, String arg1, String arg2, ArrayList<String> endArgs){
		numNeighbors = 0;
		//setup node variables
		try{
			id = Integer.parseInt(arg0);
			TTL = Integer.parseInt(arg1);
			destID = Integer.parseInt(arg2);
			if (id == destID){
				//no message
				nodeMessage = null;
				for (String s : endArgs){
					//neighbors.add(Integer.parseInt(s));
					neighborsMap.put(numNeighbors, Integer.parseInt(s));
					numNeighbors++;
				}
			}else{
				nodeMessage = endArgs.get(0);
				for (int i = 1; i < endArgs.size(); i++){
					//neighbors.add(Integer.parseInt(endArgs.get(i)));
					neighborsMap.put(numNeighbors, Integer.parseInt(endArgs.get(i)));
					numNeighbors++;
				}
			}
			//init layers
			dl = new DatalinkLayer();
			tl = new TransportLayer();
			nl = new NetworkLayer();
			nl.initialTreeSetup();
			
		}catch (NumberFormatException ex){
			System.out.println("Error with converting string to int");
			printHelp();
			ex.printStackTrace();
			System.exit(1);
		}catch (Exception e){
			System.out.println("Random error");
			printHelp();
			e.printStackTrace();
			System.exit(1);
		}
		
		//make node do stuff while its still alive
		for (globalTimer = 0; globalTimer < TTL; globalTimer++){
			dl.recieveFromChannel();
			
			if (globalTimer >= 5){//only send if network has converged
				tl.send();
			}
			
			nl.checkIfRoot();
			try {
				Thread.sleep(1 * 1000);//sleep 1 second (1000 millis)
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		tl.outputAllRecieved();
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		checkArgs(args);
		ArrayList<String>endArgs = new ArrayList<>();
		for (int i = 3; i < args.length; i++){
			endArgs.add(args[i]);
		}
		new Node(args[0],args[1],args[2],endArgs);
	}

	
	

	private static void checkArgs(String[] args) {
		if (args.length < 3){
			printHelp();
			System.exit(1);
		}
	}
	
	private static void printHelp(){
		System.out.println("Program usage: \n"+
						   "java Node <ID> <TTL> <DEST> [MSG] [NEIGHBORS]\n"+
						   "where ID = id of node (number from 0 to 9)\n"+
						   "TTL = duration in seconds before the node terminates\n" +
						   "DEST = id of destination node (int from 0 to 9)\n" +
						   "MSG = a string containing the message to send (optional)\n" +
						   "NEIGHBORS = a list of neigbors of the process ");
	}
	
	
}
