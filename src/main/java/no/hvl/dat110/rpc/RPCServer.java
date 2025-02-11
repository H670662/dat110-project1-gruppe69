package no.hvl.dat110.rpc;

import java.io.IOException;
import java.util.HashMap;

import no.hvl.dat110.TODO;
import no.hvl.dat110.messaging.MessageConnection;
import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.messaging.MessageUtils;
import no.hvl.dat110.messaging.MessagingServer;

public class RPCServer {

	private MessagingServer msgserver;
	private MessageConnection connection;
	
	// hashmap to register RPC methods which are required to extend RPCRemoteImpl
	// the key in the hashmap is the RPC identifier of the method
	private HashMap<Byte,RPCRemoteImpl> services;
	
	public RPCServer(int port) {
		
		this.msgserver = new MessagingServer(port);
		this.services = new HashMap<Byte,RPCRemoteImpl>();
		
	}
	
	public void run() {
		
		// the stop RPC method is built into the server
		RPCRemoteImpl rpcstop = new RPCServerStopImpl(RPCCommon.RPIDSTOP,this);
		
		System.out.println("RPC SERVER RUN - Services: " + services.size());
			
		connection = msgserver.accept(); 
		
		System.out.println("RPC SERVER ACCEPTED");
		
		boolean stop = false;
		
		while (!stop) {
	    
		   byte rpcid = 0;
		   Message requestmsg = null, replymsg = null;
		   
		   // TODO - START
		   // - receive a Message containing an RPC request
            try {
                requestmsg = connection.receive();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // - extract the identifier for the RPC method to be invoked from the RPC request
            assert requestmsg != null;
            byte[] rpcRequest = requestmsg.getData();
			rpcid = rpcRequest[0];

		   // - extract the method's parameter by decapsulating using the RPCUtils
			byte[] rpcParams = RPCUtils.decapsulate(rpcRequest);

		   // - lookup the method to be invoked
			RPCRemoteImpl rpcService = services.get(rpcid);

		   // - invoke the method and pass the param
			byte[] reply;

			if (rpcService != null) {
				// Invoke the RPC method with parameters
				reply = rpcService.invoke(rpcParams);
			} else {
				// Return empty response if unknown ID
				reply = RPCUtils.marshallVoid();
			}

		   // - encapsulate return value
			replymsg = new Message(RPCUtils.encapsulate(rpcid, reply));


		   // - send back the message containing the RPC reply
            try {
                connection.send(replymsg);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO - END

			// stop the server if it was stop methods that was called
		   if (rpcid == RPCCommon.RPIDSTOP) {
			   stop = true;
		   }
		}
	
	}
	
	// used by server side method implementations to register themselves in the RPC server
	public void register(byte rpcid, RPCRemoteImpl impl) {
		services.put(rpcid, impl);
	}
	
	public void stop() {

		if (connection != null) {
			connection.close();
		} else {
			System.out.println("RPCServer.stop - connection was null");
		}
		
		if (msgserver != null) {
			msgserver.stop();
		} else {
			System.out.println("RPCServer.stop - msgserver was null");
		}
		
	}
}
