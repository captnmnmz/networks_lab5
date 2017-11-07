import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class HelloMessageTest {

	public static void main(String[] args) {
		try {
			Socket mySocket = new Socket(InetAddress.getByName("255.255.255.255"),4242);
			SimpleMessageHandler[] handlers = new SimpleMessageHandler[3];
			handlers[0]= new HelloReceiver();
			handlers[1]= new HelloSender();
			handlers[2]= new DebugReceiver();
			MuxDemuxSimple dm = new MuxDemuxSimple(handlers, mySocket);
			new Thread(handlers[0]).start();
			new Thread(handlers[1]).start();
			new Thread(handlers[2]).start();
			new Thread(dm).start();
	    }catch(Exception e ) {
	    		System.err.println(e.getMessage());
	    }
	}
}
