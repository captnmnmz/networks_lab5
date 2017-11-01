package networks_lab5;

/**
 * 
 * @author bastienchevallier
 *
 */


public interface SimpleMessageHandler {
	public void handleMessage(String m);
	
	public void setMuxDemux(MuxDemuxSimple md);
}
