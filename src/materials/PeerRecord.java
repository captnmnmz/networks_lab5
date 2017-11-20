package materials;

import java.net.InetAddress;

public class PeerRecord {
	private String peerID;
	InetAddress peerIPAddress;
	private int peerSeqNum;
	double expirationTime;
	int helloInterval;
	private PeerState peerState;
	public double synTime;

	public PeerRecord(String peerID,InetAddress peerIPAddress, int peerSeqNum, int HelloInterval, PeerState peerState){
		this.peerID=peerID;
		this.peerIPAddress=peerIPAddress;
		this.peerSeqNum=peerSeqNum;
		this.expirationTime=HelloInterval*1000+System.currentTimeMillis();
		this.helloInterval=HelloInterval;
		this.setPeerState(peerState);
	}

	public PeerState getPeerState() {
		return peerState;
	}

	public void setPeerState(PeerState peerState) {
		this.peerState = peerState;
	}
	
	public int getPeerSeqNum() {
		return peerSeqNum;
	}
	
	public String getPeerId() {
		return peerID;
	}
	
	public void setSynTime(){
		this.synTime=System.currentTimeMillis();
	}
	
	public void setExpirationTime(int HelloInterval){
		this.expirationTime=HelloInterval*1000+System.currentTimeMillis();
	}
	
	public int getHelloInterval(){
		return this.helloInterval;
	}
	
	public InetAddress getAddress(){
		return this.peerIPAddress;
	}
	

}
