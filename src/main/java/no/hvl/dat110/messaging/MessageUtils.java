package no.hvl.dat110.messaging;

import java.util.Arrays;

import no.hvl.dat110.TODO;

public class MessageUtils {

    public static final int SEGMENTSIZE = 128;

    public static int MESSAGINGPORT = 8080;
    public static String MESSAGINGHOST = "localhost";

    public static byte[] encapsulate(Message message) {
        byte[] data = message.getData();
        byte[] segment = new byte[128];


        segment[0] = (byte) data.length;

        System.arraycopy(data, 0, segment, 1, data.length);
        return segment;
    }

    public static Message decapsulate(byte[] segment) {

        Message message = null;

        // TODO - START
        // decapsulate segment and put received payload data into a message



        // TODO - END

        return message;

    }

}
