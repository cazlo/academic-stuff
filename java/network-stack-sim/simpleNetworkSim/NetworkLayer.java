/**
 * AUTHOR: Andrew Paettie
 * CLASS: CS 4390.001 Computer Networks
 * DESCRIPTION: Network Layer: builds and maintains spanning tree
 * 				routes messages to correct nodes (accoriding to spanning tree)
 */

package simpleNetworkSim;

import java.util.ArrayList;
import java.util.Collection;

public class NetworkLayer extends Node{
	//int rootID = this.id;
	//int hopsToRoot = 0;
	int recieveConfigTimer = 0, sendConfigTimer = 0;
	
	int bestRootID = this.id, bestHopCount = 0, bestParentID = this.id;
	ArrayList<Integer> children = new ArrayList<>();
	
	public void checkIfRoot(){
		if (bestRootID == this.id){//if root, send config to all neighbors
			if (sendConfigTimer % 5 == 0){//every 5 seconds
				//time to send config message
				String hopCountString;
				if (bestHopCount < 10){
					hopCountString = "0"+bestHopCount;
				}else{
					hopCountString = String.valueOf(bestHopCount);
				}
				Collection<Integer> neighbors = neighborsMap.values();
				for (int nID: neighbors){
					System.out.println("Sending config to "+nID+ " at time "+ globalTimer);
					dl.recieveFromNetwork("C" + bestRootID + hopCountString, nID);
				}
			}
			sendConfigTimer++;
			
		} else{//not root, check if we havent recieved a config in > 20 seconds
			if (recieveConfigTimer > 20){
				//become root 
				System.out.println("Becoming root for config timeout; configTimer = "+recieveConfigTimer+"globalTimer="+globalTimer);
				bestRootID = this.id;
				children = new ArrayList<>();
				initialTreeSetup();
			}
			recieveConfigTimer++;
		}
	}
	
	public void recieveFromTransport(String msg, int dst){
		
		//if dest is a neighbor, broadcast to them directly,
		//regardless of what routing table says
		if (neighborsMap.containsValue(dst)){
			System.out.println("Network layer passing from transport to neighbor "+dst +" D"+id+dst+msg);
			dl.recieveFromNetwork("D"+id+dst+msg, dst);
		}else{
			//use spanning tree to determine next hop node
			//broadcast to parent and children only
			if (bestParentID != id){//dont try to broadcast to yourself
				dl.recieveFromNetwork("D"+id+bestParentID+msg, bestParentID);
			}
			System.out.println("Network layer passing from to dl, dst: "+dst +" D"+id+dst+msg);
			for (int nID : children){
				System.out.println("Network layer passing from transport to child "+nID +" D"+id+dst+msg);
				dl.recieveFromNetwork("D"+id+nID+msg, nID);
				System.out.println("Network layer passing from to dl, dst: "+dst +" D"+id+dst+msg);
			}
		}
	}
	
