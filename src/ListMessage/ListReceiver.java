package ListMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import materials.PeerRecord;
import materials.PeerTable;
import java.net.InetAddress;
import materials.Database;

/**
 * ListReceiver is the class that will handle ListMessage and update the data associated to a peer in the Peer Table
 * 
 * @author Bastien Chevallier & Jules Yates
 *
 */

public class ListReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	@Override
	public void run() {
		while(true) {
			try {
				String received = incoming.dequeue();
				ListMessage lm = new ListMessage(received);
				//Is ListMessage for me ?
				if(lm.getPeerId().equals(myMuxDemux.getID())) {

					Runnable listReceiver = new Runnable(){
						@Override
						public void run(){
							int totalParts=lm.getTotalParts();
							String senderID = lm.getSenderId();
							//Is the SenderID already in our Peer list ? & I'm not the sender of the ListMessage
							if(myMuxDemux.getPeerDatabase().containsKey(senderID) && !senderID.equals(myMuxDemux.getID())){
								Database updated = new Database(lm.getSequenceNumber());
								for (int i=0; i<totalParts; i++){
									updated.add(lm.getData());
								}
								myMuxDemux.getPeerDatabase().put(senderID, updated);
								PeerTable.sync(senderID);
								
								PeerTable.cancelTask(senderID);
							}

							
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

	/**
	 * 
	 * This method aims to handle all the received messages
	 * 
	 * @param m
	 * 		This is the message received which is enqueued in incoming
	 */
	@Override
	public void handleMessage(String m) {
		try {
			incoming.enqueue(m);
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * 
	 * This method aims to set the MuxDemuxSimple object of the class
	 * 
	 * @param md
	 * 		A MuxDemuxSimple Object
	 */
	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		myMuxDemux = md;
	}

}
