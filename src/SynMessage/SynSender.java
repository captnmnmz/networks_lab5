package SynMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import materials.PeerRecord;
import materials.PeerTable;

import java.util.TimerTask;
import java.util.Timer;



public class SynSender implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	private String SENDER = "Oliver";
	//TODO change syninterval
	private int SYNINTERVAL = 2000;
	private Timer TIMER = new Timer("SynTimer", true);
	
	@Override
	public void run() {
		while(true){
			PeerRecord peer = PeerTable.queue.dequeue();
			SynMessage message = new SynMessage(SENDER,peer.getPeerSeqNum(),peer.getPeerId());
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					//TODO change while to condition where it stops when LIST message received
					
					if ((peer.synTime+SYNINTERVAL)<System.currentTimeMillis()){
						myMuxDemux.send(message.getSynMessageAsEncodedString());
					}
					

				}
			};
			TIMER.schedule(task,0,SYNINTERVAL);
			PeerTable.addTask(peer.getPeerId(), task);

			
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
