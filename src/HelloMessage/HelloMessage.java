package HelloMessage;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import materials.PeerTable;

public class HelloMessage {
	private String senderID;
	private int sequence_num;
	private int hello_interval;
	private int NumPeers;
	private List<String> peers = new ArrayList<String>();

	/**
	 * This method populates the attributes of the HelloMessage object.
	 * 
	 * @param s
	 * 			String formatted as "HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN"
	 *
	 */

	public HelloMessage(String s) throws IllegalArgumentException {
		
		String[] _s = s.split(";");
		//TODO Verify that the first item is "HELLO" ?
		
		//Verify the number of attribute ? Less than 5 errors
		if(_s[0].equals("HELLO")) {
			if(_s.length >= 5) {

				//Verify that senderID contains only characters A-Z a-z and 0-9 
				if (!_s[1].matches("[^\\w\\d\\;]")) {

					//Verify that senderID's length is lower or equal to 16
					if(_s[1].length()<=16) {
						senderID = _s[1];
						sequence_num = Integer.parseInt(_s[2]);

						//Verify that HelloInterval is in [0,255]
						int HelloInterval = Integer.parseInt(_s[3]);
						if(0<=HelloInterval && HelloInterval<256) {
							hello_interval = Integer.parseInt(_s[3]);
							int NumPeers_temp = Integer.parseInt(_s[4].replaceAll("[^0-9]", ""));
							if (0<=NumPeers_temp && NumPeers_temp<256) {
								NumPeers = NumPeers_temp;
								if (NumPeers!=0) {
									for (int i=0; i<NumPeers; i++) {
										peers.add(_s[5+i]);
										
									}
								}
							}else {
								throw new IllegalArgumentException("The number of peers is too large : it must be contained in [0,255] \n\r"
										+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN");
							}
						}else {
							throw new IllegalArgumentException("The hello interval is too large : it must be contained in [0,255] \n\r"
									+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN");
						}
					}else {
						throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
								+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN");
					}
				}else {
					throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
							+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN");
				}
			}else {
				throw new IllegalArgumentException("The HelloMessage must contain more than 5 items \n\r"
						+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN") ;
			}
		}else {
			throw new IllegalArgumentException("The message must begin by HELLO \n\r"
					+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN");
		}
	}


	/**
	 * This method populates the attributes of the HelloMessage object.
	 * 
	 * @param senderID
	 * 			Must be a string of up to 16 characters and must contain only characters A-Z a-z and 0-9
	 * @param sequenceNo
	 * @param HelloInterval
	 * 			Must be an int in [0,255] 
	 * 
	 * @throws IllegalArgumentException
	 */
	public HelloMessage(String senderID, int sequenceNo, int HelloInterval) throws IllegalArgumentException {
		//Verify that senderID contains only characters A-Z a-z and 0-9 
		if (!senderID.matches("[^\\w\\d\\;]")) {
			//Verify that senderID's length is lower or equal to 16
			if(senderID.length()<=16) {
				this.senderID = senderID;
				sequence_num = sequenceNo;
				//Verify that HelloInterval is in [0,255]  
				if(0<=HelloInterval && HelloInterval<256) {
					hello_interval = HelloInterval;
					NumPeers = 0;
				}else {
					throw new IllegalArgumentException("The hello interval is too large : it must be contain in [0,255] \n\r"
							+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN");
				}
			}else {
				throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
						+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN");
			}
		}else {
			throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
					+"The string is supposed to be formatted as : HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN");
		}
	}

	/**
	 * This method returns the attributes of the HelloMessage object as a formatted string.
	 * 
	 * @return Return a string formatted like this : "HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN" 
	 *
	 */
	public String getHelloMessageAsEncodedString() {
		String message = "HELLO;"+ senderID +";"+Integer.toString(sequence_num)+";"+Integer.toString(hello_interval)+";"+Integer.toString(NumPeers);
		for (int i=0; i<peers.size(); i++) {
			message = message + ";" + peers.get(i);
		}
		return message;
	}

	/**
	 * This method adds a peer to the list if this one is not full. If the list is full (256 elements), display an error message
	 * 
	 * @param peerID
	 * 		A String that is the ID of the peer to add
	 *
	 */

	public void addPeer (String peerID) {
		//Verify that the list of peers is full and if peerID's length is lower or equal to 16 and if it contains only characters A-Z a-z and 0-9 
		if (NumPeers<255) {
			if (!peerID.matches("[^\\w\\d\\;]")) {
				if(peerID.length()<=16) {
					peers=PeerTable.sendPeersID();
					NumPeers = peers.size();
				}else {
					System.err.println("The id is too large : id must be a string of up to 16 characters");
				}
			}else {
				System.err.println("The id must contain only characters A-Z a-z and 0-9 ");
			}
		}else {
			System.err.println("There are  already 256 peers : impossible to add peer");
		}
	}

	/**
	 * This method returns the attributes of the HelloMessage object as a readable message.
	 * 
	 * @return Return a message which presents the attributes of the HelloMessage
	 */
	
	public String toString() {
		String message = "Details of the HelloMessage : \n\r";
		message += "SenderID : " + senderID +"\n\r";
		message += "Sequence Number : " + Integer.toString(sequence_num) + "\n\r";
		message += "Hello interval : " +Integer.toString(hello_interval) + "\n\r";
		message += "NumPeers : " + Integer.toString(NumPeers) + "\n\r";
		for (int i=0; i<NumPeers; i++) {
			message += "Peers #"+Integer.toString(i+1)+ " : " + peers.get(i) + "\n\r";
		}
		return message;
	}

	public String getSenderId() {
		return this.senderID;
	}

	public int getSequenceNumber() {
		return this.sequence_num;
	}

	public int getHelloInterval() {
		return this.hello_interval;
	}

	public int getNumPeers() {
		return this.NumPeers;
	}

	/**
	 * This method returns the LinkedList<String> of peers
	 * 
	 * @return if there is no peer, the method return an empty LinkedList<String>
	 * 
	 */

	public List<String> getPeers() {
		return this.peers;
	}

	/**
	 * This method returns the peer at the specified position in the list peers.
	 * 
	 * @param index
	 * 			index of the element to return
	 * @return if there is no peer or if the index is out of range, the method raise an IndexOutOfBoundsException
	 * 
	 */
	public String getPeerId(int index) {
		return this.peers.get(index);
	}

	/*
	 * No need of set...() methods because we already have constructors and addPeer()
	 */

}
