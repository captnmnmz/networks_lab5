package ListMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import materials.Database;

public class ListSender implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
	@Override
	public void run() {
		
	}

	@Override
	public void handleMessage(String m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		// TODO Auto-generated method stub
		
	}

}
