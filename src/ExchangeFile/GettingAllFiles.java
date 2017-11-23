package ExchangeFile;

import java.io.File;
import java.util.HashMap;

/**
 * This class is a synchronized wrapper for a regular queue. It is used for store the couple (peerID, filename) of the file to download.
 * 
 * @author Bastien CHEVALLIER
 *
 */

class QueueToDownload {
	
	private final HashMap<String, String> queue;
	private int size;

	public QueueToDownload(int size) {
		queue = new HashMap<String, String>();
		this.size = size;
	}

	public synchronized boolean isEmpty() {
		return queue.size() == 0;
	}

	public synchronized boolean isFull() {
		//Modification from the previous SynchronizedQueue
		return size == queue.size();
	}

	public synchronized void enqueue(String peerID, String filename) throws Exception {
		if (queue.size() < size) {
			queue.put(peerID,filename);
			notify();
		}
	}

	public synchronized String dequeue(String peerID) {
		if (this.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return queue.remove(peerID);
	}
}

/**
 * 
 * 
 * @author Bastien Chevallier
 *
 */
public class GettingAllFiles {
	
	
	public void getAllFiles() {
		//Client Socket with peerAddress
	}
	
}
