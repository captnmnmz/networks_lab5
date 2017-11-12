package SynMessage;


public class SynMessage {
	private String senderID;
	private String peerID;
	private int sequence_num;



	/**
	 * This method populates the attributes of the HelloMessage object.
	 * 
	 * @param s
	 * 		String formatted as "SYN;senderID;peerID;sequence#;"
	 * @throws IllegalArgumentException
	 */

	public SynMessage(String s) throws IllegalArgumentException {
		String[] _s = s.split(";");
		//Verify that the first item is "SYN" ?

		//Verify the number of features 
		if(_s[0].equals("SYN")){
			if(_s.length==4) {

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
								throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
										+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;");
							}
						}else {
							throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
									+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;");
						}
					}else {
						throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
								+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;");
					}
				}else {
					throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
							+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;") ;
				}
			}else{
				throw new IllegalArgumentException("The SynMessage must have 4 features \n\r"
						+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;") ;
			}
		}else{
			throw new IllegalArgumentException("The message must begin by SYN \n\r"
					+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;") ;
		}
	}

	/**
	 * 
	 * This method populates the attributes of the SynMessage object.
	 * 
	 * @param senderID
	 * 			Must be a string of up to 16 characters and must contain only characters A-Z a-z and 0-9
	 * @param sequenceNo
	 * @param peerID
	 * 			Must be a string of up to 16 characters and must contain only characters A-Z a-z and 0-9
	 * @throws IllegalArgumentException
	 */
	public SynMessage(String senderID, int sequenceNo, String peerID) throws IllegalArgumentException {
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
						throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
								+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;");
					}
				}else {
					throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
							+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;");
				}
			}else {
				throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
						+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;");
			}
		}else {
			throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
					+ "The string is supposed to be formatted as : SYN;senderID;peerID;sequence#;");
		}
	}

	/**
	 * This method returns the attributes of the HelloMessage object as a formatted string.
	 * 
	 * @return Return a string formatted like this : "SYN;senderID;peerID;sequence#;" 
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
