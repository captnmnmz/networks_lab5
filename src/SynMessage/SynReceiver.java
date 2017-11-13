package SynMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import ListMessage.ListMessage;
import materials.Database;

public class SynReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	private String processing = "standby";
	
	@Override
	public void run() {
		while (true) {
			try {
				String message = incoming.dequeue();
				SynMessage sm = new SynMessage(message); // throws IllegalArgumentException
				//The message is for me and I'm not in the process of sending LIST messages to this peer
				if (myMuxDemux.getID().equals(sm.getPeerId()) && processing.equals(sm.getSenderId())) {
					processing = sm.getSenderId();
					// Verify if the sequenceNo refers to our current database
					if(sm.getSequenceNumber() == myMuxDemux.getDatabase().getDatabaseSequenceNumber()) {
						String data = myMuxDemux.getDatabase().getData();
						//According to the data in database, set TotalParts
						int TotalParts;
						//Split data into String containing maximum 255 characters
						String data;
						for (int i=0; i< TotalParts; i++) {
							ListMessage lm = new ListMessage(myMuxDemux.getID(), sm.getSequenceNumber(), sm.getSenderId(), TotalParts, i+1, data);
							myMuxDemux.send(lm.getListMessageAsEncodedString());
						}
						processing = "standby";
					}	
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
		
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		
	}

}
