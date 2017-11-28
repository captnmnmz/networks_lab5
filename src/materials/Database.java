package materials;


import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Database {

	private int seqNum;

	private int SCREENINTERVALL=1000;

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
	
	
	/**
	 * 
	 * This method screens periodically the root folder in order to detect know folder or file and add them to the Database
	 * 
	 */
	public synchronized void screenDB() {
		
		File root_folder = new File(MuxDemuxSimple.root_path + "mysharefilesfolder");
		Timer TIMER = new Timer("ScreenTimer", true);
		Database db_to_screen = this;
		TimerTask screen = new TimerTask() {
			@Override
			public void run() {
				File[] listOfFiles = root_folder.listFiles();
				for (int i=0; i<listOfFiles.length; i++) {
					// Add the the path name to the database if a new folder or file was added
					if(!db.contains(listOfFiles[i].getName())) {
						db.add(listOfFiles[i].getName());
						//Increment the sequence number
						db_to_screen.updateDB();
					}
				}
				
			}
		};
		TIMER.schedule(screen,0,SCREENINTERVALL);
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
