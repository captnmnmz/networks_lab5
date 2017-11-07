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
import java.util.Hashtable;

class PeerRecord{
	String peerID;
	InetAddress peerIPAddress;
	int peerSeqNum;
	double expirationTime;
	PeerState peerState;

	public PeerRecord(String peerID,InetAddress peerIPAddress, int peerSeqNum, double expirationTime, PeerState peerState){
		this.peerID=peerID;
		this.peerIPAddress=peerIPAddress;
		this.peerSeqNum=peerSeqNum;
		this.expirationTime=expirationTime;
		this.peerState=peerState;
	}
}

public class PeerTable {
	Hashtable<String,PeerRecord> table;

	public synchronized void addPeer(PeerRecord peerRecord){
		table.put(peerRecord.peerID, peerRecord);
	}

	public synchronized PeerRecord getPeer(String peerID){
		if (table.containsKey(peerID)){
			PeerRecord peer = table.get(peerID);
			if (peer.expirationTime<System.currentTimeMillis()){
				peer.peerState=PeerState.DYING;
				table.remove(peerID);
				return null;
			}else{
				return peer;
			}
		}
		return null;

	}

	public synchronized boolean contains(PeerRecord peerRecord){
		return table.containsKey(peerRecord.peerID);
	}
	
	public synchronized String toString(){
		return table.toString();
	}


}



