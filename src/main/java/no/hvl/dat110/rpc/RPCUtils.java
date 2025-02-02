package no.hvl.dat110.rpc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import no.hvl.dat110.TODO;

public class RPCUtils {
	
	public static byte[] encapsulate(byte rpcid, byte[] payload) {
		
		byte[] rpcmsg = null;
		
		// TODO - START
		// Encapsulate the rpcid and payload in a byte array according to the RPC message syntax / format
		rpcmsg = new byte[payload.length + 1];
		rpcmsg[0] = rpcid;
		for (int i = 0; i < payload.length; i++) {
			rpcmsg[i + 1] = payload[i];
		}


		/*
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		*/
		// TODO - END
		
		return rpcmsg;
	}
	
	public static byte[] decapsulate(byte[] rpcmsg) {
		
		byte[] payload = null;
		
		// TODO - START
		// Decapsulate the rpcid and payload in a byte array according to the RPC message syntax
		if(rpcmsg.length < 1) {
			throw new IllegalArgumentException("Rpc message kan ikke være null");
		} else {
			payload = new byte[rpcmsg.length - 1];
			for (int i = 1; i < payload.length + 1; i++) {
				payload[i - 1] = rpcmsg[i];
			}
		}

		/*
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		*/
		// TODO - END
		
		return payload;
		
	}

	// convert String to byte array
	public static byte[] marshallString(String str) {
		
		byte[] encoded = null;
		
		// TODO - START 
		encoded = str.getBytes();
		System.out.println(str + "  " + Arrays.toString(encoded));


		// throw new UnsupportedOperationException(TODO.method());
		// TODO - END
		
		return encoded;
	}

	// convert byte array to a String
	public static String unmarshallString(byte[] data) {
		
		String decoded = null;
		// TODO - START
		decoded = new String(data);


		//throw new UnsupportedOperationException(TODO.method());
		// TODO - END
		
		return decoded;
	}
	
	public static byte[] marshallVoid() {
		
		byte[] encoded = null;
		
		// TODO - START
		// Står ingenting om hva som egt. skal skje
		//dette er AI's jobbb
        encoded = new byte[0];
		
		//throw new UnsupportedOperationException(TODO.method());
				
		// TODO - END
		
		return encoded;
		
	}
	
	public static void unmarshallVoid(byte[] data) {
		
		// TODO
		//forstår ikke hvordan man marshaller void, stjålet fra AI
		if (data.length != 0) {
			throw new IllegalArgumentException("Received data is not a void");
		}
		//throw new UnsupportedOperationException(TODO.method());
	}

	// convert boolean to a byte array representation
	public static byte[] marshallBoolean(boolean b) {
		
		byte[] encoded = new byte[1];
				
		if (b) {
			encoded[0] = 1;
		} else
		{
			encoded[0] = 0;
		}
		
		return encoded;
	}

	// convert byte array to a boolean representation
	public static boolean unmarshallBoolean(byte[] data) {
		
		return (data[0] > 0);
		
	}

	// integer to byte array representation
	public static byte[] marshallInteger(int x) {
		
		byte[] encoded = null;
		
		// TODO - START
	//	encoded = Integer.toString(x).getBytes();
	//	System.out.println(x + "  " + Arrays.toString(encoded));

		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		buffer.putInt(x);
		encoded = buffer.array();
		//System.out.println(x + "  " + Arrays.toString(encoded));

		// throw new UnsupportedOperationException(TODO.method());
		// TODO - END
		
		return encoded;
	}
	
	// byte array representation to integer
	public static int unmarshallInteger(byte[] data) {
		
		int decoded = 0;
		
		// TODO - START 
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		buffer.put(data);
        buffer.flip();
        decoded = buffer.getInt();
		//System.out.println(decoded + "  " + Arrays.toString(data));

		//throw new UnsupportedOperationException(TODO.method());
		// TODO - END
		
		return decoded;
		
	}
}
