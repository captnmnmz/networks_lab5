
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SynchronizedQueue {
	
	private final LinkedList<String> queue;
	private int size;

	public SynchronizedQueue(int size) {
		queue = new LinkedList<String>();
		this.size = size;
	}

	public synchronized boolean isEmpty() {
		return queue.size() == 0;
	}

	public synchronized boolean isFull() {
		//Modification from the previous SynchronizedQueue
		return size == queue.size();
	}

	public synchronized void enqueue(String url) throws Exception {
		if (isFull()) {
			throw new Exception("The queue is already full");
		}
		else if (isEmpty()){
			notify();
		}
		queue.add(url);
	}

	public synchronized String dequeue() {
		if (this.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return queue.remove();
	}

}