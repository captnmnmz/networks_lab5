package materials;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

	private int seqNum;

	private  String owner;

	private  HashMap<String,Integer> db = new HashMap<String,Integer>();
	
	private String tempData="theData";
	
	public Database(String myPeerID, int seqNum) {
		this.owner = myPeerID;
		this.seqNum = seqNum;
	}
	
	
	
	//this is called only in the case of our local database
	public synchronized void updateDB(){
		this.seqNum++;
	}
	
	public synchronized String getData(){
		return tempData;
	}
	
	public synchronized void setData(String data) {
		this.tempData = data;
	}
	

	public synchronized int getDatabaseSequenceNumber(){
		return seqNum;
	}
	
	public synchronized void setOwner(String owner){
		this.owner=owner;
	}
	


}
