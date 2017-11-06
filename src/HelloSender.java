import java.lang.Object;
import java.util.Random;


class RandomAlphanumeric {
	private static String letters = "abcdefghijklmnopqrstuvwxyz";
	private static char[] alphanumeric = (letters + letters.toUpperCase()+ "0123456789").toCharArray();
	
	public static String generateRandomAlphanumeric(int length) {
		StringBuilder s = new StringBuilder();
		for (int i=0; i<length; i++) {
			s.append(alphanumeric[new Random().nextInt(alphanumeric.length)]);
		}
		return s.toString();
	}
}


public class HelloSender implements SimpleMessageHandler, Runnable {
	
	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	 
	//TODO repeat each HelloInterval
	public void run() {
		try {
			String senderID = RandomAlphanumeric.generateRandomAlphanumeric(16);
			int HelloInterval = new Random().nextInt(256);
			HelloMessage m = new HelloMessage(senderID,1,HelloInterval);
			myMuxDemux.send(m.getHelloMessageAsEncodedString());
		}catch(IllegalArgumentException e) {
			System.err.println(e.getMessage());
		}
		
	}
	@Override
	public void handleMessage(String m) {
		try {
			incoming.enqueue(m);
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		myMuxDemux = md;
	}
	

}
