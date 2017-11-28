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
import java.util.ArrayList;
import java.util.LinkedList;

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
	private String peerID;
	private String root = "C:/Users/jules/Google Drive/Cours Polytechnique/From the internet to the IoT/";
	private FilenameQueue fileToDownload = new FilenameQueue(20);

	public GettingAllFilesFromPeer(Database peer_db, String peerID) {
		this.peerID = peerID;
		ArrayList<String> filename_list = peer_db.getData();
		for (int i=0; i<filename_list.size(); i++ ) {
			fileToDownload.enqueue(filename_list.get(i));
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
				String line;
				File received_file = new File(root + peerID + "/" + filename );
				FileWriter fw = new FileWriter(received_file);
				BufferedWriter bw = new BufferedWriter(fw);
				while(br.ready()) {
					System.out.println("br ready");
					if (!"".equals(line = br.readLine())){
						System.out.println(line);
						bw.write(line + System.getProperty( "line.separator" ));
					}
				}

				pw.close();
				output.close();
				
				bw.close();
				fw.close();
				br.close();

				clientsocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
