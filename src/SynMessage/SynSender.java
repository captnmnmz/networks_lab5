package SynMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import materials.PeerRecord;
import materials.PeerTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import materials.PeerRecord;
import materials.PeerState;
import materials.Database;

public class SynSender implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	private String SENDER = "Chevallier";
	private int SYNINTERVAL = 150;
	
	@Override
	public void run() {
		List<PeerRecord> toSynchronize = this.identifyUnsynchronizedPeer();
		for (PeerRecord peer : toSynchronize) {
			SynMessage message = new SynMessage(SENDER,peer.getPeerSeqNum(),peer.getPeerId());
			myMuxDemux.send(message.getSynMessageAsEncodedString());
			try {
				Thread.sleep(SYNINTERVAL);
				//WAIT ANSWER "LIST" FROM PEER OR RESEND
			} catch (InterruptedException e) {
				System.err.println("Sleep rose an error: " + e.getMessage());
			}
		}
	}
	
	public synchronized List<PeerRecord> identifyUnsynchronizedPeer() {
		List<PeerRecord> UnsynchronizedPeer = new ArrayList<PeerRecord>();
		try {
			//Wait for modification of PeerState in the Database
			wait();
			//Send a SYN message to the unsynchronized peer
			HashMap<String,PeerRecord> mydb = Database.getOwnDatabase();
			//Find the unsynchronised peer
			if(!mydb.keySet().isEmpty()){
				PeerTable.cleanUp();
				for (String id : mydb.keySet()){
					if(mydb.get(id).getPeerState()==PeerState.INCONSISTENT) {
						UnsynchronizedPeer.add(mydb.get(id));
					}
					if(mydb.get(id).getPeerState()==PeerState.HEARD) {
						//TODO
					}
				}
			}
			return UnsynchronizedPeer;
		} catch (InterruptedException e) {
			System.err.println(e);
			return UnsynchronizedPeer;
		}
		
	}

	@Override
	public void handleMessage(String m) {
		try {
			incoming.enqueue(m);
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		myMuxDemux = md;
	}

}
