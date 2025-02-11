package no.hvl.dat110.messaging;

import java.util.Arrays;

import no.hvl.dat110.TODO;

public class MessageUtils {

	public static final int SEGMENTSIZE = 128;

	public static int MESSAGINGPORT = 8080;
	public static String MESSAGINGHOST = "localhost";

	public static byte[] encapsulate(Message message) {

		// Check for null input.
		if(message == null){
			throw new NullPointerException("Message is null");
		}

		/*
			Segment, byte[128], consists of:
				Header: placed at segment[0]: an int from 0 to 127.
				Payload data: message data, starts at segment[1] and up to the message length.
				Padding: the rest of the segment array after the message. null pointers -> be cautious.
		 */

		// Declares a segment.
		byte[] segment = new byte[SEGMENTSIZE];


		// Declares elements of segment (header, payload data) based on the input message.
		byte header = (byte) message.getMessageLength();
		byte[] data = message.getData();


		// Assembles the segment with header on segment[0] and payload data starting on segment[1].
		segment[0] = header;
		System.arraycopy(data, 0, segment, 1, message.getMessageLength());


		// Returns the formatted segment.
		return segment;
		
	}

	public static Message decapsulate(byte[] segment) {

		//Check for null input.
		if (segment == null) {
			throw new NullPointerException("Segment is null");
		}

		// Creates an array of bytes with length based on the segment header.
		byte[] data = new byte[segment[0]];

		// Extracts payload data from the segment.
		System.arraycopy(segment, 1, data, 0, segment[0]);

		// Creates and returns a message with the data from the input segment.
		return new Message(data);
		
	}
	
}
