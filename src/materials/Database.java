package materials;


import java.util.ArrayList;


public class Database {

	private int seqNum;
	private int totalparts = 0;

	private  ArrayList<String> db = new ArrayList<String>();
	
	public Database(int seqNum) {
		this.seqNum = seqNum;
	}

	/**
	 * 
	 * This method aims to update the database. The sequence number is incremented
	 *
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
	 * This method aims to add data to the ArrayList<String> db.
	 * 
	 * @param entry
	 * 			String data that will be added to the database
	 */
	public synchronized void add(String entry){
		this.db.add(entry);
	}
	
	public synchronized ArrayList<String> getData(){
		return db;
	}
	

	public synchronized int getDatabaseSequenceNumber(){
		return seqNum;
	}
	

	public synchronized int getTotalparts() {
		return totalparts;
	}

	public synchronized void setTotalparts(int totalparts) {
		this.totalparts = totalparts;
	}

}
