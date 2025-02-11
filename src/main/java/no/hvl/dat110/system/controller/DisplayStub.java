package no.hvl.dat110.system.controller;

import no.hvl.dat110.TODO;
import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.rpc.*;

public class DisplayStub extends RPCLocalStub {

	public DisplayStub(RPCClient rpcclient) {
		super(rpcclient);
	}
	
	public void write (String message) {
		
		// TODO - START DONE
		
		// implement marshalling, call and unmarshalling for write RPC method

		byte rpcid = (byte) Common.WRITE_RPCID;

		byte[] request = RPCUtils.marshallString(message);

		rpcclient.call(rpcid, request);

		// TODO - END
		
	}
}
