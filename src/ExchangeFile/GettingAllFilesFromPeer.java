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
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.MissingResourceException;

import materials.Database;
import materials.MuxDemuxSimple;
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
	private String peerID;
	private String root = MuxDemuxSimple.root_path;
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

	private synchronized void download(BufferedReader answerStream, String filename) {

		//TODO change the length of the buffer
		String filepath = root + peerID + "/" + filename;
		
		// readLine returns null when the end of the input stream has been reached,
		// this means that the server has shutdown the stream,
		// but it is not always the case ... or not always immediately ...
		// we should also consider the "Content-Length" information from the header
		// and also handle the case of chunked data
		// used when the server doesn't know the length at start, or
		// to accomodate with smaller buffer size.
		
		String line = "";
		// __Test__.assertFalse("chunked encoding not supported", chunked);

		try {
			line = answerStream.readLine();
			System.out.println("LINE = " + line);
			if (line.split(System.getProperty("line.separator"))[0].equals(filename)) {
				System.out.println("Ready to download");
				line = answerStream.readLine();
				PrintWriter writer = new PrintWriter(filepath);		
				while(answerStream.ready()) {
					line = answerStream.readLine();
					writer.print(line+System.getProperty("line.separator"));
				}

				writer.close();
				answerStream.close();
			}
		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
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
				download(br,filename);


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
