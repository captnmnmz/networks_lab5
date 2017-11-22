package ExchangeFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ListMessage.ListMessage;
import materials.Database;
import materials.MuxDemuxSimple;
import materials.PeerTable;
import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;

public class ServingFile implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	
	@Override
	public void run() {
		while(true) {
			try {
				String request = incoming.dequeue();

				// TODO Is ListMessage for me ?
				// if (myMuxDemux.getID().equals(.getPeerId())) {
				//}
				String reg_exp = "(get ([^ \\n]*)\\n)";
				Pattern pattern = Pattern.compile(reg_exp);
				Matcher matcher = pattern.matcher(request);
				
				//TODO
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
