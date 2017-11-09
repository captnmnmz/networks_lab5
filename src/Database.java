import java.net.InetAddress;
import java.util.HashMap;

public class Database {
	//Sequence number of the database
	private int seqNum;
	//PeerID of the owner of the database
	private String owner;
	//List of databases of peers : the database of a peer is identified by the ID of this specified peer
	private static HashMap<String,HashMap<String,PeerRecord>> db_peers = new HashMap<String,HashMap<String,PeerRecord>>();
	
	public void Database(String myPeerID, InetAddress myAddress, int myHelloInterval) {
		this.owner = myPeerID;
		//Initialisation of our own database
		this.addDatabase(this.owner, new HashMap<String,PeerRecord>());
		
		//TODO Add itself to its database ?  
		
		
		this.seqNum = -1;
	}
	
	public synchronized void getOwnDatabase() {
		
	}
	
	public synchronized void getPeerDatabase() {
		
	}
	
	/**
	 * Add a peer's database to the list of databases
	 * 
	 * @param peerID : ID of the peer which owns
	 * @param peer_db : database to add
	 *
	 */
	public synchronized void addDatabase(String peerID, HashMap<String,PeerRecord> peer_db){
		db_peers.put(peerID, peer_db);
	}
	
	/**
	 * Add a peer to owner's database
	 * 
	 * @param peerID
	 * @param peerIPAddress
	 * @param HelloInterval
	 *
	 */
	public synchronized void addPeer(String peerID, InetAddress peerIPAddress, int HelloInterval){
		HashMap<String,PeerRecord> table = db_peers.get(owner);
		PeerRecord peer = new PeerRecord(peerID, peerIPAddress, -1, HelloInterval, PeerState.HEARD);
		table.put(peerID, peer);
	}
}
