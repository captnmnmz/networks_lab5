

/**
 * 
 * @author bastienchevallier
 *
 */


public interface SimpleMessageHandler extends Runnable {
	public void handleMessage(String m);
	
	public void setMuxDemux(MuxDemuxSimple md);
}
