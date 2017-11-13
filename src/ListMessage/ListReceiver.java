package ListMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import materials.Database;

public class ListReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	private String received_data = "";
	private int TotalParts=0;
	
	@Override
	public void run() {
		while(true) {
			try {
				String received = incoming.dequeue();
				ListMessage lm = new ListMessage(received);
				//ListMessage is for me
				if(lm.getPeerId().equals(myMuxDemux.getID())) {
					//TODO More difficult cases : PeerState = synchronised process ??
					
					//New sequence of ListMessage
					if(TotalParts == 0) {
						TotalParts = lm.getTotalParts();
					}
					String senderID = lm.getSenderId();
					received_data += lm.getData();
					
					if (TotalParts == lm.getPartNumber()) {
						//ListMessages have been successfully received, notify to SynSender
						synchronized (myMuxDemux.getMonitor()) {
							myMuxDemux.getMonitor().notify();
						}
						//Update peerState
						//TODO Update the peerState, expirationTime, etc., in the PeerTable (from previous TD) as appropriate
						
						//Update data in Database
						Database temp = myMuxDemux.getPeerDatabase().get(senderID);
						temp.setData(received_data);
						myMuxDemux.getPeerDatabase().put(senderID, temp);
						
						//Reinitialisation
						received_data="";
						TotalParts = 0;
					}
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
