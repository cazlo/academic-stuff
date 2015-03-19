/**
 * AUTHOR: Andrew Paettie
 * CLASS: CS 4390.001 Computer Networks
 * DESCRIPTION: The datalink layer.  Reads and writes to/from channel files
 */

package simpleNetworkSim;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;

public class DatalinkLayer extends Node{
	//ArrayList<Character> currMessage = new ArrayList<>();
	HashMap <Integer, ChannelReader> channelReaders = new HashMap<>();
	HashMap <Integer, ChannelWriter> channelWriters = new HashMap<>();
	
	public DatalinkLayer(){
		//TODO
		Collection<Integer> neighbors = neighborsMap.values();
		for (int nID : neighbors){
			channelWriters.put(nID, new ChannelWriter(id, nID));
			channelReaders.put(nID, new ChannelReader(nID, id));
		}
	}
	
	public void recieveFromNetwork(String msg, int nextHop){
		//called by network layer
		//output to correct channel file
		if (msg.length() > 94){
			System.out.println("ERROR: length of message cannot be > 94");
		}
		String lengthString = (msg.length() < 10) ?
							     "0"+msg.length() :
							     "" + msg.length();
		String checkSumString = (calcChecksum("S"+lengthString+msg) < 10) ? 
				"0" + calcChecksum("S"+lengthString+msg) : 
				 "" + calcChecksum("S"+lengthString+msg);
		System.out.println("Datalink writing: "+"S"+lengthString+msg+ checkSumString);
		channelWriters.get(nextHop).append("S"+lengthString+msg+ checkSumString);
		
	}
	
	public void recieveFromChannel(){
		//for each of its neighbors' channels, read input until EOF
		Collection<Integer> neighbors = neighborsMap.values();
		for (int nID : neighbors){
			ChannelReader c = channelReaders.get(nID);
			int byteRead = c.readByte();
			switch (byteRead){
			case Integer.MAX_VALUE://IOException
				//TODO
				break;
			case Integer.MIN_VALUE://stream doesn't exist
				//TODO
				break;
			case -1://EOF
				//TODO
				break;
			case 83: //S
			//case 115://s
				//TODO
				int b1, b2, length;
				b1 = c.readByte();
				b2 = c.readByte();
				try{
				length = Integer.parseInt(("" + (char)b1) //sorry this is kinda hacky
										+ ("" + (char)b2));//convert bytes read to ascii
								//then make them a string so they can be parsed to an int
				handleNormalByte(length, c, nID);
				}catch(NumberFormatException ne){
					//if the bytes read aren't good then try to recover
					handleAbnormalByte(c, nID);
				}
				break;
			default://abnormal byte read
				handleAbnormalByte(c, nID);
				break;
			}
		}
	}
	
	private void handleNormalByte(int length, ChannelReader in, int nID){
		String msg = "";
		//should now be reading network payload for "length" bytes
		int [] netPayload = new int [length];
		for (int i = 0; i < length; i++){
			netPayload [i] = in.readByte();
			msg = msg.concat("" + (char)netPayload[i]);
		}
		//now read and check the checksum
		int b1, b2, checksum;
		b1 = in.readByte();
		//System.out.println("b1:" + b1 + "'"+(char)b1+"'");//DEBUG
		b2 = in.readByte();
		//System.out.println("b2:" + b2+ "'"+(char)b2+"'");//DEBUG
		checksum = Integer.parseInt(("" + (char)b1) //sorry this is kinda hacky
								  + ("" + (char)b2));//convert bytes read to ascii
						//then make them a string so they can be parsed to an int
		String lengthString = (length < 10) ? "0"+length : String.valueOf(length);
		if (checksum == calcChecksum("S"+lengthString+msg)){
			//if checksum is ok, send payload to network layer
			System.out.println("DL: Checksum OK, passing msg to nl: "+ msg);
			nl.recieveFromDatalink(msg, nID);
		}else{
			//try to recover
			//System.out.println("calcChecksum="+calcChecksum("S"+lengthString+msg));//DEBUG
			//System.out.println("checksum="+checksum);//DEBUG
			handleAbnormalByte(in, nID);
		}
	}
	
	private int handleAbnormalByte(ChannelReader in, int nID){
		System.out.println("Encountered message corrption.  " +
							"Searching for next start of packet");
		boolean keepGoing = true;
		int nextGoodByte = -1;
		while (keepGoing){
			nextGoodByte = in.readByte();
			if (nextGoodByte == 83){ //|| nextGoodByte == 115){//its an s
				int b1, b2, length;
				b1 = in.readByte();
				b2 = in.readByte();
				try{
				length = Integer.parseInt(("" + (char)b1) //sorry this is kinda hacky
										+ ("" + (char)b2));//convert bytes read to ascii
								//then make them a string so they can be parsed to an int
				handleNormalByte(length, in, nID);
				keepGoing = false;
				}catch(NumberFormatException ne){
					//if the bytes read aren't good then try to recover
					handleAbnormalByte(in, nID);
					keepGoing = false;
				}
			}else if(nextGoodByte == -1){//end of stream reached
				keepGoing = false;
			}else{
				keepGoing = true;
			}
		}
		return nextGoodByte;
	}
	
	private int calcChecksum(String msg){
		int sum = 0;
		byte[] bs;
		try {
			bs = msg.getBytes("US-ASCII");
			for (byte b : bs){
				sum += b;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (sum % 100);
	}
}
