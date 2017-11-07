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
import java.util.HashMap;
import java.util.LinkedList;

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
	HashMap<String,PeerRecord> table;

	public synchronized void addPeer(String peerID, InetAddress peerIPAddress, int peerSeqNum, int HelloInterval, PeerState peerState){
		PeerRecord peer = new PeerRecord(peerID, peerIPAddress, peerSeqNum, HelloInterval, peerState);
		table.put(peerID, peer);
	}

	public synchronized String getPeer(String peerID){
		if (table.containsKey(peerID)){
			PeerRecord peer = table.get(peerID);
			if (peer.expirationTime<System.currentTimeMillis()){
				peer.peerState=PeerState.DYING;
				table.remove(peerID);
				return null;
			}else{
				return peer.peerID;
			}
		}
		return null;

	}
	
	public synchronized LinkedList<String> getPeersID(){
		LinkedList<String> peers = new LinkedList<String>();
		for (String id : table.keySet()){
			if (this.getPeer(id)!=null){
				peers.add(this.getPeer(id));
			}
		}
		return peers;
	}

	public synchronized boolean containsPeer(String peerID){
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
	
	public synchronized String toString(){
		return table.toString();
	}


}



