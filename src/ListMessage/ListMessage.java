package ListMessage;

import java.util.Arrays;

public class ListMessage {

	private String senderID;
	private String peerID;
	private int sequence_num;
	private int TotalParts;
	private int part_num;
	private String data;

	/**
	 * This method populates the attributes of the ListMessage object.
	 * 
	 * @param s
	 * 			String formatted as "LIST;senderID;peerID;sequence#;TotalParts;part#;data;"
	 * @throws IllegalArgumentException
	 */

	public ListMessage(String s) throws IllegalArgumentException {
		String[] _s_ = s.split(";",8);
		//Verify that the first item is "LIST" ?
		String[] _s = Arrays.copyOfRange(_s_,0,7);
		//Verify the number of features 
		if(_s[0].equals("LIST")) {
			if(_s.length==7) {

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
								TotalParts = Integer.parseInt(_s[4]);
								part_num = Integer.parseInt(_s[5]);
								if(part_num <= TotalParts) {
									if(_s[6].trim().length()<=255) {
										data = _s[6];
									}else {
										throw new IllegalArgumentException("Data is a text string of max 255 characters \n\r"
												+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
									}
								}else {
									throw new IllegalArgumentException("Impossible to get part# > TotalParts \n\r"
											+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
								}
							}else {
								throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
										+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
							}
						}else {
							throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
									+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
						}
					}else {
						throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
								+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
					}
				}else {
					throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
							+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
				}
			}else {
				throw new IllegalArgumentException(" The ListMessage must have 7 features \n\r"
						+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;") ;
			}
		}else {
			throw new IllegalArgumentException("The message must begin by LIST \n\r"
					+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
		}
	}

	/**
	 * 
	 * This method populates the attributes of the ListMessage object.
	 * 
	 * @param senderID
	 * 		Must be a string of up to 16 characters and must contain only characters A-Z a-z and 0-9
	 * @param sequenceNo
	 * @param peerID
	 * 		Must be a string of up to 16 characters and must contain only characters A-Z a-z and 0-9
	 * @param totalpart
	 * @param part_Number
	 * 		Must be lower than totalpart. Part_Number begins to 0.
	 * @param data
	 * 		Data to send with the ListMessage
	 * 
	 * @throws IllegalArgumentException
	 */
	public ListMessage(String senderID, int sequenceNo, String peerID, int totalpart,int part_Number, String data) throws IllegalArgumentException {
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
						TotalParts = totalpart;
						part_num = part_Number;
						if(part_num <= TotalParts) {
							if(data.length()<=255) {
								this.data =data;
							}else {
								throw new IllegalArgumentException("Data is a text string of max 255 characters \n\r"
										+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
							}
						}else {
							throw new IllegalArgumentException("Impossible to get part# > TotalParts \n\r"
									+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
						}
					}else {
						throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
								+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
					}
				}else {
					throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
							+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
				}
			}else {
				throw new IllegalArgumentException("The id is too large : id must be a string of up to 16 characters \n\r"
						+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
			}
		}else {
			throw new IllegalArgumentException("The id must contain only characters A-Z a-z and 0-9 \n\r"
					+"The string is supposed to be formatted as : LIST;senderID;peerID;sequence#;TotalParts;part#;data;");
		}
	}

	/**
	 * This method returns the attributes of the ListMessage object as a formatted string.
	 * 
	 * @return Return a string formatted like this : "LIST;senderID;peerID;sequence#;TotalParts;part#;data;" 
	 *
	 */
	public String getListMessageAsEncodedString() {
		return "LIST;"+ senderID +";"+peerID+";"+Integer.toString(sequence_num)+";"+ Integer.toString(TotalParts)+";" + Integer.toString(part_num)+";"+ data+";";
	}

	/**
	 * This method returns the attributes of the ListMessage object as a readable message.
	 * 
	 * @return Return a message which presents the attributes of the ListMessage
	 */
	
	public String toString() {
		String message = "Details of the ListMessage : \n\r";
		message += "SenderID : " + senderID +"\n\r";
		message += "PeerID : " + peerID +"\n\r";
		message += "Sequence Number : " + Integer.toString(sequence_num) + "\n\r";
		message += "Total parts : " +  Integer.toString(TotalParts) + "\n\r";
		message += "Part# : " +  Integer.toString(part_num) + "\n\r";
		message += "Data : " +  data + "\n\r";
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

	public int getPartNumber() {
		return this.part_num;
	}

	public int getTotalParts() {
		return this.TotalParts;
	}

	public String getData() {
		return this.data;
	}

	/*
	 * No need of set...() methods because we already have constructors and addPeer()
	 */

}