	public void recieveFromDatalink(String msg, int neighbor){
		switch (msg.charAt(0)){
		case 'C':
		case 'c':
			//TODO:
			//if msg is config msg, use to maintain spanning tree
			
			int mRootID = 0, mHop = 0;
			try{
				mRootID = Integer.parseInt(""+msg.charAt(1));
				mHop = Integer.parseInt(""+msg.charAt(2)+msg.charAt(3));
				if (mRootID == bestRootID &&
					mHop == (bestHopCount - 1) &&
					bestParentID == neighbor){
					System.out.println("Recieved config from parent at "+globalTimer );
					//recieved a config message from current parent
					//don't need to look at it further
					recieveConfigTimer = 0;
					//forward the config packet to children so they dont become roots
					for (int c : children){
						System.out.println("Forwarding config to "+c);
						dl.recieveFromNetwork("C" + bestRootID + 
								((bestHopCount < 10) ? "0"+(bestHopCount): ""+(bestHopCount))
								, c);
					}
					return;
				}
				
				if (mRootID < bestRootID){
					//found a new root
					System.out.println("Switching root to "+mRootID
							+" at "+mHop+" hops from neighbor "+neighbor);
					System.out.println("Reason: better rootID");
					if (bestRootID == this.id){
						//change from root to non-root,
						//reset sendConfig Timer, recieveConfigTimer to 1
						sendConfigTimer = 0;
					}
					bestRootID = mRootID;
					bestHopCount = mHop+1;
					bestParentID = neighbor;
					recieveConfigTimer = 0;
					for (int c : children){//remove parent from children list if changing parent
						if (bestParentID == c){
							children.remove((Object)c);
						}else{
							//share the news about new parent with your children
							String hopCountString = ((bestHopCount) < 10) ? "0"+(bestHopCount) : ""+ (bestHopCount);
							dl.recieveFromNetwork("C" + bestRootID + hopCountString, neighbor);
						}
					}
				}else if (mRootID == bestRootID){
					if (bestHopCount > mHop){//same root, better hop count
						if (bestHopCount == mHop + 1){
							//remove from children
							if (children.contains(neighbor)){
								children.remove((Object)neighbor);
							}
						}else{//same root, better hop count by more than 1
							if (bestParentID > neighbor){
								System.out.println("Switching root to "+mRootID
										+" at "+mHop+" hops from neighbor "+neighbor);
								System.out.println("Reason: same, rootID better hopCount");
								bestHopCount = mHop+1;
								bestParentID = neighbor;
								recieveConfigTimer = 0;
								for (int c : children){//remove parent from children list if changing parent
									if (bestParentID == c){
										children.remove((Object)c);
									}else{
										//share the news about new parent with your children
										String hopCountString = ((bestHopCount) < 10) ? "0"+(bestHopCount) : ""+ (bestHopCount);
										dl.recieveFromNetwork("C" + bestRootID + hopCountString, neighbor);
									}
								}
							}
						}
					}else if (bestHopCount == mHop){//same root, same hop count
						//remove from children
						if (children.contains(neighbor)){
							children.remove((Object)neighbor);
						}
					}else if ((bestHopCount == mHop - 1) 
							&& bestParentID < neighbor){
						//remove from children
						if (children.contains(neighbor)){
							children.remove((Object)neighbor);
						}
					}
				}else if (mRootID > bestRootID){
					//tell the node about a better root available
					System.out.println("Telling node "+ neighbor+"about a better root("+bestRootID+")");
					String hopCountString = ((bestHopCount) < 10) ? "0"+(bestHopCount) : ""+ (bestHopCount);
					dl.recieveFromNetwork("C" + bestRootID + hopCountString, neighbor);
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;
		case 'D':
		case 'd':
			int src = 0, dst = 0, tlDst = 0;
			try{
				src = Integer.parseInt(""+msg.charAt(1));
				dst = Integer.parseInt(""+msg.charAt(2));
				tlDst = Integer.parseInt("" + msg.charAt(5));//check the transport layer destination
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			//if data msg 2 cases:
			//	at dest: send to transport
			if (tlDst == id){
				System.out.println("Network layer found message for me.  Passing to transport: '"+msg.substring(3)+"'");
				tl.recieveFromNetwork(msg.substring(3));
			}else{
				//if dest is a neighbor, broadcast to them directly, regardless of what routing table says
				if (neighborsMap.containsValue(tlDst)){
					System.out.println("Network layer found message for a neighbor: " + dst + ". Sending to them directly");
					dl.recieveFromNetwork(msg, tlDst);
				}else{
					for (int nID : children){//	broadcast on all active channels except where the msg came from
						if (nID != src){//only broadcast on non-src channels
							System.out.println("Network layer found message for a unknown host: " + dst + ". Sending to child "+nID);
							dl.recieveFromNetwork(msg, nID);
						}
					}
				}
			}
			break;
		default:
			//TODO: network received invalid msg from datalink
			break;
		}
	}
	
	public void initialTreeSetup(){
		for (int n : neighborsMap.values()){
			children.add(n);
		}
	}
	
	public void outputFinalConfig(){
		System.out.println("Final network config:\n" +
				"bestRootID = "+bestRootID+"\n" +
				"bestHopCount = "+bestHopCount+"\n" +
				"bestParentID = "+bestParentID+"\n\n" +
						"Children:");
		for (int c : children){
			System.out.println(c);
		}
	}
	
}

