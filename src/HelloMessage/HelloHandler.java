package HelloMessage;


import java.util.NoSuchElementException;
import materials.MuxDemuxSimple;
import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;

public class HelloHandler implements SimpleMessageHandler, Runnable{
	
	private MuxDemuxSimple myMuxDemux= null;
	//TODO Which size for the SynchronizedQueue
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	
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
	
	
	public void setMuxDemux(MuxDemuxSimple md) {
		myMuxDemux = md;
	}
	
	public void run() {
		while (true) {
			try {
				String msg = incoming.dequeue();
				String message="";
				//DO WHAT HE WANT
				myMuxDemux.send(message);
			}catch(NoSuchElementException e ) {
				System.err.println(e.getMessage());;
			}
		}
	}
	
	
}
