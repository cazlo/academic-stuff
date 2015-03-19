/**
 * AUTHOR: Andrew Paettie
 * CLASS: CS 4390.001 Computer Networks
 * DESCRIPTION: Transport Layer: maintains connections between 2 nodes
 * 				keeps track of sender and reciever stuff related to nacks
 */


package simpleNetworkSim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TransportLayer extends Node{
	//int globalTimer = 0;//timer for coordinating nacks.  increments by 1 at end of send()
	
	//------------------------reciever stuff
	//maps source (integer) to message (String)
	//HashMap<Integer, String> msgRecieveMap = new HashMap<Integer, String>();
	//maps source to an arraylist containaing pieces of the message
	HashMap<Integer, ArrayList<String>> msgPiecesMap = new HashMap<Integer, ArrayList<String>>();
	//maps source to a arraylist of timers for a src's individually received packets 
	HashMap<Integer, ArrayList<Integer>> nackTimersMap = new HashMap<Integer, ArrayList<Integer>>();
	
	//------------------------sender stuff
	ArrayList<String> msgSendPackets = new ArrayList<>();
	boolean [] msgSent;
	//int lbs = 0; //sequence num of last byte sent
	int msgDoneTimer = -1;//starts ticking when the msg is totally sent
						//gets incremented at end of send()
	boolean recievedFinalNack = false;
	
	public TransportLayer(){
		if (nodeMessage==null){
			//don't need to setup the msgPackets
		}else if (nodeMessage.length() <= 5){//easy
			msgSendPackets.add(nodeMessage);
		} else{
			//build the message packets 5 bytes at a time
			System.out.println("Building message packets:");
			for (int i = 0, start = 0; i < nodeMessage.length(); i+=5){
				try{
					msgSendPackets.add(nodeMessage.substring(i, i+5));
					System.out.println("adding to packets: "+ nodeMessage.substring(i, i+5));
				}catch (Exception ex){//gets thrown only if we are at the end
					msgSendPackets.add(nodeMessage.substring(i, nodeMessage.length()));
					System.out.println("adding to packets: "+ nodeMessage.substring(i));
				}
			}
		}
		msgSent = new boolean [msgSendPackets.size()];
		unsendAll();
	}
	 
	public void send(){
		//called in main
		
		//sends out packets of data
		if (msgSendPackets.size() == 0){
			//don't need to send any data
		}else{
			if (allSent()){
				if ((msgDoneTimer % 20 == 0) && !recievedFinalNack ){//time is up and still no final nack
					System.out.println("Never recieved final nack; retransmitting all");
					unsendAll();//resend all data messages again
				}
			}else{
				for (int seq = 0; seq < msgSent.length; seq++){
					//go through the sent array 1 at a time to make sure
					//older messages can be retransmitted
					if (!msgSent[seq]){
						String seqString = (seq < 10) ? "0"+seq : ""+seq;
						//send packet of data
						nl.recieveFromTransport("D"+id+destID+seqString+msgSendPackets.get(seq), destID);
						//lbs++;
						msgSent[seq] = true;
						if (allSent()){//just sent the last msg
							msgDoneTimer = 0;//start the clock a tickin for the final nack
						}
						break;//only send 1 packet at a time, so break out of the for
					}
				}
			}
		}
		
		//send nacks for data not yet recieved
		if (msgPiecesMap.size() == 0){
			//noone is sending us data (yet)
		} else{
			//send nacks
			for (int src : nackTimersMap.keySet()){//look at all message senders
										//and their corresponding nack timers
				for (int seq = 0; seq < nackTimersMap.get(src).size(); seq ++){//seq is seqnum in question
					int nt = nackTimersMap.get(src).get(seq);// nt is the timer value when packet w/seq i was recieved
					if (nt == -1){//all is well with this packet
					}//set nt to -1 if we verified there is no reason to check this packet in the future
					else if (globalTimer - nt >= 5 ){//if 
						boolean hadToNack = false;
						//do we have every packet from 0 up to seq?
						for (int j = 0; j < seq; j++){
							if (msgPiecesMap.get(src).get(j) == ""){
								//have not recieved this packet yet
								System.out.println("Sending nack for reorder");
								String seqStr = (j < 10) ? "0" + j : "" + j;
								nl.recieveFromTransport("N"+id+src+seqStr, src);
								hadToNack = true;
							}
						}
						//Have we recieved packet with seq i+1?
						try{
							String ip1Msg = msgPiecesMap.get(src).get(seq + 1);
							if (ip1Msg == ""){
								//have not yet receieved msg i+1
								//send nack for i+ 1
								System.out.println("Sending nack for not getting seq + 1 yet");
								String seqStr = ((seq+1) < 10) ? "0" + (seq+1) : "" + (seq+1);
								nl.recieveFromTransport("N"+id+src+seqStr, src);
								hadToNack = true;
							}
						}catch (IndexOutOfBoundsException ex){
							//out of bounds, so have not yet receieved msg i+1
							//send nack for i+ 1
							System.out.println("Sending nack for IOOB. Seq = " + seq);
							String seqStr = ((seq+1) < 10) ? "0" + (seq+1) : "" + (seq+1);
							nl.recieveFromTransport("N"+id+src+seqStr, src);
							hadToNack = true;
							nackTimersMap.get(src).set(seq,  -1);//ignore this nack in the future
																//because this only happens @ end of message
						}
						if ( ! hadToNack){//if we didnt have to nack, then this timer is done
							//mark timer as -1 so we don't look at it anymore
							nackTimersMap.get(src).set(seq, -1);
						}
					}
				}
			}
		}
		
		globalTimer++;
		if (msgDoneTimer == -1){
			//-1 when message is still going
		}else{
			msgDoneTimer++;
		}
	}
	
	public void outputAllRecieved(){
		try{
			BufferedWriter out = new BufferedWriter(
					new FileWriter("node"+id+"recieved"));
			for (int src : msgPiecesMap.keySet()){
				out.write("Message recieved from " + src + ": ");
				//System.out.println("size:" +msgPiecesMap.get(src).size() 
				//				+"\ntostring:"+ msgPiecesMap.get(src).toString());
				for (String p : msgPiecesMap.get(src) ){
					out.write(p);
				}
				out.write('\n');
			}
			
			nl.outputFinalConfig();
			out.close();
		}catch (IOException ex){
			System.out.println("Error creating output file for node"+id);
			ex.printStackTrace();
		}
	}
	
	public void recieveFromNetwork(String msg){
		try{
			//called by network layer to pass msg to this layer
			//grab the fields from teh header
			char type = msg.charAt(0);
			int srcID = Integer.parseInt(""+msg.charAt(1));
			int dstID = Integer.parseInt(""+msg.charAt(2));
			int seqnum = Integer.parseInt(""+msg.charAt(3)+msg.charAt(4));
			
			//sanity checks
			if (dstID != id){
				System.out.println("Transport: Receieved a packet not addressed to me! Dropping");
				System.out.println("Packet: "+msg);
				return;
			}
			if (srcID == id){
				//cannot recieve a message from itself
				System.out.println("Transport: Recieved a packet from myself. Dropping");
				return;
			}
			//2 cases: data or nack
			switch (msg.charAt(0)){
			case 'D':
			case 'd'://data message:
				if (msgPiecesMap.containsKey(srcID)){//old message sender
					System.out.println("Got another packet from "+srcID);
					if (msgPiecesMap.get(srcID).size() <= seqnum){
						msgPiecesMap.get(srcID).ensureCapacity(seqnum + 1);
						nackTimersMap.get(srcID).ensureCapacity(seqnum + 1);
						while (msgPiecesMap.get(srcID).size() <= seqnum){
							msgPiecesMap.get(srcID).add("");
						}
						while (nackTimersMap.get(srcID).size() <= seqnum){
							nackTimersMap.get(srcID).add(-1);
						}
					}
					if (msgPiecesMap.get(srcID).get(seqnum) != ""){
						//TODO: handle message sent with duplicate seqnum
						System.out.println("Recieved duplicate packet. Seqnum: "+ seqnum);
					}else{
						//System.out.println("msg.substring(5):"+msg.substring(5));
						System.out.println("Recieved new packet from "+srcID + " :"+msg.substring(5));
						msgPiecesMap.get(srcID).set(seqnum, msg.substring(5));
						nackTimersMap.get(srcID).set(seqnum, globalTimer);
					}
				}else{//new message sender
					System.out.println("Got a packet from new sender "+srcID);
					msgPiecesMap.put(srcID, new ArrayList<String>());
					nackTimersMap.put(srcID, new ArrayList<Integer>());
					if (msgPiecesMap.get(srcID).size() <= seqnum){
						msgPiecesMap.get(srcID).ensureCapacity(seqnum + 1);
						nackTimersMap.get(srcID).ensureCapacity(seqnum + 1);
						while (msgPiecesMap.get(srcID).size() <= seqnum){
							msgPiecesMap.get(srcID).add("");
						}
						while (nackTimersMap.get(srcID).size() <= seqnum){
							nackTimersMap.get(srcID).add(-1);
						}
					}
					//System.out.println("msg.substring(5):"+msg.substring(5));
					System.out.println("Recieved new packet from "+srcID + " :"+msg.substring(5));
					msgPiecesMap.get(srcID).set(seqnum, msg.substring(5));
					nackTimersMap.get(srcID).set(seqnum, globalTimer);
				}
				break;
			case 'N':
			case 'n'://nack
				//TODO: handle nack
				if (seqnum >= msgSent.length){
								//recieved final nack
					sendAll();  //so set all msgSent = true
					recievedFinalNack = true;
					System.out.println("Recieved final nack ");
					break;
				}
				if (seqnum < 0 || seqnum >= msgSent.length){
					//dont want to throw an oob exception
					System.out.println("Recieved nack other than final nack");
					break;
				}
				if (msgSent[seqnum]){//
					msgSent[seqnum] = false;//set the msg sent flag for this msg to false
											//so it gets sent again
				}else{}//havn't sent the packet yet, so why recieve a nack?
					   //just drop the packet.
				break;
			default:
				//TODO: handle recieved bad tl packet from network
				break;
			}
		}catch (NumberFormatException ex){
			//TODO: internal error
			System.out.println("Internal error for msg '"+msg+"'");
			ex.printStackTrace();
		}catch (IndexOutOfBoundsException e){
			System.out.println("Internal error for msg '"+msg+"'");
			e.printStackTrace();
		}
		
	}
	
	private boolean allSent(){
		for (boolean b : msgSent){
			if (!b){return false;}
		}
		return true;
	}
	
	private void unsendAll(){
		for (boolean b : msgSent){b = false;}
	}
	
	private void sendAll(){
		for (boolean b : msgSent){b = true;}
	}
}
