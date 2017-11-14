package materials;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PeerTable {
	private static HashMap<String,PeerRecord> table = new HashMap<String,PeerRecord>() ;
	
	public static BlockingListQueue queue = new BlockingListQueue();

	public static synchronized void addPeer(String peerID, InetAddress peerIPAddress, int HelloInterval){
		PeerRecord peer = new PeerRecord(peerID, peerIPAddress, -1, HelloInterval, PeerState.HEARD);
		table.put(peerID, peer);
	}
	
	public static synchronized void addPeer(String peerID, InetAddress peerIPAddress, int HelloInterval, int seqNum){
		PeerRecord peer = new PeerRecord(peerID, peerIPAddress, seqNum, HelloInterval, PeerState.HEARD);
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
	
	public static synchronized void updatePeer(String peerID, int seqNumber){
		PeerRecord peer = table.get(peerID);
		if (peer.getPeerSeqNum()!=seqNumber){
			if (peer.getPeerState()==PeerState.SYNCHRONIZED || peer.getPeerState()==PeerState.HEARD ){
				queue.enqueue(peer);
			}
			peer.setPeerState(PeerState.INCONSISTENT);
			
		}
		if (peer.getPeerState()==PeerState.INCONSISTENT){
			peer.setPeerState(PeerState.INCONSISTENT);
		}
		if (peer.getPeerState()==PeerState.SYNCHRONIZED && peer.getPeerSeqNum()==seqNumber){
			peer.setPeerState(PeerState.SYNCHRONIZED);
			peer.setExpirationTime();
			
		}
	}
	
	public static synchronized PeerRecord getPeer(String peerID){
		return table.get(peerID);
	}
	



	public static synchronized List<String> sendPeersID(){
		List<String> PeerList= new ArrayList<String>();
		if(table.size() == 0) {
			return PeerList ;
		}
		if(!table.keySet().isEmpty()){
			PeerTable.cleanUp();
			for (String id : table.keySet()){
				PeerList.add(table.get(id).getPeerId());

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
	



}



