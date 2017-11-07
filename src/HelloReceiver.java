import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	public void run() {
		while(true) {
			try {
				String received = incoming.dequeue();
				HelloMessage hm = new HelloMessage(received);
				if (!PeerTable.containsPeer(hm.getSenderId())) {
					PeerTable.addPeer(hm.getSenderId(), InetAddress.getByName("255.255.255.255"), hm.getHelloInterval());;
				}
				//Update peer in any case
				PeerTable.updatePeer(hm.getSenderId(),hm.getSequenceNumber());
				String message = hm.toString();
				//Print the content on the screen
				System.out.println("Received : " +message);
				
			}catch(IllegalArgumentException e) {
				System.out.println(e.getMessage());
			} catch (UnknownHostException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void handleMessage(String m) {
		try {
			incoming.enqueue(m);
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void setMuxDemux(MuxDemuxSimple md) {
		myMuxDemux = md;
	}

}
