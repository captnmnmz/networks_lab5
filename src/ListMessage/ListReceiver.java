package ListMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import materials.PeerRecord;
import materials.PeerTable;

import java.net.InetAddress;

import materials.Database;

public class ListReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);


	
	@Override
	public void run() {
		while(true) {
			try {
				String received = incoming.dequeue();
				ListMessage lm = new ListMessage(received);
				//ListMessage is for me
				//TODO 
				if(lm.getPeerId().equals(myMuxDemux.getID())) {
					//TODO More difficult cases : PeerState = synchronised process ??
					Runnable listReceiver = new Runnable(){
						@Override
						public void run(){
							int totalParts=lm.getTotalParts();
							String senderID = lm.getSenderId();
							if(myMuxDemux.getPeerDatabase().containsKey(senderID) || !senderID.equals(myMuxDemux.getID())){
								Database updated = new Database(senderID, lm.getSequenceNumber());
								for (int i=0; i<totalParts; i++){
									updated.add(lm.getData());
								}
								myMuxDemux.getPeerDatabase().put(senderID, updated);
							
							}

							//Update the PeerTable by putting a new entry 
							PeerRecord peer = PeerTable.getPeer(senderID);
							int HelloInterval = peer.getHelloInterval();
							InetAddress peerIPAddress = peer.getAddress();
							
							PeerTable.addPeer(senderID, peerIPAddress, HelloInterval, lm.getSequenceNumber());
							
							PeerTable.cancelTask(senderID);

							

							
						}
					};
					
					Thread receiverThread = new Thread(listReceiver);
					receiverThread.start();
					
				}

			}catch(IllegalArgumentException e) {
				if(e.getMessage().equals("The message must begin by LIST \n\r"
						+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;")) {
						//Do nothing : the message wasn't a SynMessage
				}else {
					//The SynMessage wasn't formatted as it was supposed to be
					System.err.println(e.getMessage());
				}
			}
		}
	}

	@Override
	public void handleMessage(String m) {
		
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		
	}

}
