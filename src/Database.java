import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {
	//Sequence number of the database
	private int seqNum;
	//PeerID of the owner of the database
	private static String owner;
	//List of databases of peers : the database of a peer is identified by the ID of this specified peer
	private static HashMap<String,HashMap<String,PeerRecord>> db_peers = new HashMap<String,HashMap<String,PeerRecord>>();
	
	public void Database(String myPeerID, InetAddress myAddress, int myHelloInterval) {
		this.owner = myPeerID;
		//Initialisation of our own database
		this.addDatabase(this.owner, new HashMap<String,PeerRecord>());
		
		//TODO Add itself to its database ?  
		
		this.seqNum = -1;
	}
	
	
	public synchronized HashMap<String,PeerRecord> getOwnDatabase() {
		return db_peers.get(owner);
	}
	
	public synchronized HashMap<String,PeerRecord> getPeerDatabase(String peerID) {
		return db_peers.get(peerID); 
	}
	
	public static synchronized void updatePeer(String peerID, int seqNumber){
		HashMap<String,PeerRecord> table = db_peers.get(owner);
		PeerRecord peer = table.get(peerID);
		if (peer.peerSeqNum!=seqNumber){
			peer.peerState=PeerState.INCONSISTENT;
		}
		if (peer.peerState==PeerState.INCONSISTENT){
			peer.peerState=PeerState.INCONSISTENT;
		}
		if (peer.peerState==PeerState.INCONSISTENT && peer.peerSeqNum==seqNumber){
			peer.peerState=PeerState.SYNCHRONIZED;
		}
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
		//Evolution of the sequence number
		seqNum +=1;
	}
	
	public static synchronized List<String> sendPeersID(String peerID){
		HashMap<String,PeerRecord> table = db_peers.get(peerID);
		List<String> PeerList= new ArrayList<String>();
		if(table.size() == 0) {
			return PeerList ;
		}
		if(!table.keySet().isEmpty()){
			Database.cleanUp(peerID);
			for (String id : table.keySet()){
				PeerList.add(table.get(id).peerID);

			}
			
		}
		return PeerList;
	}
	
	/**
	 * Remove element of the peer's database corresponding to the specified peerID
	 * 
	 * @param peerID
	 *
	 */
	
	private static synchronized void cleanUp(String peerID){
		HashMap<String,PeerRecord> table = db_peers.get(peerID);
		List<String> toRemove = new ArrayList<String>();
		if (!table.keySet().isEmpty()){
			for (String id : table.keySet()){
				if (table.get(id).expirationTime<System.currentTimeMillis()){
					toRemove.add(id);
				}
			}
		}
		if (!toRemove.isEmpty()){
			for (String _id : toRemove){
				table.remove(_id);
			}
		}
	}
}
