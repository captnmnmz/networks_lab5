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
import java.time.Instant;
import java.util.Hashtable;

class PeerRecord{
	String peerID;
	InetAddress peerIPAddress;
	int peerSeqNum;
	Instant expirationTime;
	PeerState peerState;

	public PeerRecord(String peerID,InetAddress peerIPAddress, int peerSeqNum, Instant expirationTime, PeerState peerState){
		this.peerID=peerID;
		this.peerIPAddress=peerIPAddress;
		this.peerSeqNum=peerSeqNum;
		this.expirationTime=expirationTime;
		this.peerState=peerState;
	}
}

public class PeerTable {
	Hashtable<String,PeerRecord> table;

	public synchronized void put(PeerRecord peerRecord){
		table.put(peerRecord.peerID, peerRecord);
	}

	public synchronized PeerRecord get(String peerID){
		if (table.contains(peerID)){
			return table.get(peerID);
		}
	}

	public synchronized boolean contains(PeerRecord peerRecord){
		return table.containsKey(peerRecord.peerID);
	}


}



