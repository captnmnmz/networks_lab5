package materials;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Database {

	private int seqNum;

	private  String owner;

	private  ArrayList<String> db = new ArrayList<String>();
	
	
	
	public Database(String myPeerID, int seqNum) {
		this.owner = myPeerID;
		this.seqNum = seqNum;
	}
	
	
	
	//this is called only in the case of our local database
	public synchronized void updateDB(ArrayList<String> new_db){
		this.db=new_db;
		this.seqNum++;
	}
	
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
	
	public synchronized void setOwner(String owner){
		this.owner=owner;
	}
	


}
