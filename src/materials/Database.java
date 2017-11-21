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
	 * This method adds a String in the database
	 * @param entry
	 */
	public synchronized void add(String entry){
		this.db.add(entry);
	}
	
	/**
	 * This method adds a String at the specified index in the databse
	 * @param index
	 * @param entry
	 */
	public synchronized void add(int index, String entry){
		this.db.add(index, entry);
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
