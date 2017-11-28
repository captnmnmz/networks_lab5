package ExchangeFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.MissingResourceException;

import materials.Database;
import materials.PeerTable;

/**
 * This class is a synchronized wrapper for a regular queue. It is used for store the couple (peerID, filename) of the file to download.
 * 
 * @author Bastien CHEVALLIER
 *
 */

class FilenameQueue {

	private final LinkedList<String> queue;
	private int size;

	public FilenameQueue(int size) {
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

	public synchronized void enqueue(String filename) {
		if (queue.size() < size) {
			queue.add(filename);
			notify();
		}
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

/**
 * 
 * 
 * @author Bastien Chevallier
 *
 */

public class GettingAllFilesFromPeer implements Runnable {
	//TODO Define the size of the queue
	private int PORT = 4242;
	private static final int CHUNKED = -1;
	private String peerID;
	private String root = "/Users/bastienchevallier/Documents/IoT/";
	private FilenameQueue fileToDownload = new FilenameQueue(20);

	public GettingAllFilesFromPeer(Database peer_db, String peerID) {
		this.peerID = peerID;
		ArrayList<String> filename_list = peer_db.getData();
		for (int i=0; i<filename_list.size(); i++ ) {
			fileToDownload.enqueue(filename_list.get(i));
		}
	}

	
	/**
	 * Download the document part through an already open TCP connection. Header
	 * is supposed partially parsed, but not length specification.
	 * 
	 * @param answerStream
	 *          the input stream for the current TCP connection
	 * @param fileName
	 *          the name of a local file where the result will be stored
	 */
	
	private static void download(BufferedReader answerStream, File file) {
		
		//TODO change the length of the buffer
		int length = 1024;
		char[] buffer = new char[length > 0 ? length : 0]; // initial allocation

		// readLine returns null when the end of the input stream has been reached,
		// this means that the server has shutdown the stream,
		// but it is not always the case ... or not always immediately ...
		// we should also consider the "Content-Length" information from the header
		// and also handle the case of chunked data
		// used when the server doesn't know the length at start, or
		// to accomodate with smaller buffer size.
		String line = "";
		int count = 0;
		// __Test__.assertFalse("chunked encoding not supported", chunked);
		
		try {
			PrintWriter pw = new PrintWriter(file);
			while (line != null) {
				line = answerStream.readLine();
				if (line.length() == 0)
					line = answerStream.readLine();
				length = Integer.parseInt(line, 16);
				count = 0;
				if (length == 0)
					break;
				
				if (buffer.length < length)
					buffer = new char[length]; // size extended as needed
				while (count < length) {
					int n = answerStream.read(buffer, count, length - count);
					if (n < 0) { // reached EOF
						length = count;
						break;
					}
					count += n;
					// System.out.println(n + " " + count);
				}
				pw.write(buffer, 0, length);
				// uncomment the next line for exercise 3
				if (count >= length)
					break;
			}
			pw.flush();
			pw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-11);
		}
	}
	
	@Override
	public void run() {
		while(!fileToDownload.isEmpty()) {
			String filename = fileToDownload.dequeue();
			//TCP Client Socket with peerAddress
			try {
				//TODO the socket is created each time even if it is the same peer
				System.out.println(PeerTable.getPeer(peerID).getAddress());
				Socket clientsocket = new Socket(PeerTable.getPeer(peerID).getAddress(),PORT);

				//Send the request for the corresponding filename
				OutputStream output = clientsocket.getOutputStream();
				PrintWriter pw = new PrintWriter(output,true);
				String request = "get " + filename +  System.getProperty( "line.separator" );
				pw.println(request);

				//Receive the corresponding file
				BufferedReader br = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
				File received_file = new File(root + peerID + "/" + filename );

				download(br,received_file);


				pw.close();
				output.close();


				br.close();

				clientsocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
