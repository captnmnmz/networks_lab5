package SynMessage;

import java.util.HashSet;
import java.util.ArrayList;
import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import ListMessage.ListMessage;

/**
 * This class is a synchronized set that keeps tabs on which peers are been dealt by the SYN handler (or more precisely by its sub-threads)
 * 
 * @author Bastien Chevallier
 * @author Jules Yates
 *
 */
class SynProcessing{
	private HashSet<String> set;
	
	public SynProcessing(){
		set=new HashSet<String>();
	}
	
	public synchronized void add(String senderID){
		set.add(senderID);
	}
	
	public synchronized boolean isEmpty(){
		return (set.size()==0);
	}
	
	public synchronized void remove(String senderID){
		set.remove(senderID);
	}
	
	public synchronized boolean contains(String senderID){
		return set.contains(senderID);
	}
	

}

public class SynReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	private SynProcessing processing = new SynProcessing();

	
	@Override
	public void run() {
		while (true) {
			try {
				String _received = incoming.dequeue();
				String[] parsed = _received.split(";",2);
				String message = parsed[1];
				SynMessage sm = new SynMessage(message); // throws IllegalArgumentException
				
				//The message is for me and I'm not in the process of sending LIST messages to this peer
				if (myMuxDemux.getID().equals(sm.getPeerId()) && !processing.contains(sm.getSenderId())) {
					
					// Verify if the sequence number refers to our current database
					if(sm.getSequenceNumber() == myMuxDemux.getDatabase().getDatabaseSequenceNumber()) {
						
						
						Runnable listSender = new Runnable(){
							@Override
							public void run(){
								processing.add(sm.getSenderId());
								ArrayList<String> data = myMuxDemux.getDatabase().getData();
								//TODO check that this is really the number of parts
								int TotalParts=data.size();
								for (int i=0; i<TotalParts; i++) {
									String _data = data.get(i);
									ListMessage lm = new ListMessage(myMuxDemux.getID(), sm.getSequenceNumber(), sm.getSenderId(), TotalParts, i, _data);
									//Send a LIST message containing a part of the data
									myMuxDemux.send(lm.getListMessageAsEncodedString());
								}
								processing.remove(sm.getSenderId());
							}
						};
						
						Thread senderThread = new Thread(listSender);
						senderThread.start();
					}
					//End of the process

				}
			}catch(IllegalArgumentException e) {
				if(e.getMessage().equals("The message must begin by SYN \n\r"
					+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;")) {
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
