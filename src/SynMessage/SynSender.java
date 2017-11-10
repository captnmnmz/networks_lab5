package SynMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import materials.Database;

public class SynSender implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	@Override
	public void run() {
		
	}

	@Override
	public void handleMessage(String m) {
	
		
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		
	}

}
