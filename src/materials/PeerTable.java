package materials;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;

import ExchangeFile.GettingAllFilesFromPeer;

import java.util.List;

/**
 * The TimerMap class is a synchronized wrapper for a HashMap containing TimerTasks. It is meant to store the SYN message timertasks, in order 
 * to cancel them when the corresponding LIST message is received.
 * 
 * @author Jules YATES
 * @author Bastien CHEVALLIER
 *
 */
class TimerMap{
	HashMap<String, TimerTask> map = new HashMap<String, TimerTask>();
	
	public synchronized boolean contains(String id){
		return map.containsKey(id);
	}
	
	public synchronized void put(String id, TimerTask task){
		map.put(id, task);
	}
	
	public synchronized void remove(String id){
		map.remove(id);
	}
	
	public synchronized TimerTask get(String id){
		return map.get(id);
	}
}

/**
 * PeerTable is a static class, as it is shared by all useful instances of our client. It is an advanced wrapper for a HashMap of PeerRecords. 
 * 
 * @author Bastien CHEVALLIER
 * @author Jules YATES
 *
 */
public class PeerTable {
	private static HashMap<String,PeerRecord> table = new HashMap<String,PeerRecord>() ;
	
	public static BlockingListQueue queue = new BlockingListQueue();
	
	private static TimerMap timerMap = new TimerMap();

	/**
	 * This method is used to add an unknown peerrecord to the HashMap
	 * @param peerID 
	 * @param peerIPAddress
	 * @param HelloInterval
	 */
	public static synchronized void addPeer(String peerID, InetAddress peerIPAddress, int HelloInterval){
		PeerRecord peer = new PeerRecord(peerID, peerIPAddress, -1, HelloInterval, PeerState.HEARD);
		table.put(peerID, peer);
		//Create a directory for this new peer
		if(! new File("C:/Users/jules/Google Drive/Cours Polytechnique/From the internet to the IoT/"+peerID).exists()) {
			new File("C:/Users/jules/Google Drive/Cours Polytechnique/From the internet to the IoT/"+peerID).mkdir();
		}
	}
	
	/**
	 * this method adds a new peerrecord in the HashMap directly atht eh synchronized state.
	 * It can be used to replace a deprecated instance of a PeerRecord in the HashMap.
	 * 
	 * @deprecated
	 * @param peerID
	 * @param peerIPAddress
	 * @param HelloInterval
	 * @param seqNum the actualised sequence number of the corresponding peer
	 */
	public static synchronized void addPeer(String peerID, InetAddress peerIPAddress, int HelloInterval, int seqNum){
		PeerRecord peer = new PeerRecord(peerID, peerIPAddress, seqNum, HelloInterval, PeerState.SYNCHRONIZED);
		table.put(peerID, peer);
	}


	public static synchronized boolean containsPeer(String peerID){
		if (table.containsKey(peerID)){
			PeerRecord peer = table.get(peerID);
			if (peer.expirationTime<System.currentTimeMillis()){
				peer.setPeerState(PeerState.DYING);
				table.remove(peerID);
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method synchronizes a peer in the table, with the new sequence number
	 * 
	 * @param peer_db 
	 * 			Database of the peer to synchronized that contains all filenames
	 * @param peerID
	 * 			This is a String that represents the id of the peer
	 * @param seqNum
	 * 			The number that corresponds to the version of the peer's database
	 */
	public static synchronized void sync(Database peer_db, String peerID, int seqNum){
		PeerRecord peer = table.get(peerID);
		peer.setPeerSeqNum(seqNum);
		peer.setPeerState(PeerState.SYNCHRONIZED);
		peer.setExpirationTime(peer.getHelloInterval());
		
		//Once we have SYNchronised with a peer, you should prepare to download a copy of the files that are advertised from it
		//TODO
		GettingAllFilesFromPeer download = new GettingAllFilesFromPeer(peer_db, peerID);
		new Thread(download).start();
	}
	
	/**
	 * This method modifies the state of a peer depending on the sequence number received in a HELLO message
	 * and its state.
	 * 
	 * @param peerID
	 * @param seqNumber
	 * @param HelloInterval
	 */
	public static synchronized void updatePeer(String peerID, int seqNumber, int HelloInterval){
		PeerRecord peer = table.get(peerID);

		if (peer.getPeerSeqNum()!=seqNumber){
			if (peer.getPeerState()==PeerState.SYNCHRONIZED || peer.getPeerState()==PeerState.HEARD){
				PeerRecord synPeer = new PeerRecord(peer.getPeerId(), peer.getAddress(), seqNumber, HelloInterval, peer.getPeerState());
				if(!timerMap.contains(peer.getPeerId())){
					queue.enqueue(synPeer);
				}

			}
			peer.setPeerState(PeerState.INCONSISTENT);
			peer.setExpirationTime(HelloInterval);
			
		}
		if (peer.getPeerState()==PeerState.INCONSISTENT){
			peer.setPeerState(PeerState.INCONSISTENT);
			peer.setExpirationTime(HelloInterval);
		}
		if (peer.getPeerState()==PeerState.SYNCHRONIZED && peer.getPeerSeqNum()==seqNumber){
			peer.setPeerState(PeerState.SYNCHRONIZED);
			peer.setExpirationTime(HelloInterval);
			
		}
	}
	
	public static synchronized PeerRecord getPeer(String peerID){
		return table.get(peerID);
	}
	


	/**
	 * This method returns an ArrayList of the updated peer ids contained in our table
	 * @return
	 */
	public static synchronized ArrayList<String> sendPeersID(){
		ArrayList<String> PeerList= new ArrayList<String>();
		if(table.size() == 0) {
			return PeerList ;
		}else{
			PeerTable.cleanUp();
			for (String id : table.keySet()){
				PeerList.add(id);

			}
			
		}
		return PeerList;
	}
	/**
	 * @deprecated
	 * @return
	 */
	public static synchronized HashMap<String,Integer> sendDBData(){
		HashMap<String,Integer> db = new HashMap<String,Integer>();
		if(table.size() == 0) {
			return db;
		}
		if(!table.keySet().isEmpty()){
			PeerTable.cleanUp();
			for (String id : table.keySet()){

				db.put(id, table.get(id).getPeerSeqNum());
			}
		}
		return db;
	}
	
	/**
	 * This method checks all entities in the peertable and deletes those who have expired.
	 */
	public static synchronized void cleanUp(){
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
	
	/**
	 * Adds a timertask to the timer map
	 * @param id
	 * @param task
	 */
	public static synchronized void addTask(String id, TimerTask task){
		timerMap.put(id, task);
	}
	
	/**
	 * Cancels a designated task in the timer map
	 * @param id
	 */
	public static synchronized void cancelTask(String id){
		if(timerMap.contains(id)){
			timerMap.get(id).cancel();
			timerMap.remove(id);
		}

	}
	



}



