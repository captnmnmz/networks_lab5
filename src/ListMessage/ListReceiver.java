package ListMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import materials.PeerRecord;
import materials.PeerTable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.ArrayList;

import materials.Database;

/**
 * ListReceiver is the class that will handle ListMessage and update the data associated to a peer in the Peer Table, launching new threads
 * to recuperate LIST messages destined for us.
 * 
 * @author Bastien Chevallier & Jules Yates
 * 
 *
 */

public class ListReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	private  ArrayList<String> new_db;

	@Override
	public void run() {
		while(true) {
			try {
				String received = incoming.dequeue();
				ListMessage lm = new ListMessage(received);

				//Is ListMessage for me ?
				if (myMuxDemux.getID().equals(lm.getPeerId())) {

					String senderID = lm.getSenderId();

					Runnable listReceiver = new Runnable(){
						@Override
						public void run(){
							//Is the SenderID already in our Peer list ? & I'm not the sender of the ListMessage
							if(myMuxDemux.getPeerDatabase().containsKey(senderID) && !senderID.equals(myMuxDemux.getID())) {

								Database peer_db = myMuxDemux.getPeerDatabase().get(senderID);

								/*If the sequence number of this existing peer's database is different of the sequence number we receive
											ie. if it's necessary to update the database*/
								if(peer_db.getDatabaseSequenceNumber()!=lm.getSequenceNumber()) {
									
									//This is the first ListMessage of the update
									if (peer_db.getTotalparts()==0) {
										/* INITIALIZATION */
										//Set the number of Totalparts in the Database of the peer
										peer_db.setTotalparts(lm.getTotalParts());
										//Initialization of the new_db
										new_db = new ArrayList<String>();
									}

									new_db.add(lm.getData());

									//The ListMessage was completely received
									if(peer_db.getTotalparts() == lm.getPartNumber() + 1) {
										peer_db.updateDB(new_db);
										//Reset the number of TotalParts
										peer_db.setTotalparts(0);
										PeerTable.sync(senderID,lm.getSequenceNumber());
										//We can stop the SynMessage sending
										PeerTable.cancelTask(senderID);
									}
								}
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

	public synchronized ArrayList<String> getNewDB() {
		return new_db;
	}

	public synchronized void setNewDB(ArrayList<String> new_database) {
		new_db = new_database;
	}

}
