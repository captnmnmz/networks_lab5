package materials;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

	private int seqNum;

	private  String owner;

	private  HashMap<String,Integer> db = new HashMap<String,Integer>();
	
	public Database(String myPeerID, InetAddress myAddress, int myHelloInterval) {
		this.owner = myPeerID;
		this.seqNum = -1;
	}
	
	
	//we suppose the data has already been formated as "id;seqnum;id2;seqnum2;..."
	public synchronized void updateDB(String data) throws IllegalArgumentException{
		HashMap<String,Integer> updated = new HashMap<String,Integer>();
		String[] received = data.split(";");
		if (received.length%2 != 0){
			throw new IllegalArgumentException("there is not a corresponding number of peerIDs and SeqNums in the"
					+ "data sent by the peer");
		}else{
		//TODO check termination condition of "for" loop	
		}for (int i=0;i<received.length; i+=2){
			updated.put(received[i], Integer.parseInt(received[i+1]));
			db=updated;
		}
		
	}
	
	//this is called only in the case of our local database
	public synchronized void updateDB(){
		db=PeerTable.sendDBData();
	}
	
	public synchronized String format(){
		int len = db.size();
		String data="";
		if(!db.keySet().isEmpty()){
			for (String id : db.keySet()){
				data+=id;
				data+=Integer.toString(db.get(id));
			}
		}
		return data;
	}
	

	public synchronized int getDatabaseSequenceNumber(){
		return seqNum;
	}
	


}
