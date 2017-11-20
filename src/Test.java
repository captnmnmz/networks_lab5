

import java.net.DatagramSocket;
import java.net.SocketException;

import HelloMessage.DebugReceiver;
import HelloMessage.HelloReceiver;
import HelloMessage.HelloSender;
import SynMessage.SynReceiver;
import SynMessage.SynSender;
import materials.MuxDemuxSimple;
import materials.SimpleMessageHandler;
import ListMessage.ListReceiver;

public class Test {

	public static void main(String[] args) {
			DatagramSocket mySocket;
			try {
				mySocket = new DatagramSocket(4242);
				mySocket.setBroadcast(true);
				SimpleMessageHandler[] handlers = new SimpleMessageHandler[6];
				handlers[0]= new HelloReceiver();
				handlers[1]= new HelloSender();
				handlers[2]= new DebugReceiver();
				handlers[3]= new SynReceiver();
				handlers[4]= new ListReceiver();
				handlers[5]= new SynSender();
				MuxDemuxSimple dm = new MuxDemuxSimple(handlers, mySocket,"Oliver", 10);
				new Thread(dm).start();
				new Thread(handlers[0]).start();
				new Thread(handlers[1]).start();
				new Thread(handlers[2]).start();
				new Thread(handlers[3]).start();
				new Thread(handlers[4]).start();
				new Thread(handlers[5]).start();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			


	}

}
