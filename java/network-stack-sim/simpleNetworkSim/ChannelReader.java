package simpleNetworkSim;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ChannelReader {
	//public static final String PATH_TO_CHANNELS = "/";
	
	BufferedInputStream in = null;
	int src, dest;
	
	public ChannelReader(int src, int dest){
		this.src = src;
		this.dest = dest;
		open();
	}
	
	//reads from input stream 1 byte at a time.
	//returns:      Integer.MIN_VALUE if input stream does not exist
	//				Integer.MAX_VALUE if there is some kind of IOException
	//				-1 if the EOF is reached
	public int readByte(){
		if (in != null){
			try {
				return in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Integer.MAX_VALUE;
			}
		}else{
			open();
			return Integer.MIN_VALUE;
		}
	}
	
	public void mark(){
		in.mark(99);
	}
	
	public void open(){
		try {
			in = new BufferedInputStream(new FileInputStream(new File(
					//PATH_TO_CHANNELS + 
					"from"+src+"to"+dest)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				System.err.println("Tried to create new file");
				(new File("from"+src+"to"+dest)).createNewFile();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
	}
}
