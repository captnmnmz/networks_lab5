

public class HelloReceiver implements SimpleMessageHandler, Runnable {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	public void run() {
		while(true) {
			try {
				String received = incoming.dequeue();
				HelloMessage hm = new HelloMessage(received);
				
				if (!PeerTable.containsPeer(hm.getSenderId())) {
					PeerTable.addPeer();
				}
				//Update peer in any case
				PeerTable.updatePeer();
				
				String message = hm.toString();
				//Print the content on the screen
				System.out.println(message);
			}catch(IllegalArgumentException e) {
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
