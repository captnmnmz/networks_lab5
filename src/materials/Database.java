package materials;


import java.util.ArrayList;


public class Database {

	private int seqNum;
	private int totalparts = 0;
	private int counter = 0;

	private  ArrayList<String> db = new ArrayList<String>();
	
	public Database(int seqNum) {
		this.seqNum = seqNum;
	}

	/**
	 * 
	 * This method aims to update the database. The sequence number is incremented
	 *
	 * 
	 * 
	 */
	public synchronized void updateDB(){
		this.seqNum++;
	}
	
	public synchronized void resetDB(){
		this.db.clear();
		this.counter=0;
	}
	
	/**
	 * This method adds a String in the database
	 * @param entry
	 */
	public synchronized void add(String entry){
		this.db.add(entry);
		counter++;
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
	
	public synchronized int getCounter(){
		return this.counter;
	}
	
	public synchronized void ensureCapacity(int min){
		this.db.ensureCapacity(min);
	}
	
	public synchronized int getDBsize(){
		return this.db.size();
	}

}
