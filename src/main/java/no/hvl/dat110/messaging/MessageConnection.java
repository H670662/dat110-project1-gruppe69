package no.hvl.dat110.messaging;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import no.hvl.dat110.TODO;


public class MessageConnection {

	private DataOutputStream outStream; // for writing bytes to the underlying TCP connection
	private DataInputStream inStream; // for reading bytes from the underlying TCP connection
	private Socket socket; // socket for the underlying TCP connection
	
	public MessageConnection(Socket socket) {

		try {

			this.socket = socket;

			outStream = new DataOutputStream(socket.getOutputStream());

			inStream = new DataInputStream(socket.getInputStream());

		} catch (IOException ex) {

			System.out.println("Connection: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void send(Message message) throws IOException {

		// Check for null input
		if(message == null){
			throw new NullPointerException("Message is null");
		}

		//Encapsulating the message
		byte[] data = MessageUtils.encapsulate(message);

		//Sending encapsulated message to the destination port
		outStream.write(data);


	}

	public Message receive() throws IOException {

		// read a segment from the input stream and decapsulate data into a Message

		// Reads the segment from the port and saves it into a buffer
		byte[] data = new byte[128];
		inStream.read(data);

		// Decapsulates the data and returns a message
        return MessageUtils.decapsulate(data);
		
	}

	// close the connection by closing streams and the underlying socket	
	public void close() {

		try {
			
			outStream.close();
			inStream.close();

			socket.close();
			
		} catch (IOException ex) {

			System.out.println("Connection: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}