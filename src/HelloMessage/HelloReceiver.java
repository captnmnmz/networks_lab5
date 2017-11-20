package HelloMessage;
import java.net.InetAddress;
import java.net.UnknownHostException;

import materials.MuxDemuxSimple;
import materials.PeerTable;
import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;

public class HelloReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	public void run() {
		while(true) {
			try {
				String received = incoming.dequeue();
				HelloMessage hm = new HelloMessage(received);
				if (!hm.getSenderId().equals(myMuxDemux.getID())){
					if (!PeerTable.containsPeer(hm.getSenderId())) {
						System.out.println("Doesn't know peer");
						PeerTable.addPeer(hm.getSenderId(), InetAddress.getByName("255.255.255.255"), hm.getHelloInterval());;
					}
					//Update peer in any case
					PeerTable.updatePeer(hm.getSenderId(),hm.getSequenceNumber());
					//String message = hm.toString();
					//Print the content on the screen
					//System.out.println("Received : " +message);
				}

				
			}catch(IllegalArgumentException e) {
				if(e.getMessage().equals("The message must begin by HELLO \n\r"
						+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN")) {
						//Do nothing : the message wasn't a HelloMessage
				}else {
					//The HelloMessage wasn't formatted as it was supposed to be
					System.err.println(e.getMessage());
				}
			} catch (UnknownHostException e) {
				System.out.println(e.getMessage());
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
	public void setMuxDemux(MuxDemuxSimple md) {
		myMuxDemux = md;
	}

}
