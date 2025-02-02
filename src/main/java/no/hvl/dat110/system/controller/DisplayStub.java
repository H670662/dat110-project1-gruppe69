package no.hvl.dat110.system.controller;

import no.hvl.dat110.TODO;
import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.rpc.*;

public class DisplayStub extends RPCLocalStub {

    public DisplayStub(RPCClient rpcclient) {
        super(rpcclient);
    }

    public void write(String message) {

        // TODO - START
        byte[] request = RPCUtils.marshallString(message);
        byte[] reply = rpcclient.call((byte) Common.WRITE_RPCID, request);
        RPCUtils.unmarshallVoid(reply);
        // TODO - END

    }
}
