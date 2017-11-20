package SynMessage;

import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;
import materials.MuxDemuxSimple;
import materials.PeerRecord;
import materials.PeerTable;

import java.util.TimerTask;
import java.util.Timer;

/**
 * SynSender is a thread that send SynMessage to a peer in order to synchronize
 * 
 * @author Bastien Chevallier & Jules Yates
 *
 */

public class SynSender implements SimpleMessageHandler {
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	//TODO change syninterval
	private int SYNINTERVAL = 4000;
	private Timer TIMER = new Timer("SynTimer", true);
	
	@Override
	public void run() {
		while(true){
			PeerRecord peer = PeerTable.queue.dequeue();
			SynMessage message = new SynMessage(myMuxDemux.getID(),peer.getPeerSeqNum(),peer.getPeerId());
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					//TODO change while to condition where it stops when LIST message received
					if ((peer.synTime+SYNINTERVAL)<System.currentTimeMillis()){
						myMuxDemux.send(message.getSynMessageAsEncodedString());

						peer.setSynTime();

					}	

				}
			};
			TIMER.schedule(task,0,SYNINTERVAL);
			PeerTable.addTask(peer.getPeerId(), task);	

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
