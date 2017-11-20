package materials;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.List;

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

public class PeerTable {
	private static HashMap<String,PeerRecord> table = new HashMap<String,PeerRecord>() ;
	
	public static BlockingListQueue queue = new BlockingListQueue();
	
	private static TimerMap timerMap = new TimerMap();

	public static synchronized void addPeer(String peerID, InetAddress peerIPAddress, int HelloInterval){
		PeerRecord peer = new PeerRecord(peerID, peerIPAddress, -1, HelloInterval, PeerState.HEARD);
		table.put(peerID, peer);
	}
	
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
	
	public static synchronized void sync(String peerID){
		PeerRecord peer = table.get(peerID);
		peer.setPeerState(PeerState.SYNCHRONIZED);
		peer.setExpirationTime(peer.getHelloInterval());
	}
	
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
	
	public static synchronized void addTask(String id, TimerTask task){
		timerMap.put(id, task);
	}
	
	public static synchronized void cancelTask(String id){
		System.out.println("Try to cancel task : " + id);
		if(timerMap.contains(id)){
			System.out.println("Task : " + timerMap.get(id).toString() + " cancelled");
			timerMap.get(id).cancel();

			timerMap.remove(id);
		}

	}
	



}



