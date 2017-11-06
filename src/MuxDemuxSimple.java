
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MuxDemuxSimple implements Runnable {
	private final Boolean broadcast = true;
	private Socket myS = null;
	private BufferedReader in;
	private SimpleMessageHandler[] myMessageHandlers;
	private SynchronizedQueue outgoing = new SynchronizedQueue(20);
    private final String ADDRESS = "255.255.255.255";
    private final int PORT = 4242;
	
	public MuxDemuxSimple(SimpleMessageHandler[] h, Socket s) {
		myS= s;
		myMessageHandlers = h;
	}
	
	public void run() {
		//Initialisation
		for (int i=0; i< myMessageHandlers.length ; i++) {
			myMessageHandlers[i].setMuxDemux(this);
		}
		
		//Receive
		Runnable broadcast_receiver = new Runnable() {
			public void run() {
				try {
					in = new BufferedReader(new InputStreamReader(myS.getInputStream()));
					String message;
					while ((message = in.readLine())!=null) {
						for (int i=0; i< myMessageHandlers.length ; i++) {
							myMessageHandlers[i].handleMessage(message);
						}
					}
					in.close();
					myS.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
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
						DatagramSocket broadcast_s= new DatagramSocket();
						broadcast_s.setBroadcast(true);
						byte[] buf = broadcastMessage.getBytes();
						DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(ADDRESS), PORT);
						broadcast_s.send(packet);
						broadcast_s.close();
					} catch (UnknownHostException e) {
						System.err.println(e.getMessage());
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				}
			}
		};
		

		Thread senderThread = new Thread(sender);
		Thread receiverThread;
;		if (broadcast){
			receiverThread = new Thread(broadcast_receiver);
		}else{
			receiverThread = new Thread(multicast_receiver);
		}
		
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
}
