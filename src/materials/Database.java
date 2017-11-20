package materials;


import java.util.HashMap;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * ListReceiver is the class that will handle ListMessage and update the data associated to a peer in the Peer Table
 * 
 * @author Bastien Chevallier & Jules Yates
 *
 */
public class Database {

	private int seqNum;

	private  ArrayList<String> db = new ArrayList<String>();
	
	public Database(int seqNum) {
		this.seqNum = seqNum;
	}

	/**
	 * 
	 * This method aims to update the database. The sequence number is then incremented
	 * This method is called only in the case of our local database
	 * 
	 * @param new_db
	 * 			This is an ArrayList<String> that will replace the current database
	 */
	public synchronized void updateDB(ArrayList<String> new_db){
		this.db=new_db;
		this.seqNum++;
	}
	
	/**
	 * 
	 * This method aims to add data to the ArrayList<String> db. The sequence number is then incremented
	 * 
	 * @param entry
	 * 			String data that will be added to the database
	 */
	public synchronized void add(String entry){
		this.db.add(entry);
		this.seqNum++;
	}
	
	public synchronized ArrayList<String> getData(){
		return db;
	}
	

	public synchronized int getDatabaseSequenceNumber(){
		return seqNum;
	}

}
