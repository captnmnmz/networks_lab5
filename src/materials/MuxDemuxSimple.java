package materials;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MuxDemuxSimple implements Runnable {
	private final Boolean broadcast = true;
	private DatagramSocket myS = null;
	private BufferedReader in;
	private SimpleMessageHandler[] myMessageHandlers;
	private SynchronizedQueue outgoing = new SynchronizedQueue(20);
    private final String ADDRESS = "255.255.255.255";
    private final int PORT = 4242;
    private Database my_db=null;
    private HashMap<String,Database> peers_db;
    private String peerID;
    private int HelloInterval;
	
	public MuxDemuxSimple(SimpleMessageHandler[] h, DatagramSocket s, String peerID, int HelloInterval) {
		myS= s;
		myMessageHandlers = h;
		for (int i=0; i< myMessageHandlers.length ; i++) {
			myMessageHandlers[i].setMuxDemux(this);
		}
		this.HelloInterval=HelloInterval;
		this.peerID = peerID;
		my_db=new Database(-1);
		//Run the thread that will screen the root folder and update the database
		my_db.screenDB();
		// We create our own directory
		new File("C:/Users/jules/Google Drive/Cours Polytechnique/From the internet to the IoT/mySharedFileFolder").mkdir();
		peers_db=new HashMap<String,Database>();

	}
	
	public void run() {
		//Initialisation
		for (int i=0; i< myMessageHandlers.length ; i++) {
			myMessageHandlers[i].setMuxDemux(this);
		}
		
		//Receive
		Runnable broadcast_receiver = new Runnable() {
			public void run() {
				while(true) {
					byte[] bufferReceived = new byte[512];
					DatagramPacket dpReceived;
					try {
						dpReceived = new DatagramPacket(
											bufferReceived,
											bufferReceived.length,
											InetAddress.getByName(ADDRESS),
											PORT);
						myS.receive(dpReceived);
						int endIndex = dpReceived.getLength();
						String peerIPAddress = dpReceived.getAddress().toString();
						String message = new String(dpReceived.getData()).substring(0, endIndex);
						for (int i=0; i<myMessageHandlers.length; i++){
							myMessageHandlers[i].handleMessage(message);
						}
					} catch (UnknownHostException e){
					} catch (IOException e) {
					}
				}

			}
		};
		
		Runnable multicast_receiver = new Runnable(){
			public void run(){
				//TODO
		        try {
					MulticastSocket socket = new MulticastSocket(PORT);
					InetAddress group=InetAddress.getByName(ADDRESS);
					socket.joinGroup(group);
					byte[] buf = new byte[2560];
			        while (true) {
			            DatagramPacket packet = new DatagramPacket(buf, buf.length);
			            socket.receive(packet);
			            String received = new String(packet.getData(), 0, packet.getLength());
			            if ("end".equals(received)) {
			                break;
			            }
			        }
			        socket.leaveGroup(group);
			        socket.close();
				} catch (UnknownHostException e) {
					System.err.println(e.getMessage());
		        } catch (IOException e) {
		        		System.err.println(e.getMessage());
				}
			}
		};
		
		//Send
		Runnable sender = new Runnable() {
			public void run() {
				while(true) {
					try {
						String broadcastMessage = outgoing.dequeue();
						DatagramSocket broadcast_s= myS;
						byte[] buf = broadcastMessage.getBytes();
						DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(ADDRESS), PORT);
						broadcast_s.send(packet);
					} catch (UnknownHostException e) {
						System.err.println(e.getMessage());
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				}
			}
		};
		

		Thread senderThread = new Thread(sender);
		Thread receiverThread=new Thread(broadcast_receiver);

		receiverThread.start();
		senderThread.start();
	}

	
	public void send(String m) {
		try {
			outgoing.enqueue(m);
		}catch(Exception e ) {
			System.err.println(e.getMessage());
		}
	}
	
	public String getID(){
		return this.peerID;
	}
	
	public int getHelloInterval(){
		return this.HelloInterval;
	}
	
	public Database getDatabase() {
		return this.my_db;
	}
	
	public HashMap<String,Database> getPeerDatabase(){
		return this.peers_db;
	}
	
}
