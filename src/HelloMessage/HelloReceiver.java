package HelloMessage;

import materials.MuxDemuxSimple;
import materials.PeerTable;

import java.net.InetAddress;
import java.net.UnknownHostException;

import materials.Database;
import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;

public class HelloReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	public void run() {
		while(true) {
			try {
				String _received = incoming.dequeue();
				String[] parsed = _received.split(";",2);
				String received = parsed[1];
				HelloMessage hm = new HelloMessage(received);
				// Not my HelloMessage
				if (!hm.getSenderId().equals(myMuxDemux.getID())){
					if (!PeerTable.containsPeer(hm.getSenderId())) {
						//parsed[0] contains the IP Address of the peer that sent the HelloMessage
						InetAddress peerIPAddress = InetAddress.getByName(parsed[0].substring(1,parsed[0].length()));
						PeerTable.addPeer(hm.getSenderId(), peerIPAddress , hm.getHelloInterval());;
					}
					if (!myMuxDemux.getPeerDatabase().containsKey(hm.getSenderId())){
						Database peerDB = new Database(-1);
						myMuxDemux.getPeerDatabase().put(hm.getSenderId(), peerDB);
					}
					//Update peer in any case
					int HelloInterval = hm.getHelloInterval();
					PeerTable.updatePeer(hm.getSenderId(),hm.getSequenceNumber(), HelloInterval);
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
				System.err.println(e.getMessage());
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
