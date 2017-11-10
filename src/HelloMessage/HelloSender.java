package HelloMessage;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import materials.MuxDemuxSimple;
import materials.PeerTable;
import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;


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


public class HelloSender implements SimpleMessageHandler {

	private MuxDemuxSimple myMuxDemux= null;
	private SynchronizedQueue incoming = new SynchronizedQueue(20);
	private Timer TIMER = new Timer("SendingTimer", true);

	public void run() {
		try {
			//Generate a random senderID
			//String senderID = RandomAlphanumeric.generateRandomAlphanumeric(16);
			String senderID = "Yates";
			//Generate a random HelloInterval < 256
			//int HelloInterval = new Random().nextInt(256);
			int HelloInterval = 253;
			HelloMessage m = new HelloMessage(senderID,-1,HelloInterval);
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					List<String> list_peers = PeerTable.sendPeersID();
					if(list_peers!=null) {
						for (int i=0; i<list_peers.size(); i++) {
							m.addPeer(list_peers.get(i));
						}
					}
					myMuxDemux.send(m.getHelloMessageAsEncodedString());
				}
			};
			//We send Hello message before reaching the maximum time
			int delay = new Random().nextInt(HelloInterval);
			TIMER.schedule(task,0,250);
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
