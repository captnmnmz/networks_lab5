package materials;
import java.net.Socket;
import java.util.LinkedList;

public class BlockingListQueue {

	private final LinkedList<PeerRecord> queue;
	
	public BlockingListQueue(){
		this.queue=new LinkedList<PeerRecord>();
	}
	
	public synchronized boolean isEmpty() {
		return queue.size() == 0;
	}

	public synchronized boolean isFull() {
		return false;
	}

	public synchronized void enqueue(PeerRecord peer) {
		queue.add(peer);
		notify();
		
	}

	public synchronized PeerRecord dequeue() {
		while(queue.isEmpty()){
			try{
				System.out.println(Thread.currentThread().toString());
				wait();
			}catch (InterruptedException e){
				System.err.println(e.getMessage());
			}

		}
		return queue.remove();
	}
	
}
