
import java.net.DatagramSocket;

import HelloMessage.DebugReceiver;
import HelloMessage.HelloReceiver;
import HelloMessage.HelloSender;
import materials.MuxDemuxSimple;
import materials.SimpleMessageHandler;

public class HelloMessageTest {

	public static void main(String[] args) {
		try {
			DatagramSocket mySocket = new DatagramSocket(4242);
			mySocket.setBroadcast(true);
			SimpleMessageHandler[] handlers = new SimpleMessageHandler[3];
			handlers[0]= new HelloReceiver();
			handlers[1]= new HelloSender();
			handlers[2]= new DebugReceiver();
			MuxDemuxSimple dm = new MuxDemuxSimple(handlers, mySocket,"Oliver", 254);
			new Thread(dm).start();
			new Thread(handlers[0]).start();
			new Thread(handlers[1]).start();
			new Thread(handlers[2]).start();
	    }catch(Exception e ) {
	    		System.err.println(e.getMessage());
	    }
	}
}
