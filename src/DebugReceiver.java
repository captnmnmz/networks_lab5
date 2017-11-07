
public class DebugReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	@Override
	public void run() {
		String s = incoming.dequeue();
		try {
			//Print raw message
			System.out.println("Debug : " + s );
			if (s.toLowerCase().contains("exception")) {
				//TODO DO SOMETHING
			}
		}catch(Exception e) {
			
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
