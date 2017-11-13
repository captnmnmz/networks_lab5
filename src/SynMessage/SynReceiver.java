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
						//According to the data's length, set TotalParts
						int TotalParts = data.length()/255;
						//Split data into String containing maximum 255 characters
						for (int i=0; i<TotalParts; i++) {
							String _data = data.substring(0, 255);
							ListMessage lm = new ListMessage(myMuxDemux.getID(), sm.getSequenceNumber(), sm.getSenderId(), TotalParts, i+1, _data);
							data = data.substring(255);
							//Send a LIST message containing a part of the data
							myMuxDemux.send(lm.getListMessageAsEncodedString());
						}
					}
					//End of the process
					processing = "standby";
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
