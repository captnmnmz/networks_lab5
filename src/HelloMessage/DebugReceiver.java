package HelloMessage;
import materials.MuxDemuxSimple;
import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;

public class DebugReceiver implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	@Override
	public void run() {
		while(true){
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
		
	}

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
