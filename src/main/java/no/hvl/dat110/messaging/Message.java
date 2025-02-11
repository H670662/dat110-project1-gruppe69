package no.hvl.dat110.messaging;

import no.hvl.dat110.TODO;

public class Message {

	// the up to 127 bytes of data (payload) that a message can hold
	private byte[] data;

	// construction a Message with the data provided
	public Message(byte[] data) {

		//Check for "input is null"
		if(data == null){
			throw new NullPointerException("Input is null. Has to be an array of bytes. Min size = 1, Max size = 127");
		}

		//Check for "input data is too large for the message".
		if(data.length > 127) {
			throw new UnsupportedOperationException("Input size is " + data.length + " bytes. Min size = 1, Max size = 127");
		}

		// If length is OK, creates a message.
		this.data = data;

	}

	public byte[] getData() {
		return this.data; 
	}

	public int getMessageLength(){
		return this.data.length;
	}

}
