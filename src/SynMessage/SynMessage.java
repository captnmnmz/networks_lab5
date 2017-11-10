package SynMessage;


public class SynMessage {
	private String senderID;
	private String peerID;
	private int sequence_num;

	/**
	 * This method populates the attributes of the HelloMessage object.
	 * 
	 * @param s
	 * 			String formatted as "SYN;senderID;peerID;sequence#;"
	 *
	 */

	public SynMessage(String s) {
		try {
			String[] _s = s.split(";");
			//Verify that the first item is "SYN" ?

			//Verify the number of features 
			if(_s[0].equals("SYN") && _s.length==4) {

				//Verify that senderID contains only characters A-Z a-z and 0-9 
				if (!_s[1].matches("[^\\w\\d\\;]")) {

					//Verify that senderID's length is lower or equal to 16
					if(_s[1].length()<=16) {
						senderID = _s[1];

						//Verify that peerID contains only characters A-Z a-z and 0-9 
						if (!_s[1].matches("[^\\w\\d\\;]")) {

							//Verify that peerID's length is lower or equal to 16
							if(_s[1].length()<=16) {
								peerID = _s[2];
								sequence_num = Integer.parseInt(_s[3]);

							}else {
								throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters");
							}
						}else {
							throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 ");
						}
					}else {
						throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters");
					}
				}else {
					throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 ");
				}
			}else {
				throw new IllegalArgumentException("The message must begin by SYN and must have 4 features") ;
			}
		}catch(IllegalArgumentException e) {
			System.err.println("The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;");
			System.err.println(e.getMessage());
		}
	}

	/**
	 * This method populates the attributes of the SynMessage object.
	 * 
	 */
	public SynMessage(String senderID, int sequenceNo, String peerID) {
		try {
			//Verify that senderID contains only characters A-Z a-z and 0-9 
			if (!senderID.matches("[^\\w\\d\\;]")) {

				//Verify that senderID's length is lower or equal to 16
				if(senderID.length()<=16) {
					this.senderID = senderID;

					//Verify that peerID contains only characters A-Z a-z and 0-9 
					if (!peerID.matches("[^\\w\\d\\;]")) {

						//Verify that peerID's length is lower or equal to 16
						if(peerID.length()<=16) {
							this.peerID = peerID;
							sequence_num = sequenceNo;

						}else {
							throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters");
						}
					}else {
						throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 ");
					}
				}else {
					throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters");
				}
			}else {
				throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 ");
			}
		}catch(IllegalArgumentException e) {
			System.err.println("The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;");
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * This method returns the attributes of the HelloMessage object as a formatted string.
	 * 
	 * @return Return a string formatted like this : "HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN" 
	 *
	 */
	public String getSynMessageAsEncodedString() {
		return "SYN;"+ senderID +";"+peerID+";"+Integer.toString(sequence_num)+";";

	}
	
	
	public String toString() {
		String message = "Details of the SynMessage : \n\r";
		message += "SenderID : " + senderID +"\n\r";
		message += "PeerID : " + peerID +"\n\r";
		message += "Sequence Number : " + Integer.toString(sequence_num) + "\n\r";
		return message;
	}
	
	public String getSenderId() {
		return this.senderID;
	}
	
	public String getPeerId() {
		return this.peerID;
	}
	
	public int getSequenceNumber() {
		return this.sequence_num;
	}
	
	public void setSequenceNumber(int seqNo) {
		this.sequence_num = seqNo;
	}
	
	/*
	 * No need of other set...() methods because we already have constructors
	 */

}
