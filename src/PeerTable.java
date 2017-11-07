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

class PeerRecord{
	String peerID;
	InetAddress peerIPAddress;
	int peerSeqNum;
	double expirationTime;
	PeerState peerState;

	public PeerRecord(String peerID,InetAddress peerIPAddress, int peerSeqNum, int HelloInterval, PeerState peerState){
		this.peerID=peerID;
		this.peerIPAddress=peerIPAddress;
		this.peerSeqNum=peerSeqNum;
		this.expirationTime=HelloInterval*1000+System.currentTimeMillis();
		this.peerState=peerState;
	}
}

public class PeerTable {
	private static HashMap<String,PeerRecord> table = new HashMap<String,PeerRecord>() ;

	public static synchronized void addPeer(String peerID, InetAddress peerIPAddress, int HelloInterval){
		PeerRecord peer = new PeerRecord(peerID, peerIPAddress, -1, HelloInterval, PeerState.HEARD);
		table.put(peerID, peer);
	}

	public static synchronized String getPeer(String peerID){
		if (table.containsKey(peerID)){
			PeerRecord peer = table.get(peerID);
			if (peer.expirationTime<System.currentTimeMillis()){
				table.remove(peerID);
				return null;
			}else{
				return peer.peerID;
			}
		}
		return null;

	}
	
	public static synchronized LinkedList<String> getPeersID(){
		LinkedList<String> peers = new LinkedList<String>();
		if(!table.keySet().isEmpty()) {
			for (String id : table.keySet()){
				if (PeerTable.getPeer(id)!=null){
					peers.add(PeerTable.getPeer(id));
				}
			}
		}
		return peers;
	}

	public static synchronized boolean containsPeer(String peerID){
		if (table.containsKey(peerID)){
			PeerRecord peer = table.get(peerID);
			if (peer.expirationTime<System.currentTimeMillis()){
				peer.peerState=PeerState.DYING;
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
	


	public static synchronized List<String> sendPeersID(){
		List<String> PeerList= new ArrayList<String>();
		if(table.size() == 0) {
			return PeerList ;
		}
		if(!table.keySet().isEmpty()){
			for (String id : table.keySet()){
				if (PeerTable.getPeer(id)!=null){
					PeerList.add(PeerTable.getPeer(id));

				}
			}
		}
		return PeerList;
	}


}



