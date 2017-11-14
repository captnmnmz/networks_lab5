package SynMessage;

import java.util.HashSet;
import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import ListMessage.ListMessage;

/**
 * This class is a synchronized set that keeps tabs on which peers are been dealt by the SYN handler (or more precisely by its sub-threads)
 * @author jules
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
				String message = incoming.dequeue();
				SynMessage sm = new SynMessage(message); // throws IllegalArgumentException
				
				//The message is for me and I'm not in the process of sending LIST messages to this peer
				if (myMuxDemux.getID().equals(sm.getPeerId()) && !processing.contains(sm.getSenderId())) {
					
					// Verify if the sequenceNo refers to our current database
					if(sm.getSequenceNumber() == myMuxDemux.getDatabase().getDatabaseSequenceNumber()) {
						processing.add(sm.getSenderId());
						
						Runnable listSender = new Runnable(){
							@Override
							public void run(){
								String data = myMuxDemux.getDatabase().getData();
								//TODO check that this is really the number of parts
								int TotalParts = data.length()/255;
								//Split data into String containing maximum 255 characters
								for (int i=0; i<TotalParts; i++) {
									String _data = data.substring(0, 255);
									ListMessage lm = new ListMessage(myMuxDemux.getID(), sm.getSequenceNumber(), sm.getSenderId(), TotalParts, i+1, _data);
									data = data.substring(255);
									//Send a LIST message containing a part of the data
									myMuxDemux.send(lm.getListMessageAsEncodedString());
								}
								processing.remove(sm.getSenderId());
							}
						};
						//According to the data's length, set TotalParts
						
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
