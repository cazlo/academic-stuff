/**
 * AUTHOR: Andrew Paettie
 * CLASS: CS 4390.001 Computer Networks
 * DESCRIPTION: Helper function to abstract writing to channels
 */

package simpleNetworkSim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ChannelWriter {
	BufferedWriter out = null;
	int src, dst;
	
	public ChannelWriter(int src, int dst){
		this.src = src;
		this.dst = dst;
		open();
	}
	
	public void append(String message){
		try {
			open();
			out.write(message);
			//out.append(message);
			out.close();
			//
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error appending '" +message+"' to" +
					" from"+src+"to"+dst);
			e.printStackTrace();
		}
	}
	
	public void open(){
		try{
			out = new BufferedWriter(
					new FileWriter("from"+src+"to"+dst, true));	
		}catch (IOException ex){
			System.out.println("Error creating from writer for from"+src+"to"+dst);
			ex.printStackTrace();
		}
	}
}
