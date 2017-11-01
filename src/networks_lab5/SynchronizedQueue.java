package networks_lab5;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SynchronizedQueue {
	
	private final LinkedList<String> queue;
	private int size;

	public SynchronizedQueue(int i) {
		queue = new LinkedList<String>();
		size = i;
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
		queue.add(url);
	}

	public synchronized String dequeue() throws NoSuchElementException {
		if (this.isEmpty()) {
			throw new NoSuchElementException("The queue is already empty");
		}
		return queue.remove();
	}

}
